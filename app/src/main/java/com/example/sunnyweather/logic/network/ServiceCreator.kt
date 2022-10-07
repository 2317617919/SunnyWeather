package com.example.sunnyweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 *@author xw
 *@创建者  ServiceCreator
 *@创建时间 2022/10/7 10:27
 *
 * object关键字让 ServiceCreator 成为了一个单例类
 *
 * 并在内部定义了一个 BASE_URL 常量，用于指定 retrofit 的根路径
 *
 * 在内部使用 Retrofit.Builder 构建一个 retrofit 对象
 * 注意这些都是用 private 修饰符来声明的，相当于对于外部而言它们都是不可见的。
 *
 * 提供了一个外部可见的 create() 方法，并接收一个 Class 类型的参数
 * 当在外部调用这个方法时，实际上就是调用了 Retrofit 对象的 create() 方法，从而创建出相应 Service 接口的动态代理对象。
 *
 * 定义了一个不带参数的 create() 方法，并使用 inline 关键字来修饰方法，使用 reified 关键字来修饰泛型这是泛型实化的两大前提条件。
 * 接下来就可以使用 T::class.java 语法，这里调用刚才定义的带有 Class 参数的 create() 方法即可。
 *
 * inline 关键字来表示内联函数
 * reified关键字来表示该泛型要进行实化
 */

object ServiceCreator {

    private const val BASE_URL = "https://api.caiyunapp.com/"

    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}