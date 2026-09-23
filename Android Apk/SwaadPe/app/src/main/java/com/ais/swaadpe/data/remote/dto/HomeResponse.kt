package com.ais.swaadpe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    val status: Boolean,
    val message: String,
    val data: HomeData
)

data class HomeData(
    @SerializedName("location_text")
    val locationText: String,
    @SerializedName("active_order")
    val activeOrder: ActiveOrderDto?,
    val banners: List<BannerDto>,
    val categories: List<CategoryDto>,
    @SerializedName("top_picks")
    val topPicks: List<TopPickDto>,
    val restaurants: List<VendorDto>,
    @SerializedName("matched_items")
    val matchedItems: List<MatchedItemDto>?
)

data class ActiveOrderDto(
    @SerializedName("order_number")
    val orderNumber: String,
    @SerializedName("order_status")
    val orderStatus: String,
    @SerializedName("grand_total")
    val grandTotal: Double
)

data class BannerDto(
    val id: Int,
    val title: String?,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("action_url")
    val actionUrl: String?
)

data class CategoryDto(
    val id: Int,
    val name: String,
    @SerializedName("icon_url")
    val iconUrl: String?
)

data class TopPickDto(
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("item_name")
    val itemName: String,
    val price: Double,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("food_type")
    val foodType: String,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("vendor_name")
    val vendorName: String
)

data class VendorDto(
    @SerializedName("vendor_id")
    val id: Int,
    val name: String,
    val rating: Double,
    @SerializedName("delivery_time")
    val deliveryTime: String,
    @SerializedName("min_order")
    val minOrder: Double,
    @SerializedName("is_pure_veg")
    val isPureVeg: Boolean,
    @SerializedName("is_open")
    val isOpen: Boolean,
    @SerializedName("fssai_verified")
    val fssaiVerified: Boolean,
    @SerializedName("distance_display")
    val distanceDisplay: String,
    val images: List<String>
)

data class MatchedItemDto(
    @SerializedName("item_id")
    val id: Int,
    @SerializedName("item_name")
    val name: String,
    val description: String?,
    val price: Double,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("is_available")
    val isAvailable: Boolean,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("vendor_name")
    val vendorName: String
)
