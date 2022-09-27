package com.example.sneaker_maniac.api.models

import com.google.gson.annotations.SerializedName

data class ProfileModel(
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("number_card") val numberCard: String? = null,
    @SerializedName("date_card") val dateCard: String? = null,
    @SerializedName("cvc_card") val cvcCode: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("index_address") val indexAddress: String? = null,
    @SerializedName("message") val message: String? = null
)