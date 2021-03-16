package com.mei.mentor_home_page.adapter.item

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.widget.TextView
import com.mei.mentor_home_page.model.MentorLiveData
import com.mei.orc.ext.sp
import com.mei.wood.R

/**
 *
 * @author Created by lenna on 2020/5/21
 * 礼物数据详细数据
 */
class MentorHomePageLiveDataHolder(var view: View) : MentorBaseHolder(view) {
    override fun refresh(item: Any) {
        val coinTv = getView<TextView>(R.id.mentor_home_page_left_count)
        val fansTv = getView<TextView>(R.id.mentor_home_page_center_count)
        val broadcastTv = getView<TextView>(R.id.mentor_home_page_right_count)
        if (item is MentorLiveData) {
            item.apply {
                coinTv.text = "$receivedCoinCount"
                fansTv.text = "$fansCount"
                val span = SpannableString(broadcastCount)
                if (broadcastCount.contains("小时")) {
                    val start = broadcastCount.indexOf("小")
                    val end = broadcastCount.indexOf("时") + 1
                    span.setSpan(AbsoluteSizeSpan(view.sp(11)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (broadcastCount.contains("分钟")) {
                    val start = broadcastCount.indexOf("分")
                    val end = broadcastCount.indexOf("钟") + 1
                    span.setSpan(AbsoluteSizeSpan(view.sp(11)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                broadcastTv.text = span
            }
        }

    }


}