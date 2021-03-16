package com.mei.pay

import android.app.Activity
import com.joker.PayManager
import com.joker.connect.PayCallBack
import com.mei.orc.util.json.json2Obj
import com.mei.pay.webpay.OrderInfoFromWeb
import com.mei.pay.webpay.OrderInfoFromWebV2
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil

/**
 * Created by 杨强彪 on 2017/12/14.
 * @描述：web调第三方支付，将两个版本的合并，使用起来更方便
 */

/**
 * 调起第三方支付SDK
 * */
fun Activity.thirdPartPaymentV2(url: String, payCallBack: PayCallBack) {
    if (url.matches(AmanLink.URL.WEB_CALL_APP_PAY_SDK_V2.toRegex())) {//第二个版本的格式
        val orderInfoFromWebV2 = MeiUtil.getOneID(url, AmanLink.URL.WEB_CALL_APP_PAY_SDK_V2, false).json2Obj<OrderInfoFromWebV2>()
        if (orderInfoFromWebV2?.data != null) {
            val order = orderInfoFromWebV2.data.getOrder(orderInfoFromWebV2.type ?: 0)
            PayManager.performPay(this, orderInfoFromWebV2.type ?: 0, order, payCallBack)
        }
    } else if (url.matches(AmanLink.URL.WEB_CALL_APP_PAY_SDK.toRegex())) {//第一个版本的格式
        val orderInfoFromWeb = MeiUtil.getJsonObject<OrderInfoFromWeb>(url, AmanLink.URL.WEB_CALL_APP_PAY_SDK)
        if (orderInfoFromWeb?.data != null) {
            val order = orderInfoFromWeb.data.getOrder(orderInfoFromWeb.type ?: 0)
            PayManager.performPay(this, orderInfoFromWeb.type ?: 0, order, payCallBack)
        }
    }
}