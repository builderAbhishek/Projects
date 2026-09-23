package com.ais.swaadpe.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ais.swaadpe.data.api.SwaadPeApi
import com.ais.swaadpe.data.local.prefs.PreferenceManager
import com.ais.swaadpe.data.remote.dto.MyOrderDto
import com.ais.swaadpe.data.remote.dto.UpdateProfileRequest
import com.ais.swaadpe.data.remote.dto.UserProfileDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val api: SwaadPeApi,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _orderHistoryState = MutableStateFlow<OrderHistoryState>(OrderHistoryState.Loading)
    val orderHistoryState: StateFlow<OrderHistoryState> = _orderHistoryState.asStateFlow()

    private val _activeOrdersState = MutableStateFlow<OrderHistoryState>(OrderHistoryState.Loading)
    val activeOrdersState: StateFlow<OrderHistoryState> = _activeOrdersState.asStateFlow()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    val userId = preferenceManager.getUserId()
    val userMobile = preferenceManager.getUserMobile() ?: ""

    init {
        fetchProfile()
        fetchOrders()
    }

    fun fetchProfile() {
        if (userId <= 0) return
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = api.getProfile(userId)
                if (response.isSuccessful && response.body()?.status == true) {
                    val profileData = response.body()!!.data
                    if (profileData != null) {
                        _profileState.value = ProfileState.Success(profileData)
                    } else {
                        _profileState.value = ProfileState.Error("Profile data is null")
                    }
                } else {
                    _profileState.value = ProfileState.Error(response.body()?.message ?: "Failed to fetch profile")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Network error")
            }
        }
    }

    fun updateProfile(name: String, email: String, onSuccess: () -> Unit) {
        if (userId <= 0) return
        viewModelScope.launch {
            try {
                val request = UpdateProfileRequest(userId, name, email)
                val response = api.updateProfile(request)
                if (response.isSuccessful && response.body()?.status == true) {
                    fetchProfile() // Refresh local data
                    onSuccess()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun fetchOrders() {
        if (userId <= 0) return

        viewModelScope.launch {
            // Fetch History
            _orderHistoryState.value = OrderHistoryState.Loading
            try {
                val response = api.getMyOrders(userId, "history")
                if (response.isSuccessful && response.body()?.status == true) {
                    _orderHistoryState.value = OrderHistoryState.Success(response.body()!!.data)
                } else {
                    _orderHistoryState.value = OrderHistoryState.Success(emptyList())
                }
            } catch (e: Exception) {
                _orderHistoryState.value = OrderHistoryState.Error(e.message ?: "Network error")
            }

            // Fetch Active
            _activeOrdersState.value = OrderHistoryState.Loading
            try {
                val response = api.getMyOrders(userId, "active")
                if (response.isSuccessful && response.body()?.status == true) {
                    _activeOrdersState.value = OrderHistoryState.Success(response.body()!!.data)
                } else {
                    _activeOrdersState.value = OrderHistoryState.Success(emptyList())
                }
            } catch (e: Exception) {
                _activeOrdersState.value = OrderHistoryState.Error(e.message ?: "Network error")
            }
        }
    }
    
    fun logout() {
        preferenceManager.clearSession()
    }
}

sealed class OrderHistoryState {
    object Loading : OrderHistoryState()
    data class Success(val orders: List<MyOrderDto>) : OrderHistoryState()
    data class Error(val message: String) : OrderHistoryState()
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: UserProfileDto) : ProfileState()
    data class Error(val message: String) : ProfileState()
}
