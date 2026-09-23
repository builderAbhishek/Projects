package com.ais.swaadpe.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ais.swaadpe.data.api.SwaadPeApi
import com.ais.swaadpe.data.local.dao.CartDao
import com.ais.swaadpe.data.local.entity.CartEntity
import com.ais.swaadpe.data.local.prefs.PreferenceManager
import com.ais.swaadpe.data.remote.dto.*
import com.ais.swaadpe.domain.model.MenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartItem(
    val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?,
    val vendorId: Int,
    val vendorName: String?
)

data class CartConflict(
    val item: MenuItem,
    val vendorId: Int,
    val vendorName: String?
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val api: SwaadPeApi,
    private val cartDao: CartDao,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val userId = preferenceManager.getUserId()
    private var syncJob: Job? = null

    private val _cartConflict = MutableSharedFlow<CartConflict?>()
    val cartConflict = _cartConflict.asSharedFlow()

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    val cartItems: StateFlow<List<CartItem>> = cartDao.getAllCartItems()
        .map { entities ->
            entities.map {
                CartItem(it.id, it.name, it.price, it.quantity, it.imageUrl, it.vendorId, it.vendorName)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subtotal: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _billSummary = MutableStateFlow<BillSummaryDto?>(null)
    val billSummary: StateFlow<BillSummaryDto?> = _billSummary.asStateFlow()

    private val _availableCoupons = MutableStateFlow<List<CouponDto>>(emptyList())
    val availableCoupons: StateFlow<List<CouponDto>> = _availableCoupons.asStateFlow()

    private val _deliveryInfo = MutableStateFlow<DeliveryInfoDto?>(null)
    val deliveryInfo: StateFlow<DeliveryInfoDto?> = _deliveryInfo.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _orderStatus = MutableStateFlow<OrderStatus>(OrderStatus.Idle)
    val orderStatus: StateFlow<OrderStatus> = _orderStatus.asStateFlow()

    private val _addresses = MutableStateFlow<List<AddressDto>>(emptyList())
    val addresses: StateFlow<List<AddressDto>> = _addresses.asStateFlow()

    private val _selectedAddress = MutableStateFlow<AddressDto?>(null)
    val selectedAddress: StateFlow<AddressDto?> = _selectedAddress.asStateFlow()

    private val _receiverName = MutableStateFlow("")
    val receiverName: StateFlow<String> = _receiverName.asStateFlow()

    private val _receiverPhone = MutableStateFlow("")
    val receiverPhone: StateFlow<String> = _receiverPhone.asStateFlow()

    init {
        fetchAddresses()
    }

    fun fetchCoupons() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                val response = api.getCoupons(subtotal.value)
                if (response.isSuccessful && response.body()?.status == true) {
                    _availableCoupons.value = response.body()?.data?.availableCoupons ?: emptyList()
                }
            } catch (e: Exception) { } finally {
                _isSyncing.value = false
            }
        }
    }

    fun applyCoupon(code: String?) {
        viewModelScope.launch {
            _isSyncing.value = true
            performFullServerSync(code)
            _isSyncing.value = false
        }
    }

    fun removeCoupon() {
        applyCoupon(null)
    }

    fun addToCart(item: MenuItem, vendorId: Int, vendorName: String?, isOpen: Boolean = true, forceAdd: Boolean = false) {
        viewModelScope.launch {
            if (!item.isAvailable && !forceAdd) {
                _uiMessage.emit("Item '${item.name}' is currently out of stock")
                return@launch
            }
            
            if (!isOpen && !forceAdd) {
                _uiMessage.emit("Restaurant '$vendorName' is currently not accepting orders")
                return@launch
            }

            val currentItems = cartItems.value
            
            // Check for restaurant conflict
            if (!forceAdd && currentItems.isNotEmpty() && currentItems.any { it.vendorId != vendorId }) {
                _cartConflict.emit(CartConflict(item, vendorId, vendorName))
                return@launch
            }

            val existing: CartEntity? = cartDao.getItemById(item.id)
            if (existing != null) {
                cartDao.insertItem(existing.copy(quantity = existing.quantity + 1))
            } else {
                cartDao.insertItem(CartEntity(item.id, item.name, item.price, 1, item.imageUrl, vendorId, vendorName))
                _uiMessage.emit("Added ${item.name} to cart")
            }
            triggerSync()
        }
    }

    fun clearAndAddToCart(conflict: CartConflict) {
        viewModelScope.launch {
            cartDao.clearCart()
            _cartConflict.emit(null)
            addToCart(conflict.item, conflict.vendorId, conflict.vendorName, forceAdd = true)
        }
    }

    fun resetConflict() {
        viewModelScope.launch {
            _cartConflict.emit(null)
        }
    }

    fun removeFromCart(itemId: Int) {
        viewModelScope.launch {
            val existing: CartEntity? = cartDao.getItemById(itemId)
            if (existing != null) {
                if (existing.quantity > 1) {
                    cartDao.insertItem(existing.copy(quantity = existing.quantity - 1))
                } else {
                    cartDao.deleteById(itemId)
                    _uiMessage.emit("${existing.name} removed from cart")
                }
            }
            triggerSync()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartDao.clearCart()
            _billSummary.value = null
            _deliveryInfo.value = null
            _uiMessage.emit("Cart cleared")
        }
    }

    fun triggerSync() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            delay(500)
            performFullServerSync(_billSummary.value?.couponCode)
        }
    }

    private suspend fun performFullServerSync(couponCode: String? = null) {
        if (cartItems.value.isEmpty()) {
            _billSummary.value = null
            return
        }

        try {
            val syncRequest = CartSyncRequest(
                userId = userId, 
                items = cartItems.value.map { CartSyncItemDto(it.id, it.quantity, it.vendorId) }
            )
            val syncResponse = api.syncCart(syncRequest)

            if (syncResponse.isSuccessful && syncResponse.body()?.status == true) {
                val addressId = _selectedAddress.value?.id
                if (addressId != null) {
                    val checkoutRequest = CheckoutRequest(
                        userId = userId,
                        addressId = addressId,
                        couponCode = couponCode
                    )
                    val checkoutResponse = api.checkout(checkoutRequest)
                    if (checkoutResponse.isSuccessful && checkoutResponse.body()?.status == true) {
                        _billSummary.value = checkoutResponse.body()?.data?.billSummary
                        _deliveryInfo.value = checkoutResponse.body()?.data?.deliveryInfo
                    }
                } else {
                    val summary = syncResponse.body()?.data?.cartSummary
                    if (summary != null) {
                        _billSummary.value = BillSummaryDto(
                            itemTotal = summary.itemTotal,
                            itemSavings = summary.itemSavings,
                            packagingCharges = 0.0,
                            deliveryFee = 0.0,
                            deliveryDiscount = 0.0,
                            platformFee = 0.0,
                            gstRate = 5.0,
                            gstTaxes = 0.0,
                            couponApplied = couponCode != null,
                            couponCode = couponCode,
                            couponDiscount = 0.0,
                            couponMessage = if(couponCode != null) "Select address to verify coupon" else null,
                            grandTotal = summary.itemTotal,
                            roundOff = 0.0,
                            toPay = summary.itemTotal.toInt(),
                            savingsMessage = if(summary.itemSavings > 0) "Saved ₹${summary.itemSavings} on items" else null
                        )
                    }
                }
            } else if (syncResponse.isSuccessful && syncResponse.body()?.status == false) {
                _deliveryInfo.value = DeliveryInfoDto(
                    estimatedTime = "Not Available",
                    distanceKm = 0.0,
                    distanceDisplay = "",
                    isDeliverable = false
                )
            }
        } catch (e: Exception) { }
    }

    fun getTotalPrice(): Double {
        return cartItems.value.sumOf { it.price * it.quantity }
    }

    fun syncCartWithServer() {
        triggerSync()
    }

    fun fetchAddresses() {
        viewModelScope.launch {
            try {
                val response = api.getAddresses(userId = userId)
                if (response.isSuccessful && response.body()?.status == true) {
                    val addressList = response.body()?.data ?: emptyList()
                    _addresses.value = addressList
                    if (_selectedAddress.value == null && addressList.isNotEmpty()) {
                        selectAddress(addressList.first())
                    } else {
                        triggerSync()
                    }
                }
            } catch (e: Exception) { }
        }
    }

    fun selectAddress(address: AddressDto) {
        _selectedAddress.value = address
        _receiverName.value = address.receiverName ?: ""
        _receiverPhone.value = address.receiverPhone ?: ""
        triggerSync()
    }

    fun updateReceiverDetails(name: String, phone: String) {
        val currentId = _selectedAddress.value?.id ?: return
        viewModelScope.launch {
            _receiverName.value = name
            _receiverPhone.value = phone
            try {
                api.updateReceiver(UpdateReceiverRequest(currentId, name, phone))
            } catch (e: Exception) { }
        }
    }

    fun addNewAddress(tag: String, fullAddress: String, houseNo: String?, lat: Double, lng: Double, receiverName: String?, receiverPhone: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.saveAddress(AddAddressRequest(userId, tag, fullAddress, houseNo, lat, lng, receiverName, receiverPhone))
                if (response.isSuccessful && response.body()?.status == true) {
                    fetchAddresses()
                    onSuccess()
                }
            } catch (e: Exception) { }
        }
    }

    fun placeOrder(address: String, paymentMethod: String) {
        val firstItem = cartItems.value.firstOrNull() ?: return
        viewModelScope.launch {
            _orderStatus.value = OrderStatus.Loading
            try {
                val request = OrderRequest(
                    userId = userId,
                    vendorId = firstItem.vendorId,
                    itemTotal = _billSummary.value?.itemTotal ?: cartItems.value.sumOf { it.price * it.quantity },
                    deliveryFee = _billSummary.value?.deliveryFee ?: 0.0,
                    grandTotal = _billSummary.value?.toPay?.toDouble() ?: cartItems.value.sumOf { it.price * it.quantity },
                    address = address,
                    paymentMethod = paymentMethod,
                    items = cartItems.value.map { OrderItemDto(it.id, it.name, it.quantity, it.price) }
                )
                val response = api.placeOrder(request)
                if (response.isSuccessful && response.body()?.status == true) {
                    _orderStatus.value = OrderStatus.Success(response.body()?.orderNumber ?: "")
                    clearCart()
                } else {
                    _orderStatus.value = OrderStatus.Error(response.body()?.message ?: "Failed to place order")
                }
            } catch (e: Exception) {
                _orderStatus.value = OrderStatus.Error(e.message ?: "Network error")
            }
        }
    }
}
