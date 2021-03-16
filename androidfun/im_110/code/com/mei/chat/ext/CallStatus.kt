@file:Suppress("UNUSED_PARAMETER")

package com.mei.chat.ext

/**
 * Created by hang on 2020/4/30.
 */
enum class CallStatus(code: Int, text: String) {
    TIMEOUT(1, "未接听"),
    REFUSED(2, "已拒绝"),
    CANCELLED(3, "已取消"),
    FINISHED(4, "已结束")
}