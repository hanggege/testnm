package com.mei.chat.ui.adapter.item.send

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.joker.im.Message
import com.joker.im.custom.chick.ChickCustomData
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.live.ext.createSplitSpannable
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMSendGeneralCardHolder(itemView: View) : IMSendBaseMessageHolder(itemView) {
    @SuppressLint("ClickableViewAccessibility")
    override fun refreshChild(msg: Message) {
        val data = getCustomData()?.data as? ChickCustomData
        if (data != null) {
            /** ----------------------top模块----------------------**/
            getView<TextView>(R.id.card_content).apply {
                isVisible = data.content.isNotEmpty()
                if (data.content.isNotEmpty()) {
                    text = data.content.createSplitSpannable(Cxt.getColor(R.color.color_333333))
                }
            }

            val card = data.card
            if (card != null) {
                val status = if (card.buttons.isNullOrEmpty()) "测试卡片" else "测试报告卡片"
                val clickType = if (card.buttons.isNullOrEmpty()) "测试卡片" else "立即查看"
                val functionPage = if (itemView.context is VideoLiveRoomActivity) "直播间" else "私聊页"
                GrowingUtil.track("function_view", "function_name", "测试", "function_page", functionPage, "source", data.source, "status", status, "click_type", "N/A")
                /** ----------------------title模块----------------------**/
                val cardTitle = getView<TextView>(R.id.card_title)
                cardTitle.paint.isFakeBoldText = true
                if (card.title.isNotEmpty()) {
                    cardTitle.text = card.title.createSplitSpannable(Cxt.getColor(R.color.color_333333))
                }


                /** ----------------------subtitle模块----------------------**/
                val cardSubtitle = getView<TextView>(R.id.card_subtitle)
                cardSubtitle.isVisible = card.subTitle.isNotEmpty()
                if (card.subTitle.isNotEmpty()) {
                    cardSubtitle.text = card.subTitle.createSplitSpannable(Cxt.getColor(R.color.color_333333))
                }


                /** ----------------------按钮模块----------------------**/
                getView<LinearLayout>(R.id.button_layout).isVisible = card.buttons.isNotEmpty()
                getView<TextView>(R.id.start_btn).apply {
                    isVisible = card.buttons.isNotEmpty()
                    if (card.buttons.isNotEmpty()) {
                        text = arrayListOf(card.buttons[0]).createSplitSpannable(Color.WHITE)
                        setOnClickListener {
                            GrowingUtil.track("function_click", "function_name", "测试", "function_page", functionPage, "source", data.source, "status", status, "click_type", clickType)
                            Nav.toAmanLink(context, card.buttons[0].action)
                        }
                    }
                }
                getView<TextView>(R.id.end_btn).apply {
                    isVisible = card.buttons.size > 1
                    if (card.buttons.size > 1) {
                        text = arrayListOf(card.buttons[1]).createSplitSpannable(Color.WHITE)
                        setOnClickListener {
                            Nav.toAmanLink(context, card.buttons[1].action)
                        }
                    }
                }
                /** ----------------------标题旁边的图片模块----------------------**/
                getView<ImageView>(R.id.start_image).apply {
                    isVisible = card.style == 0
                    if (card.style == 0) {
                        glideDisplay(card.images, R.color.color_D8D8D8)
                    }
                }
                getView<ImageView>(R.id.end_image).apply {
                    isVisible = card.style == 1
                    if (card.style == 1) {
                        glideDisplay(card.images, R.color.color_D8D8D8)
                    }
                }
                getView<ImageView>(R.id.top_image).apply {
                    isVisible = card.style == 2
                    if (card.style == 2) {
                        glideDisplay(card.images, R.color.color_D8D8D8)
                    }
                }
                getView<ImageView>(R.id.bottom_image).apply {
                    isVisible = card.style == 3
                    if (card.style == 3) {
                        glideDisplay(card.images, R.color.color_D8D8D8)
                    }
                }

                if (card.action.isNotEmpty()) {
                    itemView.findViewById<View>(R.id.im_message_container)?.setOnClickListener {
                        GrowingUtil.track("function_click", "function_name", "测试", "function_page", functionPage, "source", data.source, "status", status, "click_type", clickType)
                        Nav.toAmanLink(itemView.context, card.action)
                    }
                }
            }
        }
    }

}
