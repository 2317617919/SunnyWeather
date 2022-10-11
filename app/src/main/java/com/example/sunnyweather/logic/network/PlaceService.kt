package com.example.sunnyweather.logic.network


import com.example.sunnyweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *@author xw
 *@创建者  PlaceService
 *@创建时间 2022/10/7 10:21
 */
interface PlaceService {

    /**
     *  https://api.caiyunapp.com/v2/place?query=北京&token={token}&lang=zh_CN
     */
    @GET("v2/place?&token={SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String) : Call<PlaceResponse>
}