package com.example.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 *@author xw
 *@创建者  SunnyWeatherApplication
 *@创建时间 2022/10/7 9:53
 */
class SunnyWeatherApplication : Application(){
    companion object {
        const val TOKEN = "1dVwApn8Zf6anY27"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}