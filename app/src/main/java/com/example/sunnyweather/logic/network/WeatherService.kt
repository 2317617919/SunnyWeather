package com.example.sunnyweather.logic.network

import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.DailyResponse
import com.example.sunnyweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 *@author xw
 *@创建者  WeatherService
 *@创建时间 2022/10/7 19:17
 */
interface WeatherService {
    /**
     * https://api.caiyunapp.com/v2.5/{token}/116.4073963,39.9041999/realtime.json
     */
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng :String,@Path("lat") lat :String):Call<RealtimeResponse>

    /**
     * https://api.caiyunapp.com/v2.5/{token}/116.4073963,39.9041999/daily.json
     */
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String,@Path("lat") lat: String):Call<DailyResponse>
}