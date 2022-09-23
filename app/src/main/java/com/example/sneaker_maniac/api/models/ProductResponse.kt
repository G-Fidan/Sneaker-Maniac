package com.example.sneaker_maniac.api.models

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("image_url") val image: String? = null,
    @SerializedName("price") val price: String? = null
)
