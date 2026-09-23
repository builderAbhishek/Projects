package com.ais.swaadpe.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ais.swaadpe.data.api.SwaadPeApi
import com.ais.swaadpe.data.remote.dto.TrackingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TrackingUiState {
    object Idle : TrackingUiState()
    object Loading : TrackingUiState()
    data class Success(val data: TrackingData) : TrackingUiState()
    data class Error(val message: String) : TrackingUiState()
}

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val api: SwaadPeApi
) : ViewModel() {

    private val _trackingState = MutableStateFlow<TrackingUiState>(TrackingUiState.Idle)
    val trackingState: StateFlow<TrackingUiState> = _trackingState.asStateFlow()

    private var pollingJob: Job? = null

    fun startPolling(orderId: String) {
        // Stop any existing polling
        stopPolling()

        pollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    // We don't want to show full screen loading every 10 seconds, 
                    // so we only set it if it's the first time or an error occurred before.
                    if (_trackingState.value !is TrackingUiState.Success) {
                        _trackingState.value = TrackingUiState.Loading
                    }

                    val response = api.trackOrder(orderId)
                    if (response.isSuccessful && response.body()?.status == true) {
                        val data = response.body()?.data
                        if (data != null) {
                            _trackingState.value = TrackingUiState.Success(data)
                            
                            // Optimization: Stop polling if order is delivered or cancelled
                            val status = data.orderInfo.currentStatus.lowercase()
                            if (status == "delivered" || status == "cancelled" || status == "failed") {
                                stopPolling()
                            }
                        } else {
                            _trackingState.value = TrackingUiState.Error("No tracking data available")
                        }
                    } else {
                        // If it's the first call, show error. If it's a background update, maybe just ignore or handle differently.
                        if (_trackingState.value !is TrackingUiState.Success) {
                            _trackingState.value = TrackingUiState.Error(response.body()?.message ?: "Failed to fetch tracking details")
                        }
                    }
                } catch (e: Exception) {
                    if (_trackingState.value !is TrackingUiState.Success) {
                        _trackingState.value = TrackingUiState.Error("Network error: ${e.message}")
                    }
                }
                
                delay(10000) // 10 seconds wait
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
