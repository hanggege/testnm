package com.mei.base.util.td

import com.joker.PayFlags
import com.joker.PayType
import com.joker.TdType
import com.joker.flag.TdFlags
import com.mei.provider.ProjectExt

/**
 *@author Created by joker on 2017/7/5.
 * Modify by Ling on 2019-07-22
 */

/**
 * 登录等携带的参数，用于服务器判断是什么登录操作
 */
fun @receiver:TdFlags Int.parseState(): String {
    return when (this) {
        TdType.weixin -> ProjectExt.WEIXIN_ANDROID
        else -> ""
    }
}


fun parsePayType(@PayFlags type: Int): String {
    var payType = ""
    when (type) {
        PayType.alipay -> payType = "alipay"
        PayType.weixin -> payType = "wxpay"
        PayType.paypal -> payType = "paypal"
    }
    return payType
}
