package com.mei.pay.back

import com.joker.PayFlags
import com.joker.PayType

/**
 *
 * @author Created by Ling on 2019-07-22
 */


/**
 * 付款操作
 */
fun @receiver:PayFlags Int.parsePayType(): String {
    return when (this) {
        PayType.alipay -> "alipay"
        PayType.weixin -> "wxpay"
        PayType.paypal -> "paypal"
        else -> ""
    }
}