package com.ais.swaadpe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddressResponse(
    val status: Boolean,
    val count: Int? = null,
    val data: List<AddressDto>? = null,
    val message: String? = null,
    @SerializedName("address_id")
    val addressId: Int? = null
)

data class AddressDto(
    @SerializedName(value = "id", alternate = ["address_id"])
    val id: Int,
    @SerializedName("address_tag")
    val addressTag: String,
    @SerializedName("full_address")
    val fullAddress: String,
    @SerializedName("house_no")
    val houseNo: String?,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("receiver_name")
    val receiverName: String?,
    @SerializedName("receiver_phone")
    val receiverPhone: String?
)

data class AddAddressRequest(
    @SerializedName("user_id")
    val userId: Int,
    val tag: String,
    @SerializedName("full_address")
    val fullAddress: String,
    @SerializedName("house_no")
    val houseNo: String?,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("receiver_name")
    val receiverName: String?,
    @SerializedName("receiver_phone")
    val receiverPhone: String?
)

data class UpdateReceiverRequest(
    @SerializedName("address_id")
    val addressId: Int,
    @SerializedName("receiver_name")
    val receiverName: String,
    @SerializedName("receiver_phone")
    val receiverPhone: String
)

data class UpdateReceiverResponse(
    val status: Boolean,
    val message: String,
    val data: UpdateReceiverData? = null,
    val error: String? = null
)

data class UpdateReceiverData(
    @SerializedName("address_id")
    val addressId: Int,
    @SerializedName("new_name")
    val newName: String,
    @SerializedName("new_phone")
    val newPhone: String
)
