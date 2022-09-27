package com.example.sneaker_maniac.api

import com.example.sneaker_maniac.api.models.LoginRequest
import com.example.sneaker_maniac.api.models.MessageResponse
import com.example.sneaker_maniac.api.models.ProductResponse
import com.example.sneaker_maniac.api.models.ProfileModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("products/")
    suspend fun getAllProducts(): List<ProductResponse>

    @POST("login/")
    suspend fun login(@Body body: LoginRequest): MessageResponse

    @POST("registration/")
    suspend fun registration(@Body body: LoginRequest): MessageResponse

    @GET("account/")
    suspend fun getProfile(): ProfileModel

    @POST("account/")
    suspend fun updateProfile(@Body body: ProfileModel): MessageResponse

    @POST("order/")
    suspend fun createOrder(@Body body: Int): MessageResponse
}