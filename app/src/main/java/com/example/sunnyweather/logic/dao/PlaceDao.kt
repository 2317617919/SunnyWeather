package com.example.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson

/**
 *@author xw
 *@创建者  PlaceDao
 *@创建时间 2022/10/9 12:11
 */
object PlaceDao {

    // savePlace() 方法用于将 Place 对象存储到 SharedPreferences 文件中，
    // 使用技巧，先通过 GSON 将 Place 对象转成一个 JSON 字符串，然后就可以用字符串存储的方式来保存数据了。
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    //getSavedPlace() 方法用于读取数据，
    // 先将JSON字符串从 SharedPreferences 文件中读取出来，然后再通过 GSON 将 JSON 字符串解析成 Place 对象并返回。
    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }
    //提供一个 isPlaceSaved() 方法，用于判断是否有数据已被存储。
    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() =
        SunnyWeatherApplication.context.getSharedPreferences("Sunny_weather", Context.MODE_PRIVATE)
}