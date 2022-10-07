package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException

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

    fun searchPlace(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }
}