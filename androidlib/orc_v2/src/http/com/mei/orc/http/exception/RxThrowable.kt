package com.mei.orc.http.exception

/**
 * Created by joker on 16/8/26.
 */
open class RxThrowable(throwable: Throwable?, var code: Int) : Exception(throwable) {
    var msg: String? = null

    val currMessage: String?
        get() = if (this is CanceledException) {
            ""
        } else
            msg
}
