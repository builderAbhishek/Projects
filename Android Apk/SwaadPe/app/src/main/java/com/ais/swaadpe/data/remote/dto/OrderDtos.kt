package com.ais.swaadpe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("item_total")
    val itemTotal: Double,
    @SerializedName("delivery_fee")
    val deliveryFee: Double,
    @SerializedName("grand_total")
    val grandTotal: Double,
    val address: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    val items: List<OrderItemDto>
)

data class OrderItemDto(
    val id: Int,
    val name: String,
    val quantity: Int,
    val price: Double
)

data class OrderResponse(
    val status: Boolean,
    val message: String?,
    @SerializedName("order_number")
    val orderNumber: String?
)

data class MyOrdersResponse(
    val status: Boolean,
    val message: String,
    val data: List<MyOrderDto>
)

data class MyOrderDto(
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("order_number")
    val orderNumber: String,
    val vendor: OrderVendorDto,
    val amount: Double,
    @SerializedName("order_status")
    val orderStatus: String,
    @SerializedName("payment_status")
    val paymentStatus: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("formatted_date")
    val formattedDate: String,
    val items: List<OrderDetailItemDto>
)

data class OrderVendorDto(
    val id: Int,
    val name: String,
    val address: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("is_open")
    val isOpen: Boolean
)

data class OrderDetailItemDto(
    val name: String,
    val quantity: Int,
    val price: Double,
    @SerializedName("food_type")
    val foodType: String // 'Veg', 'Non-Veg', 'Egg'
)

// --- Order Details Screen DTOs (Mapped to order_details.php) ---

data class OrderDetailsResponse(
    val status: Boolean,
    val message: String,
    val data: OrderDetailsData?
)

data class OrderDetailsData(
    val header: OrderDetailsHeader,
    val vendor: OrderDetailsVendor,
    @SerializedName("order_items")
    val orderItems: OrderDetailsItems,
    @SerializedName("bill_summary")
    val billSummary: OrderDetailBillSummaryDto,
    val customer: OrderDetailsCustomer,
    @SerializedName("payment_info")
    val paymentInfo: OrderDetailPaymentInfoDto,
    @SerializedName("fssai_info")
    val fssaiInfo: FssaiInfoDto
)

data class OrderDetailsHeader(
    val title: String,
    val status: String
)

data class OrderDetailsVendor(
    val name: String,
    val address: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val mobile: String
)

data class OrderDetailsItems(
    @SerializedName("order_id_display")
    val orderIdDisplay: String,
    val items: List<FormattedItemDto>
)

data class FormattedItemDto(
    @SerializedName("display_text")
    val displayText: String,
    @SerializedName("total_price_formatted")
    val totalPriceFormatted: String,
    @SerializedName("is_veg")
    val isVeg: Boolean
)

data class OrderDetailBillSummaryDto(
    @SerializedName("item_total")
    val itemTotal: String,
    @SerializedName("gst_taxes")
    val gstTaxes: String,
    @SerializedName("delivery_fee")
    val deliveryFee: String,
    @SerializedName("is_delivery_free")
    val isDeliveryFree: Boolean,
    @SerializedName("grand_total")
    val grandTotal: String,
    val discount: String,
    val paid: String,
    @SerializedName("savings_banner_show")
    val savingsBannerShow: Boolean,
    @SerializedName("savings_message")
    val savingsMessage: String
)

data class OrderDetailsCustomer(
    val name: String,
    @SerializedName("masked_mobile")
    val maskedMobile: String
)

data class OrderDetailPaymentInfoDto(
    val method: String,
    val date: String,
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("show_pay_now_button")
    val showPayNowButton: Boolean,
    @SerializedName("payable_amount")
    val payableAmount: Double
)

data class FssaiInfoDto(
    @SerializedName("show_fssai")
    val showFssai: Boolean,
    @SerializedName("license_text")
    val licenseText: String
)

// --- Tracking DTOs ---
data class TrackingResponse(
    val status: Boolean,
    val message: String,
    val data: TrackingData?
)

data class TrackingData(
    @SerializedName("order_info")
    val orderInfo: TrackingOrderInfo,
    @SerializedName("vendor_info")
    val vendorInfo: TrackingVendorInfo,
    @SerializedName("rider_info")
    val riderInfo: TrackingRiderInfo?,
    @SerializedName("tracking_timeline")
    val trackingTimeline: List<TrackingLogDto>
)

data class TrackingOrderInfo(
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("order_number")
    val orderNumber: String,
    @SerializedName("current_status")
    val currentStatus: String,
    @SerializedName("grand_total")
    val grandTotal: Double,
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("placed_at")
    val placedAt: String
)

data class TrackingVendorInfo(
    val name: String,
    val address: String
)

data class TrackingRiderInfo(
    val name: String,
    val mobile: String
)

data class TrackingLogDto(
    val status: String,
    val message: String,
    @SerializedName("created_at")
    val createdAt: String
)

// Deprecated or kept for compatibility if used elsewhere
data class OrderHistoryResponse(
    val status: Boolean,
    val data: List<OrderHistoryDto>
)

data class OrderHistoryDto(
    val id: Int,
    @SerializedName("order_number")
    val orderNumber: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("item_total")
    val itemTotal: Double,
    @SerializedName("delivery_fee")
    val deliveryFee: Double,
    @SerializedName("grand_total")
    val grandTotal: Double,
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("order_status")
    val orderStatus: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("vendor_name")
    val vendorName: String,
    @SerializedName("vendor_image")
    val vendorImage: String?,
    @SerializedName("items_summary")
    val itemsSummary: String
)
