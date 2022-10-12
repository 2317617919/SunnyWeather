package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

/**
 *@author xw
 *@创建者  Repository
 *@创建时间 2022/10/7 11:26
 *
 * 仓库层中定义的方法，为了能将异步获取的数据以响应式编程的方式通知给上一层，通常会返回一个 liveData 对象
 *
 * liveData() 函数是 lifecycle-livedata-ktx 库提供的一个非常强大且好用的功能，
 * 它可以自动构建并返回一个 LiveData 对象，然后在它的代码块中提供一个挂起函数的上下文，
 * 这样我们就可以在 liveData() 函数的代码块中调用任意的挂起函数了。
 *
 * 调用了 SunnyWeatherNetwork 的 searchPlaces() 函数来搜索城市数据，
 * 然后判断如果服务器响应的状态是 ok，那么就使用 Kotlin 内置的 Result.success() 方法来包装获取的城市数据列表，
 * 否则使用 Result.failure() 方法来包装一个异常信息。最后使用一个 emit() 方法将包装的结果发射出去，
 * 这个 emit() 方法其实类似于调用 LiveData 的 setValue()方法来通知数据变化，
 * 只不过这里我们无法直接取得返回的LiveData 对象，所以 lifecycle-livedata-ktx 库提供了这样一个替代方法。
 *
 * 注意 liveData()函数的线程参数类型指定成了 Dispatchers.IO 这样代码块中的所有代码就都运行在子线程中了
 * Android 是不允许在主线程中进行网络请求的，诸如读写数据库之类的本地数据操作也是不建议在主线程中进行的，因此非常有必要在仓库层进行一次线程转换
 */

object Repository {

    /**
     * PlaceDao 接口封装
     */
    fun savePlace(place: Place) =PlaceDao.savePlace(place)

    fun getSavedPlace() =PlaceDao.getSavedPlace()

    fun isPlaceSaved() =PlaceDao.isPlaceSaved()

    fun searchPlace(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }

    }

    /**
     *  获取实时天气信息和获取未来天气信息这两个请求 息这两个请求是没有先后顺序的 因此并发执行可以提升程序的运行效率
     *  使用协程的 async 函数  分别在两个 async 函数中发起网络请求，然后再分别调用它们的 await() 方法，
     *  就可以保证只有在两个网络请求都成功响应之后，才会进一步执行程序
     *
     *  由于 async 函数必须在协程作用域内才能调用 所以这里又使用 coroutineScope 函数创建了一个协程作用域。
     */
    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(RuntimeException("realtime response status is ${realtimeResponse.status}" + "daily response status is ${dailyResponse.status}"))
            }
        }
    }

    /**
     * 由于使用了协程来简化网络回调的写法，导致 SunnyWeatherNetwork 中封装的每个网络请求接口都可能会抛出异常，
     * 于是必须在仓库层中为每个网络请求都进行 try catch 处理，这无疑增加了仓库层代码实现的复杂度。
     * 可以在某个统一的入口函数中进行封装，使得只要进行一次 try catch 处理就行了。
     *
     * 新增的 fire() 函数 按照liveData()函数的参数接收标准定义的一个高阶函数。在 fire() 函数的内部会先调用一下 liveData() 函数
     * 然后在 liveData() 函数的代码块中统一进行了 try catch 处理
     * 并在try语句中调用传入的 Lambda 表达式中的代码，最终获取 Lambda 表达式的执行结果并调用emit()方法发射出去
     *
     * 注意：在 liveData() 函数的代码块中，是拥有挂起函数上下文的，可是当回调到 Lambda 表达式中，
     * 代码就没有挂起函数上下文了，但实际上 Lambda 表达式中的代码一定也是在挂起函数中运行的。为了解决这个问题，
     * 需要在函数类型前声明一个 suspend 关键字，以表示所有传入的 Lambda  表达式中的代码也是拥有挂起函数上下文的。
     */
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) = liveData<Result<T>>(context) {
        val result = try {
            block()
        } catch (e: Exception) {
            Result.failure<T>(e)
        }
        emit(result)
    }

}