package com.ais.swaadpe.presentation.screens.cart

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ais.swaadpe.R
import com.ais.swaadpe.data.remote.dto.AddressDto
import com.ais.swaadpe.domain.model.MenuItem
import com.ais.swaadpe.presentation.viewmodels.CartItem
import com.ais.swaadpe.presentation.viewmodels.CartViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    onAddNewAddressClick: () -> Unit,
    onOffersClick: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val billSummary by viewModel.billSummary.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val deliveryInfo by viewModel.deliveryInfo.collectAsState()
    
    val receiverName by viewModel.receiverName.collectAsState()
    val receiverPhone by viewModel.receiverPhone.collectAsState()

    val cartConflict by viewModel.cartConflict.collectAsState(initial = null)

    var showAddressSheet by remember { mutableStateOf(false) }
    var showReceiverSheet by remember { mutableStateOf(false) }
    var showBillSheet by remember { mutableStateOf(false) }
    
    val couponCode = billSummary?.couponCode ?: ""
    val couponMessage = billSummary?.couponMessage ?: ""
    val isCouponApplied = billSummary?.couponApplied ?: false

    val sheetState = rememberModalBottomSheetState()
    val receiverSheetState = rememberModalBottomSheetState()
    val billSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val totalPay = billSummary?.toPay ?: 0
    val themeColor = Color(0xFF3BB742)
    val backgroundColor = Color(0xFFF7F9F7)

    val isDeliverable = deliveryInfo?.isDeliverable ?: true

    LaunchedEffect(Unit) {
        viewModel.syncCartWithServer()
    }

    if (cartConflict != null) {
        AlertDialog(
            onDismissRequest = { viewModel.resetConflict() },
            title = { Text("Replace cart items?") },
            text = { Text("Your cart contains items from another restaurant. Do you want to clear the cart and add items from ${cartConflict?.vendorName} instead?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearAndAddToCart(cartConflict!!) },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                ) {
                    Text("Replace", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetConflict() }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(text = "My Cart", style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black))
                            if (cartItems.isNotEmpty()) {
                                Text(
                                    text = "${cartItems.size} Items from ${cartItems.firstOrNull()?.vendorName ?: "Restaurant"}",
                                    style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Black)
                        }
                    },
                    actions = {
                        if (cartItems.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearCart() }) {
                                Icon(Icons.Default.DeleteOutline, "Clear", tint = Color.Red.copy(0.7f))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                if (cartItems.isNotEmpty()) {
                    CheckoutBottomBar(
                        total = totalPay,
                        isAddressSelected = selectedAddress != null,
                        isDeliverable = isDeliverable,
                        themeColor = themeColor,
                        onPlaceOrder = {
                            if (selectedAddress == null) showAddressSheet = true
                            else onCheckoutClick()
                        },
                        onShowBill = { showBillSheet = true }
                    )
                }
            },
            containerColor = backgroundColor
        ) { paddingValues ->
            if (cartItems.isEmpty()) {
                EmptyCartView(onBackClick, themeColor)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { VendorHeaderCard(cartItems.firstOrNull()?.vendorName ?: "Restaurant", themeColor) }

                    items(cartItems) { item ->
                        ModernCartItemRow(
                            item = item,
                            isAvailable = isDeliverable,
                            themeColor = themeColor,
                            onAdd = { viewModel.addToCart(MenuItem(item.id, item.name, null, item.price, item.imageUrl, true), item.vendorId, item.vendorName) },
                            onRemove = { viewModel.removeFromCart(item.id) }
                        )
                    }

                    item {
                        CouponSection(
                            code = couponCode, 
                            message = couponMessage,
                            isApplied = isCouponApplied,
                            onRemove = { viewModel.removeCoupon() },
                            onClick = onOffersClick
                        )
                    }

                    item {
                        DeliveryAddressCard(
                            selectedAddress = selectedAddress,
                            deliveryInfo = deliveryInfo,
                            receiverName = receiverName,
                            receiverPhone = receiverPhone,
                            themeColor = themeColor,
                            onEditAddress = { showAddressSheet = true },
                            onEditReceiver = { showReceiverSheet = true }
                        )
                    }

                    item { DetailedBillCard(billSummary = billSummary, onShowDetails = { showBillSheet = true }) }

                    item { CancellationPolicy() }

                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }

        if (showAddressSheet) {
            ModalBottomSheet(onDismissRequest = { showAddressSheet = false }, sheetState = sheetState, dragHandle = null, containerColor = Color(0xFFF7F8FA)) {
                val addresses by viewModel.addresses.collectAsState()
                AddressSelectionContent(
                    addresses = addresses,
                    selectedAddressId = selectedAddress?.id,
                    themeColor = themeColor,
                    onAddressSelected = {
                        viewModel.selectAddress(it)
                        scope.launch { sheetState.hide() }.invokeOnCompletion { showAddressSheet = false } 
                    },
                    onAddNewAddressClick = { 
                        scope.launch { sheetState.hide() }.invokeOnCompletion { showAddressSheet = false; onAddNewAddressClick() } 
                    },
                    onClose = { scope.launch { sheetState.hide() }.invokeOnCompletion { showAddressSheet = false } }
                )
            }
        }

        if (showReceiverSheet) {
            ModalBottomSheet(onDismissRequest = { showReceiverSheet = false }, sheetState = receiverSheetState, dragHandle = null, containerColor = Color.White) {
                UpdateReceiverContent(
                    currentName = receiverName,
                    currentPhone = receiverPhone,
                    currentAddress = selectedAddress?.fullAddress ?: "",
                    themeColor = themeColor,
                    onSubmit = { n, p ->
                        viewModel.updateReceiverDetails(n, p)
                        scope.launch { receiverSheetState.hide() }.invokeOnCompletion { showReceiverSheet = false } 
                    },
                    onClose = { scope.launch { receiverSheetState.hide() }.invokeOnCompletion { showReceiverSheet = false } }
                )
            }
        }

        if (showBillSheet) {
            ModalBottomSheet(onDismissRequest = { showBillSheet = false }, sheetState = billSheetState, dragHandle = null, containerColor = Color.White) {
                BillSummaryBottomSheetContent(
                    billSummary = billSummary,
                    themeColor = themeColor,
                    onClose = { scope.launch { billSheetState.hide() }.invokeOnCompletion { showBillSheet = false } }
                )
            }
        }
    }
}

