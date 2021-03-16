package com.joker.im.custom


/**
 * 佛祖保佑         永无BUG

 * Created by joker on 2017/2/18.
 */

class ReportOrderData : CustomBaseData() {

    class Result(data: ReportOrderData) : CustomInfo<ReportOrderData>(CustomType.report_order.name, data)

    override val summary: String
        get() = "请您在购买咨询师服务时，在小鹿平台上进行担保交易，切勿与咨询师私下交易，否则将面临无法退款和隐私泄露等风险。如果您发现咨询师意图私下交易，请点此举报 >"

    override val copySummary: String
        get() = ""
}
