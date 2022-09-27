package com.example.sneaker_maniac.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.sneaker_maniac.Properties
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiManager {
    lateinit var apiService: ApiService
    private var retrofit: Retrofit? = null
    const val validatorErrorKey = "message"
    private var connectivityManager: ConnectivityManager? = null
    private var connectivityCallback: ConnectivityManager.NetworkCallback? = null

    init {
        initService(initRetrofit())
    }

    private fun initRetrofit(): Retrofit {
        if (retrofit != null) {
            return this.retrofit!!
        }
        val gson = Gson()
            .newBuilder()
            .setLenient()
            .create()
        retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("http://n931333e.beget.tech/api/v1/")
            .client(initOkHttpClient())
            .build()

        return retrofit!!
    }

    private fun initOkHttpClient(): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .callTimeout(120, TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }
            .retryOnConnectionFailure(false)
            .followRedirects(false)
            .addInterceptor(AuthorizationInterceptor())

        return okHttpBuilder.build()
    }

    private fun initService(retrofit: Retrofit) {
        apiService = retrofit.create(ApiService::class.java)
    }


    fun setConnectCallback(context: Context, delegate: IInternetConnected? = null) {
        if (connectivityManager != null && connectivityCallback != null) {
            unsetConnectCallback()
        }
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Properties.isNetworkConnect = true

                val capabilities = connectivityManager
                    ?.getNetworkCapabilities(connectivityManager?.activeNetwork)

                Properties.isNetworkConnect =
                    capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                MainScope().launch(Dispatchers.Main) {
                    delegate?.onConnect()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Properties.isNetworkConnect = false
                val capabilities = connectivityManager
                    ?.getNetworkCapabilities(connectivityManager?.activeNetwork)

                Properties.isNetworkConnect =
                    capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                MainScope().launch(Dispatchers.Main) {
                    delegate?.onLost()
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        connectivityManager?.registerNetworkCallback(
            networkRequest,
            connectivityCallback as ConnectivityManager.NetworkCallback
        )
    }

    fun unsetConnectCallback() {
        if (connectivityManager != null && connectivityCallback != null) {
            connectivityManager?.unregisterNetworkCallback(connectivityCallback as ConnectivityManager.NetworkCallback)
            connectivityManager = null
            connectivityCallback = null
        }
    }
}