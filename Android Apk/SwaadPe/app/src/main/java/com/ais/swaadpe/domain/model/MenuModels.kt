package com.ais.swaadpe.domain.model

data class MenuCategory(
    val name: String,
    val items: List<MenuItem>
)

data class MenuItem(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val isAvailable: Boolean
)

data class VendorDetails(
    val id: Int,
    val name: String,
    val image: String?,
    val address: String?,
    val rating: Double,
    val deliveryTime: String,
    val minOrder: Double,
    val isOpen: Boolean = true
)
