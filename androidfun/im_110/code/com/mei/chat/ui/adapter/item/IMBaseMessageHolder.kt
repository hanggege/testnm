package com.mei.chat.ui.adapter.item

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.Message
import com.joker.im.custom.CustomInfo
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.message.CustomMessage
import com.mei.base.ui.nav.Nav
import com.mei.init.spiceHolder
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.date.amountDays
import com.mei.orc.util.date.amountYears
import com.mei.orc.util.date.formatToString
import com.mei.orc.util.string.getInt
import com.mei.widget.holder.quickAdapter
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.cache.requestUserInfo
import com.net.MeiUser
import com.tencent.imsdk.TIMConversationType
import java.util.*
import kotlin.math.abs

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/13
 */

fun chatTimeString(timeStamp: Long): String {
    if (timeStamp <= 0) return ""
    val date = Date(timeStamp * 1000)
    val days = date.amountDays(Date())
    val year = abs(date.amountYears(Date()))
    val betweenSecond = abs(System.currentTimeMillis() / 1000 - timeStamp)
    val formatStr = when {
        betweenSecond < 2 * 60 -> "刚刚"
        days == 0 -> "HH:mm"
        days == -1 -> "昨天 HH:mm"
        days == -2 -> "前天 HH:mm"
        year == 0 -> "MM月dd日"
        else -> "yy年MM月dd日"
    }
    return date.formatToString(formatStr)
}

abstract class IMBaseMessageHolder(itemView: View) : BaseViewHolder(itemView) {
    var message: Message? = null
    var playVoicePosition = -1

    fun refresh(msg: Message, playPostion: Int) {
        message = msg
        playVoicePosition = playPostion
        initEvent()
        refreshBase(msg)
        refreshChild(msg)
    }

    private fun initEvent() {
        itemView.findViewById<View>(R.id.im_message_container)?.setOnClickListener {
            quickAdapter?.apply {
                getOnItemChildClickListener()?.onItemChildClick(this, it, bindingAdapterPosition)
            }
        }
        itemView.findViewById<View>(R.id.im_message_container)?.setOnLongClickListener {
            quickAdapter?.apply {
                getOnItemChildLongClickListener()?.onItemChildLongClick(this, it, bindingAdapterPosition)
            }
            true
        }
    }

    abstract fun refreshChild(msg: Message)


    open fun refreshBase(msg: Message) {
        itemView.findViewById<TextView>(R.id.im_time_stamp)?.apply {
            text = chatTimeString(msg.timMessage.timestamp())
            isVisible = msg.hasTime
        }

        val avatarIV = itemView.findViewById<RoundImageView>(R.id.avatar_img)

        val data = getCustomData()?.data as? ChickCustomData
        val senderId = if (data != null && data.userId > 0) data.userId else msg.sender.getInt()
        avatarIV?.apply {
            Glide.with(itemView).clear(this)
            setOnClickListener {
                if (!msg.isSelf) {
                    when (msg.chatType) {
//                        TIMConversationType.Group -> if (MeiUser.getSharedUser().isMentor) Nav.toUserActivity(itemView.context, msg.sender.getInt())
                        TIMConversationType.C2C -> {
                            if (context is VideoLiveRoomActivity) {

                            } else getCacheUserInfo(senderId)?.let { info ->
                                if (info.isPublisher) {
                                    Nav.toPersonalPage(context, info.userId)
                                } else if (senderId > 1000) {
                                    UIToast.toast(context, "该用户非知心达人")
                                }
                            }
                        }
                        else -> {
                        }
                    }
                }
            }

            /** 这里的用户信息在进入时需要提前缓存 **/
            if (msg.isSelf) {
                glideDisplay(MeiUser.getSharedUser().info?.avatar.orEmpty(), MeiUser.getSharedUser().info?.gender.genderAvatar(MeiUser.getSharedUser().info?.isPublisher))
            } else {
                val info = getCacheUserInfo(senderId)
                if (info == null) {
                    spiceHolder().apiSpiceMgr.requestUserInfo(arrayOf(senderId), true) {
                        it.firstOrNull().let { userInfo ->
                            glideDisplay(userInfo?.avatar.orEmpty(), userInfo?.gender.genderAvatar(userInfo?.isPublisher))
                        }
                    }
                } else {
                    glideDisplay(info.avatar.orEmpty(), info.gender.genderAvatar(info.isPublisher))
                }
            }
        }
    }


    /**
     * 获取自定义对象
     */
    fun getCustomData(): CustomInfo<*>? = (message as? CustomMessage)?.customInfo

}