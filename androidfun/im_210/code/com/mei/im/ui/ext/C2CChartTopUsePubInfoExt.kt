package com.mei.im.ui.ext

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.mei.base.ui.nav.Nav
import com.mei.im.ui.view.C2CChartTopUsePubInfo
import com.mei.live.LiveEnterType
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.formatOneContribute
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.orc.Cxt
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.net.MeiUser
import com.net.model.chick.report.ChatC2CPubInfo
import com.net.model.chick.report.ChatC2CUseInfo
import kotlinx.android.synthetic.main.chat_c2c_im_audience.view.*
import kotlinx.android.synthetic.main.chat_c2c_im_live.view.*
import kotlinx.android.synthetic.main.chat_c2c_im_work_room_info.view.*

/**
 * IM顶部栏
 */

/**
 * 主播信息
 */
fun C2CChartTopUsePubInfo.initPublisher(info: ChatC2CPubInfo.InfoBean) {
    c2c_im_live.setOnClickListener {
        Nav.toPersonalPage(context, info.userId)
    }
    message_header_item_header.glideDisplay(info.userInfo?.avatar.orEmpty(), info.userInfo?.gender.genderAvatar())

    if (info.publisherTags.isEmpty()) {
        info.skills.forEach {
            labelList.add(it.name)
        }
    } else {
        info.publisherTags.forEach {
            labelList.add(it)
        }
    }
    id_flowlayout_zhubo?.adapter = labelAdapter
    tv_user_jieshao?.text = if (info.introduction.isNullOrEmpty()) {
        "亲爱的你是否有一些心事？来跟我聊聊吧"
    } else {
        info.introduction.replace("\n", " ")
    }

    when (info.onlineStatus) {
        2 -> {
            //在线中
            message_header_item_header_line1.isVisible = false
            message_header_item_header_line3.isVisible = false
            message_header_item_header_line2.isVisible = false
            online_staus.isVisible = true
            shimmer_frame.isVisible = false
            message_header_item_header_living_bg.visibility = View.VISIBLE
            personal_avatar.setBackgroundResource(0)
            online_staus.background = ContextCompat.getDrawable(context, R.drawable.icon_on_line_status)
            round_boder.delegate.strokeColor = Color.TRANSPARENT
            round_boder.delegate.strokeWidth = 0
            round_boder.setBackgroundColor(Cxt.getColor(android.R.color.white))
            personal_live_status.isVisible = false
            c2c_im_live.setOnClickListener {
                Nav.toPersonalPage(context, info.userId)
            }

        }
        1 -> {
            //直播中
            message_header_item_header_line1.isVisible = true
            shimmer_frame.isVisible = true
            online_staus.isVisible = false
            val headerBG = message_header_item_header_bg
            message_header_item_header_living_bg.isVisible = true
            headerBG.mStartColor = Color.parseColor("#FFA00A")
            headerBG.mEndColor = Color.parseColor("#FF5454")
            fl_pub_head_audience.setOnClickListener {
                VideoLiveRoomActivityLauncher.startActivity(context, info.roomId, LiveEnterType.im_c2c_private_page)
            }
            ly_frame_info.setOnClickListener {
                Nav.toPersonalPage(context, info.userId)
            }
            round_boder.delegate.strokeColor = Color.parseColor("#FFA00A")
            personal_avatar.setBackgroundResource(0)
            personal_live_status.delegate.applyViewDelegate {
                strokeWidth = 1.dp
                strokeStartColor = ContextCompat.getColor(context, android.R.color.white)
                startColor = ContextCompat.getColor(context, R.color.color_FFB00A)
                endColor = ContextCompat.getColor(context, R.color.color_FF8449)
                radius = 12.dp
            }
            personal_live_status.text = "直播中"
            personal_live_status.isVisible = true
        }
        else -> {
            //不在线
            online_staus.isVisible = false
            personal_live_status.isVisible = false
            message_header_item_header_line1.isVisible = false
            message_header_item_header_line3.isVisible = false
            message_header_item_header_line2.isVisible = false
            shimmer_frame.isVisible = true
            message_header_item_header_living_bg.isVisible = false
            personal_avatar.setBackgroundResource(0)
            round_boder.delegate.strokeColor = Color.TRANSPARENT
            round_boder.delegate.strokeWidth = 0
            personal_live_status.delegate.applyViewDelegate { startColor = ContextCompat.getColor(context, android.R.color.white) }
            round_boder.setBackgroundColor(Cxt.getColor(android.R.color.white))
            c2c_im_live.setOnClickListener {
                Nav.toPersonalPage(context, info.userId)
            }
        }
    }
    info.userInfo?.let {
        c2c_im_live.isVisible = true
    }

}

