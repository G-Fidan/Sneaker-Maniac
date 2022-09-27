package com.example.sneaker_maniac.api.models

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("token") val token: String? = null,
    @SerializedName("message") val message: String? = null,
)
