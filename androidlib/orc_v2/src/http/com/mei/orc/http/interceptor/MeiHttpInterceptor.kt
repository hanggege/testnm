package com.mei.orc.http.interceptor

import com.mei.orc.http.retrofit.ClientHelper
import com.mei.orc.util.json.getIntValue
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/2/20
 */
class MeiHttpInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            throw e
        }

        try {
            val builder = chain.request().url.newBuilder()
            val json = response.body?.run {
                source().buffer.clone().readString(Charset.forName("UTF-8"))
            } ?: ""
            val rtn = JSONObject(json).getIntValue("rtn")
            for (meiInterceptor in ClientHelper.meiInterceptors) {
                meiInterceptor.handleResult(builder.build(), rtn, json)
            }
        } catch (e: Exception) {
//            e.printStackTrace()
        }

        return response
    }
}
