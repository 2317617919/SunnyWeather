package com.example.sunnyweather.logic.model

/**
 *@author xw
 *@创建者  Weather
 *@创建时间 2022/10/7 19:15
 *
 * 将 Realtime 和 Daily 对象封装起来
 */
data class Weather(val realtime : RealtimeResponse.Realtime,val daily: DailyResponse.Daily)