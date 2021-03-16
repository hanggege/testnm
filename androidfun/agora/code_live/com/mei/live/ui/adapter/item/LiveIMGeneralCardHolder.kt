package com.mei.live.ui.adapter.item

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.message.CustomMessage
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.live.ext.parseColor
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showUserInfoDialog
import com.mei.live.ui.fragment.LiveIMSplitFragment
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.util.string.getInt
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.extensions.appendLink

/**
 * Created by hang on 2020/11/4.
 */
class LiveIMGeneralCardHolder(val fragment: LiveIMSplitFragment, val itemView: View) : BaseViewHolder(itemView) {
    fun refresh(item: CustomMessage) {
        //服务类型
        val data = item.customInfo?.data as? ChickCustomData
        if (data != null) {
            //殷浩说1000以内的消息取userid
            val userId = if (item.sender.getInt() <= 1000) data.userId else item.sender.getInt(0)
            val userInfo = getCacheUserInfo(userId) ?: getCacheUserInfo(data.userId)
            loadUserInfo(fragment, userInfo, data)

            getView<RoundImageView>(R.id.user_avatar_img).apply {
                glideDisplay(userInfo?.avatar.orEmpty(), userInfo?.gender.genderAvatar(userInfo?.isPublisher))
                setOnClickListener {
                    fragment.activity.showUserInfoDialog(fragment.roomInfo, userInfo?.userId
                            ?: 0) { type, _ ->
                        if (type == 0) {
                            (fragment.activity as? VideoLiveRoomActivity)?.showInputKey(userInfo)
                        }
                    }
                }
            }

            val card = data.card
            if (card != null) {
                GrowingUtil.track("function_view", "function_name", "测试", "function_page", "直播间", "source", "主播推荐测试", "status", "测试卡片", "click_type", "N/A")
                /** ----------------------title模块----------------------**/
                val cardTitle = getView<TextView>(R.id.card_title)
                cardTitle.paint.isFakeBoldText = true
                cardTitle.text = buildSpannedString {
                    card.title.forEach {
                        appendLink(it.text, it.color.parseColor(Cxt.getColor(R.color.color_333333)))
                    }
                }

                /** ----------------------subtitle模块----------------------**/
                val cardSubtitle = getView<TextView>(R.id.card_subtitle)
                cardSubtitle.isVisible = card.subTitle.isNotEmpty()
                cardSubtitle.text = buildSpannedString {
                    card.subTitle.forEach {
                        appendLink(it.text, it.color.parseColor(Cxt.getColor(R.color.color_333333)), it.fontScale)
                    }
                }

                /** ----------------------按钮模块----------------------**/
                getView<LinearLayout>(R.id.button_layout).isVisible = card.buttons.isNotEmpty()
                getView<TextView>(R.id.end_btn).apply {
                    isVisible = card.buttons.isNotEmpty()
                    if (card.buttons.isNotEmpty()) {
                        text = buildSpannedString {
                            appendLink(card.buttons[0].text, card.buttons[0].color.parseColor(Color.WHITE))
                        }
                        setOnClickListener {
                            Nav.toAmanLink(context, card.buttons[0].action)
                        }
                    }
                }
                getView<TextView>(R.id.start_btn).apply {
                    isVisible = card.buttons.size > 1
                    if (card.buttons.size > 1) {
                        text = buildSpannedString {
                            appendLink(card.buttons[1].text, card.buttons[1].color.parseColor(Color.WHITE))
                        }
                        setOnClickListener {
                            Nav.toAmanLink(context, card.buttons[1].action)
                        }
                    }
                }
                /** ----------------------标题旁边的图片模块----------------------**/
                getView<ImageView>(R.id.start_image).apply {
                    isVisible = card.style == 1
                    if (card.style == 1) {
                        glideDisplay(card.images, R.color.color_D8D8D8)
                    }
                }
                getView<ImageView>(R.id.end_image).apply {
                    isVisible = card.style == 0
                    if (card.style == 0) {
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
                    getView<LinearLayout>(R.id.live_msg_content_frame).setOnClickListener {
                        GrowingUtil.track("function_click", "function_name", "测试", "function_page", "直播间",
                                "source", "主播推荐测试", "status", "测试卡片", "click_type", "测试卡片")
                        Nav.toAmanLink(fragment.context, card.action)
                    }
                }
            }

        }
    }
}