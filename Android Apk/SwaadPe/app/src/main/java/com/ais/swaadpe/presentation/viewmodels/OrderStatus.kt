package com.ais.swaadpe.presentation.viewmodels

sealed class OrderStatus {
    object Idle : OrderStatus()
    object Loading : OrderStatus()
    data class Success(val orderNumber: String) : OrderStatus()
    data class Error(val message: String) : OrderStatus()
}
