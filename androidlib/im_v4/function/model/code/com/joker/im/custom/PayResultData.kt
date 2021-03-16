package com.joker.im.custom


/**
 * 佛祖保佑         永无BUG

 * Created by joker on 2017/2/22.
 */

class PayResultData : CustomBaseData() {


    class Result(data: PayResultData) : CustomInfo<PayResultData>(CustomType.pay_result.name, data)

    override val summary: String
        get() = "支付\"$course_name\"成功"

    override val copySummary: String
        get() = ""


    var order_sn: String = ""//订单ID
    var order_price: String = ""//订单价格
    var pay_time: String = ""//支付时间
    var course_name: String = ""//课程名
    var pay_type: String = ""//支付类型
    var trade_type: String = ""//交易类型
    var link_url: String = ""//跳转内链
}
