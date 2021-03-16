package com.mei.chat.ui.adapter.item.send.custom

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.joker.im.Message
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.CourseInfo
import com.mei.GrowingUtil
import com.mei.chat.ui.adapter.item.send.IMSendBaseMessageHolder
import com.mei.course_service.ui.MeiWebCourseServiceActivityLauncher
import com.mei.live.ext.createSplitSpannable
import com.mei.orc.Cxt
import com.mei.orc.ext.dip
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.util.string.decodeUrl
import com.mei.widget.holder.setVisibleAndGone
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MeiWebData
import kotlinx.android.synthetic.main.im_row_receive_course_card_item.view.*
import kotlinx.android.synthetic.main.im_row_send_course_card_item.view.*
import kotlinx.android.synthetic.main.im_row_send_course_card_item.view.im_message_container

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMSendCourseCardMessageHolder(itemView: View) : IMSendBaseMessageHolder(itemView) {

    @SuppressLint("ClickableViewAccessibility")
    override fun refreshChild(msg: Message) {
        itemView.apply {
            val data = getCustomData()?.data as? ChickCustomData
            if (data?.courseInfo == null) {
                im_send_course_unit.isVisible = false
                im_receive_course_name_tv.text = "不支持该类型消息请升级高版本"
            } else {
                when (data.courseInfo?.cardType) {
                    CourseInfo.CardType.TYPE_NORMAL
                        , CourseInfo.CardType.TYPE_RECHARGE
                        , CourseInfo.CardType.TYPE_CANCEL
                        , CourseInfo.CardType.TYPE_RECOMMEND
                        , CourseInfo.CardType.TYPE_ASK -> {
                        im_send_course_unit.isVisible = true
                        im_send_course_price_tv.isVisible = true
                        val hint = String.format(resources.getString(R.string.course_service_text), data.courseInfo?.audioCount)
                        im_send_course_name_tv.text = data.courseInfo?.courseName.decodeUrl()
                        im_send_course_price_tv.text = "${data.courseInfo?.cost}"
                        im_send_course_tv.text = hint
                        im_send_original_price.isVisible = data.courseInfo?.crossedPrice != 0
                        val originalPrice = if (data.courseInfo?.crossedPrice != 0) "${data.courseInfo?.crossedPrice}" else ""
                        if (originalPrice.isNotEmpty()) {
                            val ss = SpannableString(originalPrice)
                            ss.setSpan(StrikethroughSpan(), 0, originalPrice.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                            im_send_original_price.text = ss
                        }
                        im_send_course_tv.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            leftMargin = dip(5)
                        }

                        setVisibleAndGone(R.id.history_course_cl, data.courseInfo?.priceText?.isNotEmpty() != true)
                        setVisibleAndGone(R.id.price_text_tv, data.courseInfo?.priceText?.isNotEmpty() == true)
                        setText(R.id.price_text_tv, data.courseInfo?.priceText.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_333333)))

                    }
                    else -> {
                        im_send_course_unit.isVisible = false
                        im_receive_course_name_tv.text = "不支持该类型消息请升级高版本"
                    }

                }
                im_message_container.setOnClickListener { view ->
                    goToCourseDetailPage(view.context, data.courseInfo)
                }
                GrowingUtil.track("function_view",
                        "function_name", "课程"
                        , "function_page", "IM",
                        "status", if (data.courseInfo?.hasBuy != true) "未购买" else "已购买")

            }

        }


    }

    private fun goToCourseDetailPage(context: Context, courseInfo: CourseInfo?) {
        var url = MeiUtil.appendParamsUrl(AmanLink.NetUrl.course_introduce, "status_bar_height" to "${getStatusBarHeight()}", "course_id" to "${courseInfo?.courseId}", "from" to "im")
        if (courseInfo?.cardType == CourseInfo.CardType.TYPE_RECOMMEND) {
            url = MeiUtil.appendParamsUrl(url, "is_recommend" to "1")
        }
        MeiWebCourseServiceActivityLauncher.startActivity(context
                , MeiWebData(url
                , 0).apply {
            need_reload_web = 1
        })
        GrowingUtil.track("function_click",
                "function_name", "课程"
                , "function_page", "IM",
                "click_type", "课程入口",
                "status", if (courseInfo?.hasBuy == false) "未购买" else "已购买")
    }


}