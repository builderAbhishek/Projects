package com.ais.swaadpe.data.api

import com.ais.swaadpe.data.remote.dto.*
import com.ais.swaadpe.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SwaadPeApi {

    @POST("api_login.php")
    suspend fun login(
        @Body loginRequest: LoginRequest,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<LoginResponse>

    @GET("api_home.php")
    suspend fun getHomeData(
        @Query("user_id") userId: Int,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("address") address: String,
        @Query("q") query: String? = null,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<HomeResponse>

    @GET("get_vendor_menu.php")
    suspend fun getVendorMenu(
        @Query("vendor_id") vendorId: Int,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<MenuResponse>

    @POST("place_order.php")
    suspend fun placeOrder(
        @Body orderRequest: OrderRequest,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<OrderResponse>

    @GET("get_my_orders.php")
    suspend fun getMyOrders(
        @Query("user_id") userId: Int,
        @Query("type") type: String, // 'active' or 'history'
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<MyOrdersResponse>

    @GET("order_details.php")
    suspend fun getOrderDetails(
        @Query("order_id") orderId: String, // This corresponds to order_number in PHP
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<OrderDetailsResponse>

    @GET("track_order.php")
    suspend fun trackOrder(
        @Query("order_id") orderId: String,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<TrackingResponse>

    @POST("sync_cart.php")
    suspend fun syncCart(
        @Body syncRequest: CartSyncRequest,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<CartSyncResponse>

    @POST("checkout.php")
    suspend fun checkout(
        @Body checkoutRequest: CheckoutRequest,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<CheckoutResponse>

    @GET("get_addresses.php")
    suspend fun getAddresses(
        @Query("user_id") userId: Int,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<AddressResponse>

    @POST("get_addresses.php")
    suspend fun saveAddress(
        @Body addAddressRequest: AddAddressRequest,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<AddressResponse>

    @POST("update_receiver.php")
    suspend fun updateReceiver(
        @Body updateRequest: UpdateReceiverRequest,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<UpdateReceiverResponse>

    @GET("profile.php")
    suspend fun getProfile(
        @Query("user_id") userId: Int,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<ProfileResponse>

    @POST("profile.php")
    suspend fun updateProfile(
        @Body updateRequest: UpdateProfileRequest,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<UpdateProfileResponse>

    @GET("get_coupons.php")
    suspend fun getCoupons(
        @Query("cart_total") cartTotal: Double,
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY
    ): Response<CouponResponse>
}
