package com.ais.swaadpe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val mobile: String,
    @SerializedName("fcm_token")
    val fcmToken: String? = null
)

data class LoginResponse(
    val status: Boolean,
    val message: String,
    @SerializedName("is_new_user")
    val isNewUser: Boolean? = null,
    @SerializedName("user_data")
    val userData: UserDataDto? = null,
    val error: String? = null
)

data class UserDataDto(
    @SerializedName("user_id")
    val userId: Int,
    val name: String?,
    val mobile: String,
    val email: String?,
    @SerializedName("wallet_balance")
    val walletBalance: Double,
    val address: String?
)
