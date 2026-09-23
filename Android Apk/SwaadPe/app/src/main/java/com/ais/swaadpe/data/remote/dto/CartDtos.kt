package com.ais.swaadpe.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- Sync Cart DTOs ---
data class CartSyncRequest(
    @SerializedName("user_id")
    val userId: Int,
    val items: List<CartSyncItemDto>
)

data class CartSyncItemDto(
    val id: Int,
    val quantity: Int,
    @SerializedName("vendor_id")
    val vendorId: Int
)

data class CartSyncResponse(
    val status: Boolean,
    val message: String,
    @SerializedName("out_of_stock_alerts")
    val outOfStockAlerts: List<String>? = null,
    val data: CartSyncData? = null
)

data class CartSyncData(
    @SerializedName("updated_items")
    val updatedItems: List<UpdatedItemDto>? = null,
    @SerializedName("cart_summary")
    val cartSummary: CartSummaryDto? = null
)

data class CartSummaryDto(
    @SerializedName("item_total")
    val itemTotal: Double,
    @SerializedName("item_savings")
    val itemSavings: Double
)

data class UpdatedItemDto(
    val id: Int,
    val name: String,
    @SerializedName("original_price")
    val originalPrice: Double,
    @SerializedName("discounted_price")
    val discountedPrice: Double,
    val quantity: Int
)

// --- Checkout DTOs ---
data class CheckoutRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("address_id")
    val addressId: Int,
    @SerializedName("coupon_code")
    val couponCode: String? = null
)

data class CheckoutResponse(
    val status: Boolean,
    val message: String,
    val data: CheckoutData? = null
)

data class CheckoutData(
    @SerializedName("bill_summary")
    val billSummary: BillSummaryDto? = null,
    @SerializedName("delivery_info")
    val deliveryInfo: DeliveryInfoDto? = null
)

data class BillSummaryDto(
    @SerializedName("item_total")
    val itemTotal: Double,
    @SerializedName("item_savings")
    val itemSavings: Double = 0.0,
    @SerializedName("packaging_charges")
    val packagingCharges: Double,
    @SerializedName("delivery_fee")
    val deliveryFee: Double,
    @SerializedName("delivery_discount")
    val deliveryDiscount: Double,
    @SerializedName("platform_fee")
    val platformFee: Double,
    @SerializedName("gst_rate")
    val gstRate: Double,
    @SerializedName("gst_taxes")
    val gstTaxes: Double,
    @SerializedName("coupon_applied")
    val couponApplied: Boolean,
    @SerializedName("coupon_code")
    val couponCode: String?,
    @SerializedName("coupon_discount")
    val couponDiscount: Double,
    @SerializedName("coupon_message")
    val couponMessage: String?,
    @SerializedName("grand_total")
    val grandTotal: Double,
    @SerializedName("round_off")
    val roundOff: Double,
    @SerializedName("to_pay")
    val toPay: Int,
    @SerializedName("savings_message")
    val savingsMessage: String?
)

data class DeliveryInfoDto(
    @SerializedName("estimated_time")
    val estimatedTime: String,
    @SerializedName("distance_km")
    val distanceKm: Double,
    @SerializedName("distance_display")
    val distanceDisplay: String,
    @SerializedName("is_deliverable")
    val isDeliverable: Boolean
)

// --- Coupon DTOs ---
data class CouponResponse(
    val status: Boolean,
    val message: String,
    val data: CouponData? = null
)

data class CouponData(
    @SerializedName("available_coupons")
    val availableCoupons: List<CouponDto>
)

data class CouponDto(
    @SerializedName("coupon_id")
    val couponId: Int,
    val code: String,
    val title: String,
    val subtitle: String,
    @SerializedName("valid_till")
    val validTill: String,
    @SerializedName("discount_type")
    val discountType: String,
    @SerializedName("discount_value")
    val discountValue: Double,
    @SerializedName("max_discount")
    val maxDiscount: Double,
    @SerializedName("min_order_amount")
    val minOrderAmount: Double,
    @SerializedName("is_applicable")
    val isApplicable: Boolean,
    @SerializedName("locked_message")
    val lockedMessage: String
)
