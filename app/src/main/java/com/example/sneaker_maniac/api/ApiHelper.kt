package com.example.sneaker_maniac.api

object ApiHelper {
    private val apiService = ApiManager.apiService

    suspend fun getProducts() = apiService.getAllProducts()
}