@Composable
fun VendorHeaderCard(vendorName: String, themeColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(10.dp), color = themeColor.copy(0.1f)) {
            Icon(Icons.Default.Storefront, null, modifier = Modifier.padding(8.dp), tint = themeColor)
        }
        Spacer(Modifier.width(12.dp))
        Text(text = vendorName, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black))
    }
}

@Composable
fun ModernCartItemRow(item: CartItem, isAvailable: Boolean, themeColor: Color, onAdd: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.5.dp)
    ) {
        Column {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = item.imageUrl, contentDescription = null,
                    modifier = Modifier.size(75.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.logo)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).border(1.dp, Color(0xFF4CAF50)).padding(2.dp)) {
                            Box(modifier = Modifier.size(5.dp).background(Color(0xFF4CAF50), CircleShape))
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(text = item.name, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(text = "₹${item.price.toInt()}", style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.Black))
                }
                Surface(shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, Color(0xFFEFEFEF)), color = Color.White) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
                        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Remove, null, tint = themeColor, modifier = Modifier.size(18.dp)) }
                        Text("${item.quantity}", style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.Black), modifier = Modifier.padding(horizontal = 6.dp))
                        IconButton(onClick = onAdd, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Add, null, tint = themeColor, modifier = Modifier.size(18.dp)) }
                    }
                }
            }
            if (!isAvailable) {
                Text(
                    text = "Not Delivered: This item is currently unavailable",
                    color = Color.Red,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
fun CouponSection(code: String, message: String, isApplied: Boolean, onRemove: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isApplied) Color(0xFFE8F5E9) else Color.White),
        elevation = CardDefaults.cardElevation(0.5.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isApplied) Icons.Default.CheckCircle else Icons.Outlined.LocalOffer, 
                null, 
                tint = if (isApplied) Color(0xFF2E7D32) else Color(0xFF2E7D32), 
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isApplied) "Coupon Applied" else "Use Coupons",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                )
                if (isApplied) {
                    Text(text = "Code: $code", style = TextStyle(fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold))
                } else if (message.isNotEmpty()) {
                    Text(text = message, style = TextStyle(fontSize = 11.sp, color = if (message.contains("Add")) Color.Red else Color.Gray))
                } else {
                    Text(text = "Save more with available offers", style = TextStyle(fontSize = 11.sp, color = Color.Gray))
                }
            }
            if (isApplied) {
                Text(
                    text = "REMOVE", 
                    modifier = Modifier.clickable { onRemove() }.padding(8.dp),
                    style = TextStyle(color = Color.Red, fontWeight = FontWeight.Black, fontSize = 12.sp)
                )
            } else {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
            }
        }
    }
}

