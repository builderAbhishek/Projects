package com.ais.swaadpe.domain.model

data class Banner(
    val id: Int,
    val title: String?,
    val imageUrl: String
)

data class Category(
    val id: Int,
    val name: String,
    val iconUrl: String?
)

data class Restaurant(
    val id: Int,
    val name: String,
    val rating: Double,
    val deliveryTime: String,
    val imageUrl: String?,
    val cuisineTags: String?,
    val isOpen: Boolean,
    val isPureVeg: Boolean,
    val distanceDisplay: String,
    val images: List<String> = emptyList()
)

data class MatchedItem(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val vendorId: Int,
    val vendorName: String
)

data class TopPick(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val foodType: String,
    val vendorId: Int,
    val vendorName: String
)

data class ActiveOrder(
    val orderNumber: String,
    val status: String,
    val total: Double
)
