package com.ais.swaadpe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    val status: Boolean,
    val message: String,
    val data: UserProfileDto? = null,
    val error: String? = null
)

data class UserProfileDto(
    @SerializedName("user_id")
    val userId: Int,
    val name: String,
    val mobile: String,
    val email: String,
    @SerializedName("wallet_balance")
    val walletBalance: Double,
    @SerializedName("joined_on")
    val joinedOn: String?
)

data class UpdateProfileRequest(
    @SerializedName("user_id")
    val userId: Int,
    val name: String,
    val email: String
)

data class UpdateProfileResponse(
    val status: Boolean,
    val message: String,
    val data: UpdatedProfileData? = null,
    val error: String? = null
)

data class UpdatedProfileData(
    @SerializedName("user_id")
    val userId: Int,
    val name: String,
    val email: String
)