@Composable
fun DeliveryAddressCard(
    selectedAddress: AddressDto?,
    deliveryInfo: com.ais.swaadpe.data.remote.dto.DeliveryInfoDto?,
    receiverName: String,
    receiverPhone: String,
    themeColor: Color,
    onEditAddress: () -> Unit,
    onEditReceiver: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Timer, null, tint = Color(0xFF1DA624), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = "Delivery in ${deliveryInfo?.estimatedTime ?: "Calculating..."}", style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.Black))
                if (deliveryInfo?.distanceDisplay != null) {
                    Text(text = " • ${deliveryInfo.distanceDisplay}", style = TextStyle(color = Color.Gray, fontSize = 13.sp))
                }
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Icon(Icons.Outlined.LocationOn, null, tint = Color(0xFFE53935), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Deliver to ${selectedAddress?.addressTag ?: "Select Address"}", style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.Black))
                    Text(text = selectedAddress?.fullAddress ?: "Add address for delivery details", style = TextStyle(fontSize = 12.sp, color = Color.Gray), maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Text(text = "CHANGE", style = TextStyle(color = themeColor, fontWeight = FontWeight.Bold, fontSize = 12.sp), modifier = Modifier.clickable { onEditAddress() }.padding(4.dp))
            }
            if (selectedAddress != null) {
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Person, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(text = if (receiverName.isNotEmpty()) "$receiverName, $receiverPhone" else "Add Contact Details", style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black), modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Edit, null, tint = Color.Gray, modifier = Modifier.size(16.dp).clickable { onEditReceiver() } )
                }
            }
        }
    }
}

@Composable
fun DetailedBillCard(billSummary: com.ais.swaadpe.data.remote.dto.BillSummaryDto?, onShowDetails: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.5.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Bill Summary", style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.Black))
            Spacer(Modifier.height(12.dp))
            val itemTotal = billSummary?.itemTotal ?: 0.0
            val delivery = billSummary?.deliveryFee ?: 0.0
            val coupon = billSummary?.couponDiscount ?: 0.0
            BillRowSimplified("Item Total", "₹$itemTotal")
            if (delivery > 0) BillRowSimplified("Delivery Fee", "₹$delivery")
            if (coupon > 0) BillRowSimplified("Coupon Discount", "-₹$coupon", isHighlight = true)
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "To Pay", style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.Black))
                Text(text = "₹${billSummary?.toPay ?: 0}", style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black))
            }
            Text(text = "VIEW DETAILED BILL", style = TextStyle(color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 11.sp), modifier = Modifier.padding(top = 12.dp).clickable { onShowDetails() } )
        }
    }
}

@Composable
fun BillRowSimplified(label: String, value: String, isHighlight: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = TextStyle(fontSize = 13.sp, color = Color.Gray))
        Text(text = value, style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (isHighlight) Color(0xFF2E7D32) else Color.Black))
    }
}

@Composable
fun CancellationPolicy() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        Text(text = "Cancellation Policy", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray))
        Spacer(Modifier.height(4.dp))
        Text(text = "Orders cannot be cancelled once packed. For help, contact support.", style = TextStyle(fontSize = 11.sp, color = Color.LightGray, lineHeight = 16.sp))
    }
}

@Composable
fun EmptyCartView(onBackClick: () -> Unit, themeColor: Color) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.size(100.dp).alpha(0.1f))
        Spacer(Modifier.height(24.dp))
        Text("Your cart is empty", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.Gray))
        Spacer(Modifier.height(24.dp))
        Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = themeColor), shape = RoundedCornerShape(12.dp)) { Text("Add items from Menu", fontWeight = FontWeight.Bold, color = Color.White) }
    }
}

@Composable
fun CheckoutBottomBar(total: Int, isAddressSelected: Boolean, isDeliverable: Boolean, themeColor: Color, onPlaceOrder: () -> Unit, onShowBill: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().shadow(16.dp), color = Color.White) {
        Row(modifier = Modifier.padding(16.dp).navigationBarsPadding(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.clickable { onShowBill() }) {
                Text(text = "₹$total", style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color.Black))
                Text(text = "VIEW DETAILED BILL", style = TextStyle(fontSize = 11.sp, color = themeColor, fontWeight = FontWeight.Bold))
            }
            Button(
                onClick = onPlaceOrder, enabled = isDeliverable, modifier = Modifier.height(52.dp).fillMaxWidth(0.7f),
                shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = if (isAddressSelected) themeColor else Color.Black)
            ) {
                Text(
                    text = if (!isDeliverable) "Not Deliverable" else if (isAddressSelected) "Place Order" else "Select Address",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                    color = if (!isDeliverable) Color.Black else Color.White
                )
            }
        }
    }
}

