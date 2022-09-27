package com.example.sneaker_maniac.api.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("phone") val phone: String? = null
)
