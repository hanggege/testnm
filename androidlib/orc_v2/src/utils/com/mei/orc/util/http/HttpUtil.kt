package com.mei.orc.util.http

import android.text.TextUtils
import com.mei.orc.net.encode
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by steven on 1/19/16.
 */


private val mOkHttpClient by lazy {
    OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS).connectTimeout(15, TimeUnit.SECONDS).build()
}

fun String?.urlEncode(): String {
    if (this.isNullOrEmpty()) return ""
    try {
        return encode()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

private fun queryString(params: Map<Any, Any>): String {
    val pairs = ArrayList<String>()
    for ((key1, value) in params) {
        val key = key1.toString()
        when (value) {
            is Map<*, *> -> for ((key2, subValue) in value) {
                val subKey = key2.toString()
                pairs.add(String.format("%s[%s]=%s", key, subKey, subValue.toString().urlEncode()))
            }
            is List<*> -> for (subValue in value) {
                pairs.add(String.format("%s[]=%s", key, subValue.toString().urlEncode()))
            }
            else -> pairs.add(String.format("%s=%s", key, value.toString().urlEncode()))
        }
    }
    return TextUtils.join("&", pairs)
}

@JvmOverloads
fun requestGet(url: String, params: Map<Any, Any>? = null, success: (String) -> Unit = {}, failure: (Exception) -> Unit = {}) {
    var realUrl = url
    val pString = params?.let { queryString(it) } ?: ""
    if (!TextUtils.isEmpty(pString)) {
        realUrl += "?$pString"
    }
    val request = Request.Builder()
            .url(realUrl)
            .get()//默认就是GET请求，可以不写
            .build()
    val call = mOkHttpClient.newCall(request)
    call.enqueue(object : okhttp3.Callback {
        override fun onFailure(call: Call, e: IOException) {
            failure(e)
        }

        override fun onResponse(call: Call, response: Response) {
            success(response.body?.string().orEmpty())
        }
    })
}

