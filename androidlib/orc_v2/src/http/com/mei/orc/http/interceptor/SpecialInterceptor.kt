package com.mei.orc.http.interceptor

import android.webkit.URLUtil
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/12/7
 * 针对只传一个url，请求HttpUrl全部替换的拦截器
 */


const val interceptor_url = "interceptor_url"

class SpecialInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val targetUrl = request.url.queryParameter(interceptor_url)
        if (request.url.querySize == 1 && URLUtil.isNetworkUrl(targetUrl) && targetUrl != null) {
            val httpUrl = targetUrl.toHttpUrl()
            request = request.newBuilder().url(httpUrl).build()
        }
        return chain.proceed(request)
    }


}