/**
 * 工作室信息
 */
fun C2CChartTopUsePubInfo.initGroupInfo(info: ChatC2CPubInfo.InfoBean) {
    when (info.onlineStatus) {
        1 -> {
            work_room_page_rfl.isVisible = false
            work_room_living_fl.isVisible = true
        }
        else -> {
            work_room_page_rfl.isVisible = true
            work_room_living_fl.isVisible = false
        }
    }
    //工作室显示信息逻辑处理
    c2c_im_live.isVisible = info.userInfo?.groupInfo == null
    work_room_header_cl.isVisible = info.userInfo?.groupInfo != null
    work_room_img_riv.glideDisplay(info.userInfo?.groupInfo?.avatar, R.drawable.default_avatar_50, R.drawable.default_avatar_50)
    living_img_iv.glideDisplay(info.personalImage, if (info.userInfo.gender == 1) R.drawable.icon_living_man else R.drawable.icon_living_women)
    work_room_name_tv.text = info.userInfo?.groupInfo?.groupName.orEmpty()
    work_room_living_fl.setOnClickListener {
        if (info.onlineStatus == 1) {
            VideoLiveRoomActivityLauncher.startActivity(context, info.roomInfo?.roomId, LiveEnterType.im_c2c_private_page)
        }
    }
    work_room_header_cl.setOnClickListener {
        Nav.toPersonalPage(context, info.userId)
    }
}

/**
 * 非主播信息
 */
@SuppressLint("SetTextI18n")
fun C2CChartTopUsePubInfo.initNoPublisher(chatC2CUseInfo: ChatC2CUseInfo) {
    val info = chatC2CUseInfo.info
    fl_head_audience.glideDisplay(info.avatar.orEmpty(), R.drawable.default_avatar_50, info.gender.genderAvatar(info.isIsPublisher))
    tv_level.apply {
        setPadding(0, 0, if (info.userLevel > 9) dip(5) else dip(9), 0)
        isVisible = info.userLevel > 0
        text = info.userLevel.toString()
        setBackgroundResource(getBackGroundLevelResource(info.userLevel, LevelSize.Normal))
    }
    iv_online_status.isVisible = info.onlineStatus == 2
    val service = if (MeiUser.getSharedUser().info?.groupInfo != null) {
        chatC2CUseInfo.info.medalMap.orEmpty()[MeiUser.getSharedUser().info?.groupInfo?.groupId.toString()]
    } else {
        chatC2CUseInfo.info.medalMap.orEmpty()[JohnUser.getSharedUser().userID.toString()]
    }

    iv_service_representation.isVisible = service != null
    iv_service_representation.text = service?.medal.orEmpty()
    if (info.gender == 1) {
        user_extra_tv.text = "男 ${info.birthYear.orEmpty()}"
        user_extra_tv.setBackColor(Color.parseColor("#6995ff"))
    } else {
        user_extra_tv.text = "女 ${info.birthYear.orEmpty()}"
        user_extra_tv.setBackColor(Color.parseColor("#FF69B0"))
    }
    info.interests.forEach {
        labelList.add(it.name)
    }
    info.sendMeCount.let {
        show_xinbi.text = "贡献心币"
        tv_xin_coin.text = formatOneContribute(info.sendCoinCount.toLong(), "w")
    }
    info.sendCoinCount.let {
        send_xinbi.text = "送我心币"
        tv_give_me_coin.text = formatOneContribute(info.sendMeCount.toLong(), "w")
    }
    info.upstreamCount.let {
        connect_xinbi.text = "连线次数"
        tv_give_me_conet.text = formatOneContribute(info.upstreamCount.toLong(), "w")
    }

    id_flowlayout_no_zhubo?.adapter = labelAdapter
    info.let {
        c2c_im_audience.isVisible = true
    }

}