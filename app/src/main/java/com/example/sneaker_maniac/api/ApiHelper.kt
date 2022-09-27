package com.example.sneaker_maniac.api

import com.example.sneaker_maniac.api.models.LoginRequest
import com.example.sneaker_maniac.api.models.ProfileModel

object ApiHelper {
    private val apiService = ApiManager.apiService

    suspend fun getProducts() = apiService.getAllProducts()

    suspend fun login(body: LoginRequest) = apiService.login(body)

    suspend fun registration(body: LoginRequest) = apiService.registration(body)

    suspend fun getProfile() = apiService.getProfile()

    suspend fun updateProfile(profileModel: ProfileModel) = apiService.updateProfile(profileModel)

    suspend fun createOrder(body: Int) = apiService.createOrder(body)


}