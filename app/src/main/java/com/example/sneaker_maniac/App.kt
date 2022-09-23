package com.example.sneaker_maniac

import android.app.Application
import android.content.Context
import com.example.sneaker_maniac.api.ApiManager


class App : Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        ApiManager.setConnectCallback(applicationContext)
    }
}