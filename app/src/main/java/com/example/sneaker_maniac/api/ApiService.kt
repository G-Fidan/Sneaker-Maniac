package com.example.sneaker_maniac.api

import com.example.sneaker_maniac.api.models.ProductResponse
import retrofit2.http.GET

interface ApiService {
    @GET("products/")
    suspend fun getAllProducts(): List<ProductResponse>
}