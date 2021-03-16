package com.mei.wood.extensions

import android.app.Activity
import com.mei.orc.http.interceptor.interceptor_url
import com.mei.orc.http.response.BaseResponse
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.http.retrofit.RxRequest
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.json.toJson
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 佛祖保佑         永无BUG
 *
 * Created by joker on 2017/3/2.
 */

inline fun <reified RESULT> RetrofitClient.executeKt(url: String,
                                                     crossinline success: (RESULT) -> Unit = {},
                                                     crossinline finish: () -> Unit = {},
                                                     crossinline failure: (RESULT?) -> Unit = {}) {
    executeKt(OkHttpRequest(url), success = {
        val data = it.toJson()?.json2Obj(RESULT::class.java)
        if (data == null) failure(null)
        else success(data)
    }, finish = {
        finish()
    }, failure = {
        failure(it?.toJson()?.json2Obj(RESULT::class.java))
    })
}

inline fun <reified RESULT> RetrofitClient.executeUIKt(activity: Activity,
                                                       url: String,
                                                       crossinline success: (RESULT) -> Unit = {},
                                                       crossinline finish: () -> Unit = {},
                                                       crossinline failure: (RESULT?) -> Unit = {}) {
    executeKt(activity, OkHttpRequest(url), success = {
        val data = it.toJson()?.json2Obj(RESULT::class.java)
        if (data == null) failure(null)
        else success(data)
    }, finish = {
        finish()
    }, failure = {
        failure(it?.toJson()?.json2Obj(RESULT::class.java))
    })
}

interface OkHttpInterface {
    @GET(interceptor_url)
    fun request(@Query(interceptor_url) url: String): Observable<OkHttpResponse>
}

class OkHttpResponse : BaseResponse<Map<String, Any>>()

class OkHttpRequest(val url: String) : RxRequest<OkHttpResponse, OkHttpInterface>(OkHttpResponse::class.java, OkHttpInterface::class.java) {
    override fun createCacheKey(): String = toString()

    override fun loadDataFromNetwork(): Observable<OkHttpResponse> = service.request(url)
}

