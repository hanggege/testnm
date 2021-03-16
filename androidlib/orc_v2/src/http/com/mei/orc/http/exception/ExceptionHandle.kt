package com.mei.orc.http.exception

import android.net.ParseException
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * Created by joker on 16/8/26.
 * retrofit 出错处理类
 */
class ExceptionHandle {

    /**
     * 约定异常
     */
    internal object ERROR {
        /**
         * 未知错误
         */
        val UNKNOWN = 1000

        /**
         * 解析错误
         */
        val PARSE_ERROR = 1001

        /**
         * 网络错误
         */
        val NETWORD_ERROR = 1002

        /**
         * 协议出错
         */
        val HTTP_ERROR = 1003

        /**
         * 证书出错
         */
        val SSL_ERROR = 1005

        /**
         * 取消请求
         */
        val REQUEST_CANCEL = 1006
    }


    inner class ServerException : RuntimeException() {
        var code: Int = 0
        var msg: String? = null
    }

    companion object {
        private val UNAUTHORIZED = 401
        private val FORBIDDEN = 403
        private val NOT_FOUND = 404
        private val REQUEST_TIMEOUT = 408
        private val INTERNAL_SERVER_ERROR = 500
        private val BAD_GATEWAY = 502
        private val SERVICE_UNAVAILABLE = 503
        private val GATEWAY_TIMEOUT = 504

        fun handleException(e: Throwable): RxThrowable {
            val ex: RxThrowable
            if (e is CanceledException) {
                return e
            } else if (e is UnknownHostException) {
                ex = RxThrowable(e, ERROR.HTTP_ERROR)
                ex.msg = "网络开小差了"//4.2.34 更换UI时要求改的
                return ex
            } else if (e is HttpException) {
                ex = RxThrowable(e, ERROR.HTTP_ERROR)
                when (e.code()) {
                    UNAUTHORIZED, FORBIDDEN, NOT_FOUND, REQUEST_TIMEOUT, GATEWAY_TIMEOUT, INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE -> ex.msg = "网络开小差了"
                    else -> ex.msg = "网络开小差了"
                }
                return ex
            } else if (e is ServerException) {
                ex = RxThrowable(e, e.code)
                ex.msg = e.msg
                return ex
            } else if (e is JsonParseException
                    || e is JSONException
                    || e is ParseException) {
                ex = RxThrowable(e, ERROR.PARSE_ERROR)
                ex.msg = "数据解析错误"
                return ex
            } else if (e is ConnectException || e is SocketTimeoutException) {
                ex = RxThrowable(e, ERROR.NETWORD_ERROR)
                ex.msg = "连接失败"
                return ex
            } else if (e is javax.net.ssl.SSLHandshakeException) {
                ex = RxThrowable(e, ERROR.SSL_ERROR)
                ex.msg = "证书验证失败"
                return ex
            } else if (e is NullPointerException) {
                ex = RxThrowable(e, ERROR.UNKNOWN)
                ex.msg = ""
                return ex
            } else {
                ex = RxThrowable(e, ERROR.UNKNOWN)
                ex.msg = e.message
                return ex
            }
        }
    }
}
