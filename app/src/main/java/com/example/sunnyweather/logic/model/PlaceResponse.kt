package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 *@author xw
 *@创建者  PlaceResponse
 *@创建时间 2022/10/7 10:02
 */
data class PlaceResponse(val status: String, val places: List<Place>)

data class Place(val name: String, val location: Location, @SerializedName("formatted_address") val address: String)

data class Location(val lng: String, val lat: String)
