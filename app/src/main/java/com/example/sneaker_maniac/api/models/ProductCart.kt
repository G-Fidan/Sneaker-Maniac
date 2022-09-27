package com.example.sneaker_maniac.api.models

import com.google.gson.annotations.SerializedName

data class ProductCart(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("product") var product: ProductResponse? = null,
    @SerializedName("count") var count: Int? = null
)
