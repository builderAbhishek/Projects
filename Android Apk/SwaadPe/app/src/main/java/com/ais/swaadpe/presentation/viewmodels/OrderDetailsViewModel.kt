package com.ais.swaadpe.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ais.swaadpe.data.api.SwaadPeApi
import com.ais.swaadpe.data.remote.dto.OrderDetailsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val api: SwaadPeApi
) : ViewModel() {

    private val _state = MutableStateFlow<OrderDetailsState>(OrderDetailsState.Loading)
    val state: StateFlow<OrderDetailsState> = _state.asStateFlow()

    fun getOrderDetails(orderId: String) {
        viewModelScope.launch {
            _state.value = OrderDetailsState.Loading
            try {
                val response = api.getOrderDetails(orderId)
                if (response.isSuccessful && response.body()?.status == true) {
                    val data = response.body()?.data
                    if (data != null) {
                        _state.value = OrderDetailsState.Success(data)
                    } else {
                        _state.value = OrderDetailsState.Error("No data found")
                    }
                } else {
                    _state.value = OrderDetailsState.Error(response.body()?.message ?: "Failed to fetch details")
                }
            } catch (e: Exception) {
                _state.value = OrderDetailsState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}

sealed class OrderDetailsState {
    object Loading : OrderDetailsState()
    data class Success(val data: OrderDetailsData) : OrderDetailsState()
    data class Error(val message: String) : OrderDetailsState()
}
