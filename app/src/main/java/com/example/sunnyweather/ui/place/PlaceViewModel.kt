package com.example.sunnyweather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place

/**
 *@author xw
 *@创建者  PlaceViewModel
 *@创建时间 2022/10/7 11:48
 *
 * 定义一个 searchPlaces() 方法，这里并没有直接调用仓库层中的 searchPlaces()方法
 * 而是将传入的搜索参数赋值给了一个 searchLiveData 对象,并使用 Transformations 的 switchMap() 方法来观察这个对象，
 * 否则仓库层返回的LiveData 对象将无法进行观察。
 *
 * 每当searchPlaces()函数被调用时，switchMap()方法所对应的转换函数就会执行。
 * 然后在转换函数中，我们只需要调用仓库层中定义的searchPlaces()方法就可以发起网络请求，同时将仓库层返回的LiveData 对象转换成一个可供Activity 观察的LiveData 对象。
 * 另外，我们还在PlaceV iewModel 中定义了一个placeList集合，用于对界面上显示的城市数据进行缓存，因为原则上与界面相关的数据都应该放到ViewModel 中，
 * 这样可以保证它们在手机屏幕发生旋转的时候不会丢失，稍后我们会在编写UI层代码的时候用到这个集合。
 * 好了，关于逻辑层的实现到这里就基本完成了，现在 SunnyWeather 项目已经拥有了搜索全球城市数据的能力，那么接下来就开始进行UI层的实现吧。
 */
class PlaceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlace(query)
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }
}