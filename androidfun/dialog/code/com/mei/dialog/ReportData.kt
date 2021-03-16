package com.mei.dialog

import com.mei.init.spiceHolder
import com.mei.wood.extensions.executeKt
import com.net.model.chick.report.ReasonBean
import com.net.network.chick.report.ReportReasonRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/3/16
 */
var reportList: ArrayList<ReasonBean> = arrayListOf()

fun getReportReason() = if (reportList.isNotEmpty()) reportList else {
    refreshReportReason()
    arrayListOf("色情暴力", "恶意辱骂", "欺诈骗钱", "资料虚假", "广告")
            .mapIndexed { index, s -> ReasonBean(index + 1, s) }
}

private fun refreshReportReason() {
    spiceHolder().apiSpiceMgr.executeKt(ReportReasonRequest(), success = {
        reportList.clear()
        reportList.addAll(it.data.orEmpty())
    })
}