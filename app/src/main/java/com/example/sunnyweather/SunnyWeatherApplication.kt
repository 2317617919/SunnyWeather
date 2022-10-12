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
        const val TOKEN = "Q4XGlPnC6meZonBR"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}