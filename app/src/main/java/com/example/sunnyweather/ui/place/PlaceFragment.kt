package com.example.sunnyweather.ui.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sunnyweather.R
import com.example.sunnyweather.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*

/**
 *@author xw
 *@创建者  PlaceFragment
 *@创建时间 2022/10/7 17:40
 */
class PlaceFragment : Fragment() {
    //lazy函数 懒加载技术来获取 PlaceViewModel 的实例  允许在整个类中随时使用viewModel这个变量，而完全不用关心它何时初始化、是否为空等前提条件。
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    // onCreateView() 方法中加载 fragment_place 布局，
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //在 PlaceFragment 中进行了判断，如果当前已有存储的城市数据，
        // 那么就获取已存储的数据并解析成Place对象，然后使用它的经纬度坐标和城市名直接跳转并传递给WeatherActivity ，
        // 这样用户就不需要每次都重新搜索并选择城市了
        if (viewModel.isPlaceSaved()){
            val place= viewModel.getSavePlace()
            val intent =Intent(context,WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        //设置 LayoutManager 和适配器
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        //使用 PlaceViewModel 中的 placeList 集合作为数据源
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        //调用了 EditText 的 addTextChangedListener() 方法来监听搜索框内容的变化情况。
        // 每当搜索框中的内容发生了变化，我们就获取新的内容，然后传递给 PlaceViewModel 的searchPlaces()方法
        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        //对 PlaceViewModel 中的 placeLiveData 对象进行观察，当有任何数据变化时，就会回调到传入的 Observer 接口实现中。
        // 然后对回调的数据进行判断：如果数据不为空，那么就将这些数据添加到 PlaceViewModel 的 placeList 集合中，
        // 并通知 PlaceAdapter 刷新界面；如果数据为空，则说明发生了异常，此时弹出一个 Toast 提示，并将具体的异常原因打印出来
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer{  result ->
                val places =result.getOrNull()
            if (places !=null){
                recyclerView.visibility =View.VISIBLE
                bgImageView.visibility=View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else{
                Toast.makeText(activity,"未能查询到任何地点",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}