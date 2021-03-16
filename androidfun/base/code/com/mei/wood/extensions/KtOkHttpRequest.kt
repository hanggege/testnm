package com.mei.wood.extensions

import com.mei.orc.callback.Callback
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.rx.Rx2Observable
import com.mei.orc.util.json.json2Obj
import io.reactivex.ObservableOnSubscribe

/**
 * 佛祖保佑         永无BUG
 *
 * Created by joker on 2017/3/2.
 */
inline fun <reified T> RetrofitClient.executeObject(url: String, crossinline success: (T?) -> Unit) {
    executeStr(url) { result ->
        val t: T? = result.json2Obj(T::class.java)
        success(t)
    }
}

/**
 * 默认方法 网络失败取缓存
 * FOR JAVA (Kotlin的使用更简单)
 */
fun <T> RetrofitClient.executeJava(url: String, cls: Class<T>, call: Callback<T?>) {
    executeStr(url) { result ->
        call.onCallback(result.json2Obj(cls))
    }
}


fun RetrofitClient.executeJavaStr(url: String, call: Callback<String>) {
    executeStr(url) { result ->
        call.onCallback(result)
    }
}


fun RetrofitClient.executeStr(url: String, success: (String) -> Unit) {
    android.util.Log.e("KtOkRequest", "请求url = $url")
    Rx2Observable.createNonNull(
            ObservableOnSubscribe<String> { subscriber ->
                val result: String = requestNet(url)
                subscriber.onNext(result)
                subscriber.onComplete()
            })
            .appNetTransformer()
            .subscribe(object : com.mei.wood.rx.ObserverSilentSubscriber<String>() {
                override fun onNext(t: String) {
                    try {
                        success(t)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onComplete() {
                    dispose()
                }
            })

}

private fun RetrofitClient.requestNet(url: String): String {
    var result: String
    try {
        val request = okhttp3.Request.Builder().url(url).build()
        val response = okClient.newCall(request).execute()
        result = if (response.isSuccessful && response.body != null) {
            response.body?.string().orEmpty()
        } else ""
    } catch (e: Exception) {
        result = ""
        android.util.Log.e("KtOkRequest", "请求出错 --  " + e.message)
    }
    return result
}

