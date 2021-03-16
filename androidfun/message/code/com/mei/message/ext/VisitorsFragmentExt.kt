package com.mei.message.ext

import com.mei.message.VisitorsFragment
import com.mei.orc.util.date.amountDays
import com.mei.orc.util.date.amountYears
import com.mei.orc.util.date.formatToString
import com.mei.wood.extensions.executeKt
import com.net.model.chick.message.VisitorsMessage
import com.net.network.chick.message.MessageVisitorsRequest
import kotlinx.android.synthetic.main.fragment_visitors.*
import java.util.*
import kotlin.math.abs

/**
 *
 * @author Created by lenna on 2020/6/15
 */

/**刷新访问记录列表*/
fun VisitorsFragment.refreshVisitorsList() {
    mPageNo = 1
    mPageSize = 20
    timeResult=""
    loadVisitorsList()
}

/**加载访问记录列表*/
fun VisitorsFragment.loadVisitorsList() {
    showLoading(true)
    apiSpiceMgr.executeKt(MessageVisitorsRequest(mPageNo, mPageSize), success = { it ->
        if (it != null && it.isSuccess) {
            val data: VisitorsMessage? = it.data
            data?.visitors?.apply {
                if (mPageNo == 1) {
                    mVisitorsList.clear()
                }
                list?.apply {
                    for (i in 0 until size) {
                        val visitors: VisitorsMessage.Visitors? = get(i)
                        visitors?.titleTimeStr = visitorsTimeString(visitors?.updateTime ?: 0)
                        visitors?.timeStr = visitorsTimesStr(visitors?.updateTime ?: 0)
                        if (timeResult != visitors?.titleTimeStr) {
                            timeResult = visitors?.titleTimeStr ?: ""
                            visitors?.isVisibleTitle = true
                            if (i - 1 > -1) {
                                val prevVisitors: VisitorsMessage.Visitors? = get(i - 1)
                                prevVisitors?.isVisibleLine = false
                            }
                        }
                    }

                }
                mVisitorsList.addAll(mVisitorsList.size, list)
                visitorsAdapter.loadMoreModule.isEnableLoadMore = mVisitorsList.size > 5

                if (mVisitorsList.size == list.size) {
                    visitorsAdapter.notifyDataSetChanged()
                } else {
                    visitorsAdapter.notifyItemRangeInserted(mVisitorsList.size - list.size, list.size)
                }

                if (hasMore) {
                    visitorsAdapter.loadMoreModule.loadMoreComplete()
                    mPageNo = nextPageNo
                } else {
                    visitorsAdapter.loadMoreModule.loadMoreEnd()
                }

            }


        } else {
            visitorsAdapter.loadMoreModule.loadMoreFail()
        }
    }, finish = {
        showLoading(false)
        visitors_refresh.isRefreshing = false
    }
    )
}

/**计算访问时间*/
fun visitorsTimeString(timeStamp: Long): String {
    if (timeStamp <= 0) return ""
    val date = Date(timeStamp * 1000)
    val days = date.amountDays(Date())
    val year = abs(date.amountYears(Date()))
    val formatStr = when {
        days == 0 -> "今天"
        days == -1 -> "昨天"
        year == 0 -> "MM月dd日"
        else -> "yy年MM月dd日"
    }
    return date.formatToString(formatStr)
}

fun visitorsTimesStr(timeStamp: Long): String {
    if (timeStamp <= 0) return ""
    val date = Date(timeStamp * 1000)
    return date.formatToString("HH:mm")
}

