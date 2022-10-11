package com.example.sunnyweather.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 *@author xw
 *@创建者  SunnyWeatherNetwork
 *@创建时间 2022/10/7 10:41
 */
object SunnyWeatherNetwork {
    /**
     * 首先使用 ServiceCreator.create 创建了一个 PlaceService 接口的动态代理对象，
     * 为了让代码变得更加简洁，使用了简化 Retrofit 回调的写法
     *
     * 定义了一个 searchPlaces() 函数，并在这里调用刚刚在 PlaceService 接口中定义的 searchPlaces()方法，以发起搜索城市数据请求。
     * 由于是需要借助协程技术来实现的，因此这里又定义了一个 await() 函数，并将 searchPlaces() 函数也声明成挂起函数
     *
     *
     * 当外部调用 SunnyWeatherNetwork 的 searchPlaces() 函数时
     * Retrofit 就会立即发起网络请求，同时当前的协程也会被阻塞住。直到服务器响应我们的请求之后，await()函数
     * 会将解析出来的数据模型对象取出并返回，同时恢复当前协程的执行，searchPlaces() 函数在得到 await() 函数的返回值后会将该数据再返回到上一层。
     */
    private val placeService = ServiceCreator.create<PlaceService>()

    private val weatherService = ServiceCreator.create<WeatherService>()

    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()


    suspend fun getDailyWeather(lng: String, lat: String) = weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) = weatherService.getRealtimeWeather(lng, lat).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

}