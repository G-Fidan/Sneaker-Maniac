package com.example.sneaker_maniac.api

import android.content.Context.MODE_PRIVATE
import com.example.sneaker_maniac.App
import com.example.sneaker_maniac.R
import okhttp3.*

class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response
        val token: String = App.appContext.getSharedPreferences(
            App.appContext.getString(R.string.app_name),
            MODE_PRIVATE
        ).getString("token", "") ?: ""
        try {
            val newRequest = request
                .newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Authorization", "Bearer $token")
                .build()
            response = chain.proceed(newRequest)
        } catch (e: Exception) {
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(400)
                .body(
                    ResponseBody.create(
                        MediaType.parse("application/json"),
                        e.message ?: ""
                    )
                )
                .message(e.message ?: "")
                .build()
        }

        return response
    }
}