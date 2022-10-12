package com.example.sunnyweather.ui.weather

import android.content.Context
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.drawerLayout
import kotlinx.android.synthetic.main.activity_weather.swipeRefresh
import kotlinx.android.synthetic.main.activity_weather.weatherLayout
import kotlinx.android.synthetic.main.forecast.forecastLayout
import kotlinx.android.synthetic.main.life_index.carWashingText
import kotlinx.android.synthetic.main.life_index.coldRiskText
import kotlinx.android.synthetic.main.life_index.dressingText
import kotlinx.android.synthetic.main.life_index.ultravioletText
import kotlinx.android.synthetic.main.now.*
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * 设置透明状态栏
         */
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        
        setContentView(R.layout.activity_weather)
//        //实例化 ViewCompat.getWindowInsetsController()   - androidx.core 更新至 1.6
//        val decorView =ViewCompat.getWindowInsetsController(window.decorView)
//        //沉浸式设置 参数二 decorFitsSystemWindows false 表示沉浸，true表示不沉浸
//        WindowCompat.setDecorFitsSystemWindows(window,false)
//        //隐藏状态栏
//        decorView?.hide(WindowInsetsCompat.Type.statusBars())
//        //设置状态栏颜色为透明
//        window.statusBarColor = Color.TRANSPARENT

        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)

        /**
         * 下拉刷新，更新UI状态
         *
         * 调用 SwipeRefreshLayout 的 setColorSchemeResources() 方法，来设置下拉刷新进度条的颜色
         * 调用setOnRefreshListener() 方法给 SwipeRefreshLayout 设置一个下拉刷新的监听器
         * 当触发了下拉刷新操作的时候，就在监听器的回调中调用 refreshWeather() 方法来刷新天气信息。
         * 请求结束后将 SwipeRefreshLayout  的 isRefreshing 属性设置成 false，用于表示刷新事件结束，并隐藏刷新进度条
         */
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        /**
         * 加入滑动菜单
         */
        navBtn.setOnClickListener {
        //调用 DrawerLayout 的openDrawer()方法来打开滑动菜单
            drawerLayout.openDrawer(GravityCompat.START)
        }
        /**
         *监听 DrawerLayout 的状态，当滑动菜单被隐藏的时候，同时也要隐藏输入法。
         * 待会在滑动菜单中搜索城市时会弹出输入法，如果滑动菜单隐藏后输入法却还显示在界面上，
         * 就会是一种非常怪异的情况。
         * 现在将 PlaceFragment 嵌入 WeatherActivity 中之后，
         * 如果还执行这段逻辑肯定是不行的，因为这会造成无限循环跳转的情况
         */

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
    }

    /**
     * 刷新天气信息
     * 调用 WeatherViewModel 的 refreshWeather() 方法，
     * 并将 SwipeRefreshLayout 的 isRefreshing 属性设置成true，从而让下拉刷新进度条显示出来。
     */
    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        //填充now.xml 布局中的数据
        val currentTempText = "${realtime.temperature.toInt()}°C"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}°C"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }

        //填充life_index.xml 布局
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }
}