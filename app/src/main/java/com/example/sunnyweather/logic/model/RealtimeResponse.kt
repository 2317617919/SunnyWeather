package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 *@author xw
 *@创建者  RealtimeResponse
 *@创建时间 2022/10/7 18:50
 */
data class RealtimeResponse(val status: String, val result: Result) {

    data class Result(val realtime: Realtime)

    data class Realtime(val skycon: String, val temperature: Float, @SerializedName("air_quality") val airQuality: AirQuality)

    data class AirQuality(val aqi: AQI)

    data class AQI(val chn: Float)

}