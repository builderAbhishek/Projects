package com.ais.swaadpe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MenuResponse(
    val status: Boolean,
    val message: String,
    val data: MenuData
)

data class MenuData(
    @SerializedName("vendor_details")
    val vendorDetails: VendorDetailsDto,
    val menu: List<MenuCategoryDto>
)

data class VendorDetailsDto(
    val id: Int,
    val name: String,
    val image: String?,
    val address: String?,
    val rating: Double,
    @SerializedName("delivery_time")
    val deliveryTime: String,
    @SerializedName("min_order")
    val minOrder: Double,
    @SerializedName("is_open")
    val isOpen: Boolean = true
)

data class MenuCategoryDto(
    @SerializedName("category_name")
    val categoryName: String,
    val items: List<MenuItemDto>
)

data class MenuItemDto(
    @SerializedName("item_id")
    val id: Int,
    @SerializedName("item_name")
    val name: String,
    val description: String?,
    val price: Double,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("is_available")
    val isAvailable: Boolean
)
