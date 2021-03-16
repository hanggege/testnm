package com.mei.orc.http.exception

/**
 * Created by joker on 16/8/30.
 */
class CanceledException : RxThrowable(null, ExceptionHandle.ERROR.REQUEST_CANCEL) {
    init {
        msg = "取消请求"
    }

}
