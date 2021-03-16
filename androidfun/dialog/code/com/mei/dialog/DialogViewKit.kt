package com.mei.dialog

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import com.joker.im.custom.chick.ChickCustomData
import com.mei.live.manager.genderAvatar
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.choince.ChoiceView
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.extensions.appendLink
import com.mei.wood.ui.MeiCustomActivity

/**
 *  Created by zzw on 2019-11-29
 *  Des: 所需要的对话框view
 */

/**
 * 系统邀请弹窗
 */
fun getSystemInViteDateView(context: Activity, data: ChickCustomData, back: (Int) -> Unit = {}): View =
        FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val content = LayoutInflater.from(context).inflate(R.layout.view_system_invite_222, this, false)
            val userAvatar = content.findViewById<RoundImageView>(R.id.user_avatar_img)
            (context as? MeiCustomActivity)?.apiSpiceMgr?.requestUserInfo(arrayOf(data.userId)) {
                it.firstOrNull()?.let { userInfo ->
                    userAvatar.glideDisplay(userInfo.avatar.orEmpty(), userInfo.gender.genderAvatar(userInfo.isPublisher))
                }
            }

            val leftBtn = data.inviteUp?.leftText ?: "再想想"
            val rightBtn = data.inviteUp?.rightText ?: "立即连线"



            content.findViewById<TextView>(R.id.invite_title).apply {
                text = data.title
            }
            content.findViewById<TextView>(R.id.tv_context).apply {
                paint.isFakeBoldText = true
                isVisible = data.inviteUp?.title.orEmpty().isNotEmpty()
                text = data.inviteUp?.title.orEmpty()
            }
            content.findViewById<TextView>(R.id.invite_tag).apply {
                isVisible = data.inviteUp?.tag.orEmpty().isNotEmpty()
                text = data.inviteUp?.tag.orEmpty()
            }
            content.findViewById<TextView>(R.id.left_dialog_btn).apply {
                text = leftBtn
                setOnClickListener {
                    back(0)
                }
            }
            content.findViewById<TextView>(R.id.right_dialog_btn).apply {
                text = rightBtn
                setOnClickListener {
                    back(1)
                }
            }
            addView(content)
        }

/**
 * 保护弹窗
 */
fun getExclusiveProtectView(context: Context, price: Int, back: () -> Unit): View = FrameLayout(context).apply {
    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val contentView = LayoutInflater.from(context).inflate(R.layout.view_exclusive_protect, this, false)

    contentView.findViewById<TextView>(R.id.protect_tip).paint.isFakeBoldText = true

    contentView.findViewById<TextView>(R.id.protect_text).text = buildSpannedString {
        appendLink("连线全程仅知心达人可见，如需连线，请点击右下角申请", Cxt.getColor(R.color.color_333333))
        appendLink(" 私密连线 ", Cxt.getColor(R.color.color_FF3A3A))
        appendLink("收费为${price}心币/分钟", Cxt.getColor(R.color.color_333333))
    }
    contentView.findViewById<ChoiceView>(R.id.back_choice).setOnClickListener {
        back()
    }
    addView(contentView)
}




