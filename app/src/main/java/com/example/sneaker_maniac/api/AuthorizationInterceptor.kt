package com.example.sneaker_maniac.api

import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.example.sneaker_maniac.App
import com.example.sneaker_maniac.BuildConfig
import com.example.sneaker_maniac.R
import okhttp3.*
import okio.Buffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8

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
        if (BuildConfig.DEBUG) {
            val responseLogBuilder = StringBuilder()
            responseLogBuilder.append(response.code())
            responseLogBuilder.append(" ")
            responseLogBuilder.append(response.request().method())
            responseLogBuilder.append(" ")
            responseLogBuilder.append(response.request().url())
            responseLogBuilder.append(" ")
            if (response.request().body() != null) {
                responseLogBuilder.append("\n--- REQUEST\n")
                responseLogBuilder.append(requestBodyToString(response.request().body()))
            }

            val body = response.body()!!
            val source = body.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer()
            val contentType = body.contentType()
            val charset: Charset = contentType?.charset(UTF_8) ?: UTF_8
            if (body.contentLength() != 0L) {
                responseLogBuilder.append("\n--- RESPONSE\n")
                val string = buffer.clone().readString(charset)
                responseLogBuilder.append(string)
            }
            Log.d(this::class.java.simpleName, responseLogBuilder.toString())
        }
        return response
    }

    private fun requestBodyToString(requestBody: RequestBody?): String? {
        requestBody ?: return null
        return try {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            null
        }
    }
}