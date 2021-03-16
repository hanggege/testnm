package com.mei.orc.single

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken

/**
 *  Created by zzw on 2019-07-16
 *  Des:放置 全局单利公用对象
 */

val gson: Gson by lazy {
    GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapterFactory(object : TypeAdapterFactory {
                override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
                    return null
                }
            })
            .create()
}