@Composable
fun AddressSelectionContent(addresses: List<AddressDto>, selectedAddressId: Int?, themeColor: Color, onAddressSelected: (AddressDto) -> Unit, onAddNewAddressClick: () -> Unit, onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Delivery Address", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black))
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, "Close") }
        }
        Spacer(Modifier.height(16.dp))
        Surface(modifier = Modifier.fillMaxWidth().clickable { onAddNewAddressClick() }, shape = RoundedCornerShape(12.dp), color = Color.White, border = BorderStroke(1.dp, Color(0xFFF0F0F0))) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, null, tint = themeColor)
                Spacer(Modifier.width(12.dp))
                Text("Add New Address", style = TextStyle(fontWeight = FontWeight.Bold, color = themeColor))
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f, fill = false)) {
            items(addresses) { address ->
                val isSelected = address.id == selectedAddressId
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onAddressSelected(address) },
                    shape = RoundedCornerShape(16.dp),
                    border = if (isSelected) BorderStroke(2.dp, themeColor) else null,
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) themeColor.copy(0.05f) else Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(if (address.addressTag.lowercase() == "home") Icons.Default.Home else Icons.Default.Work, null, tint = if (isSelected) themeColor else Color.Gray)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(text = address.addressTag, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black))
                            Text(text = address.fullAddress, style = TextStyle(fontSize = 12.sp, color = Color.Gray))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateReceiverContent(currentName: String, currentPhone: String, currentAddress: String, themeColor: Color, onSubmit: (String, String) -> Unit, onClose: () -> Unit) {
    var name by remember { mutableStateOf(currentName) }
    var phone by remember { mutableStateOf(currentPhone) }
    
    // Validation logic
    val isNameValid = name.trim().isNotEmpty()
    val isPhoneValid = phone.trim().length == 10 && phone.all { it.isDigit() }
    val isFormValid = isNameValid && isPhoneValid

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
        Text(text = "Update Delivery Details", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black))
        Spacer(Modifier.height(8.dp))
        Text(text = currentAddress, style = TextStyle(fontSize = 12.sp, color = Color.Gray))
        Spacer(Modifier.height(24.dp))
        
        OutlinedTextField(
            value = name, 
            onValueChange = { if (!it.contains("\n")) name = it }, // Single line restriction
            label = { Text("Receiver Name") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = name.isNotEmpty() && !isNameValid,
            textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Medium),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = themeColor, 
                focusedLabelColor = themeColor,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        Spacer(Modifier.height(16.dp))
        
        OutlinedTextField(
            value = phone, 
            onValueChange = { 
                if (it.length <= 10 && it.all { char -> char.isDigit() }) phone = it 
            }, // Numeric & 10 digit restriction
            label = { Text("Receiver Phone") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = phone.isNotEmpty() && !isPhoneValid,
            textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Medium),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = themeColor, 
                focusedLabelColor = themeColor,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        
        if (phone.isNotEmpty() && phone.length < 10) {
            Text(text = "Enter a valid 10-digit number", color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(start = 8.dp, top = 4.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { if (isFormValid) onSubmit(name.trim(), phone.trim()) }, 
            enabled = isFormValid, // Disable button if validation fails
            modifier = Modifier.fillMaxWidth().height(54.dp), 
            shape = RoundedCornerShape(14.dp), 
            colors = ButtonDefaults.buttonColors(
                containerColor = themeColor,
                disabledContainerColor = Color.LightGray
            )
        ) { 
            Text("Save & Continue", fontWeight = FontWeight.Bold, color = Color.White) 
        }
    }
}

@Composable
fun BillSummaryBottomSheetContent(billSummary: com.ais.swaadpe.data.remote.dto.BillSummaryDto?, themeColor: Color, onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Detailed Bill", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black))
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, null) }
        }
        Spacer(Modifier.height(24.dp))
        if (billSummary != null) {
            BillDetailRow("Item Total", "₹${billSummary.itemTotal}")
            BillDetailRow("Packaging Charges", "₹${billSummary.packagingCharges}")
            BillDetailRow("Delivery Fee", "₹${billSummary.deliveryFee}")
            BillDetailRow("Platform Fee", "₹${billSummary.platformFee}")
            BillDetailRow("GST Taxes", "₹${billSummary.gstTaxes}")
            if (billSummary.couponDiscount > 0) BillDetailRow("Coupon Discount", "-₹${billSummary.couponDiscount}", isHighlight = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))
            BillDetailRow("Grand Total", "₹${billSummary.grandTotal}", isBold = true)
            BillDetailRow("Round Off", "₹${billSummary.roundOff}")
            BillDetailRow("To Pay", "₹${billSummary.toPay}", isBold = true, size = 18.sp, themeColor = Color.Black)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun BillDetailRow(label: String, value: String, isBold: Boolean = false, isHighlight: Boolean = false, size: androidx.compose.ui.unit.TextUnit = 14.sp, themeColor: Color = Color.Black) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = TextStyle(fontSize = size, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium, color = if (isBold) Color.Black else Color.Gray))
        Text(text = value, style = TextStyle(fontSize = size, fontWeight = FontWeight.ExtraBold, color = if (isHighlight) Color(0xFF2E7D32) else if (isBold) themeColor else Color.Black))
    }
}
