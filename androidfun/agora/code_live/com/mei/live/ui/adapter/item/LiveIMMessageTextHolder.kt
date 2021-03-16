package com.mei.live.ui.adapter.item

import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.message.CustomMessage
import com.mei.base.ui.nav.Nav
import com.mei.init.spiceHolder
import com.mei.live.ext.appendSplitSpannable
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showCommonBottomDialog
import com.mei.live.ui.dialog.showUserInfoDialog
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.live.ui.fragment.LiveIMSplitFragment
import com.mei.live.ui.fragment.upstreamUserIds
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.util.string.getInt
import com.mei.widget.gradient.GradientTextView
import com.mei.widget.gradient.applyViewDelegate
import com.mei.widget.holder.addOnLongClickListener
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.extensions.appendLink
import com.mei.wood.extensions.buildFirstIndent
import com.mei.wood.extensions.buildLink
import com.net.model.room.RoomType

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/6/2
 */
class LiveIMMessageTextHolder(val fragment: LiveIMSplitFragment, val itemView: View) : BaseViewHolder(itemView) {

    fun refresh(item: CustomMessage) {
        val holder = this
        //服务类型
        val data = item.customInfo?.data as? ChickCustomData
        if (data != null) {
            //殷浩说1000以内的消息取userid
            val userId = if (item.sender.getInt() <= 1000) data.userId else item.sender.getInt(0)
            val userInfo = getCacheUserInfo(userId) ?: getCacheUserInfo(data.userId)
            if (userInfo == null) spiceHolder().apiSpiceMgr.requestUserInfo(arrayOf(userId))
            val isLiveCreator = fragment.roomInfo.createUser?.userId == userInfo?.userId
            // 专属房连麦用户发送的消息且自己是知心达人
            val exclusiveLivingUser = fragment.activity.upstreamUserIds()
                    .filterNot { fragment.roomInfo.createUser?.userId == it }
                    .any { it == userInfo?.userId }
                    && fragment.roomInfo.roomType == RoomType.EXCLUSIVE
                    && fragment.roomInfo.isCreator

            val serviceRepresentationView = holder.getView<TextView>(R.id.live_room_message_service).apply {
                val model = if (fragment.roomInfo.isStudio) {
                    userInfo?.medalMap.orEmpty()[fragment.roomInfo.groupInfo?.groupId.toString()]
                } else {
                    userInfo?.medalMap.orEmpty()[fragment.roomInfo.publisherId.toString()]
                }
                isVisible = model != null
                text = model?.medal.orEmpty()
            }
            val roleTa = holder.getView<TextView>(R.id.live_user_role_ta).apply {
                isVisible = userInfo?.roomRole == 3
            }
            holder.getView<GradientTextView>(R.id.live_user_role_view).apply {
                isVisible = (isLiveCreator || exclusiveLivingUser) && !data.userOfficialAvatar
                text = if (isLiveCreator) {
                    if (fragment.roomInfo.isStudio) "主讲人" else if (fragment.roomInfo.isSpecialStudio) "咨询师" else "知心达人"
                } else "连麦中"
                isSelected = isLiveCreator
                delegate.applyViewDelegate {
                    if (isSelected) {
                        startColor = ContextCompat.getColor(context, R.color.color_FF9E40)
                        endColor = ContextCompat.getColor(context, R.color.color_FE6B10)
                    } else {
                        startColor = ContextCompat.getColor(context, R.color.color_74DD49)
                        endColor = ContextCompat.getColor(context, R.color.color_5BBE57)
                    }
                    radius = 8.dp
                }
                holder.getView<RoundImageView>(R.id.user_avatar_img).apply {
                    if (data.userOfficialAvatar) {
                        glideDisplay(R.drawable.official_avatar)
                    } else {
                        glideDisplay(userInfo?.avatar.orEmpty(), userInfo?.gender.genderAvatar(userInfo?.isPublisher))
                    }
                    setOnClickListener {
                        if (!data.userOfficialAvatar) {
                            fragment.activity.showUserInfoDialog(fragment.roomInfo, userInfo?.userId
                                    ?: 0) { type, info ->
                                if (type == 0) {
                                    (fragment.activity as? VideoLiveRoomActivity)?.let {
                                        it.apiSpiceMgr.requestUserInfo(arrayOf(info.userId), true) { list ->
                                            list.firstOrNull()?.let { info ->
                                                it.showInputKey(info)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            val studioMemberIcon = holder.getView<ImageView>(R.id.icon_studio_member)
            studioMemberIcon.isVisible = userInfo?.groupId ?: 0 > 0 && !data.userOfficialAvatar

            val iosUserIcon = holder.getView<ImageView>(R.id.live_ios_user_icon)
            iosUserIcon.isVisible = fragment.roomInfo.roomRole != 0 && (userInfo?.clientType == "iOS" || userInfo?.clientType == "MP") && !data.userOfficialAvatar

            if(iosUserIcon.isVisible){
                if(userInfo?.clientType == "iOS"){
                    iosUserIcon.setBackgroundResource(R.drawable.icon_ios_user)
                }else{
                    iosUserIcon.setBackgroundResource(R.drawable.icon_mp_user)
                }
            }

            val roleView = holder.getView<GradientTextView>(R.id.live_user_role_view).apply {
                isVisible = (isLiveCreator || exclusiveLivingUser) && !data.userOfficialAvatar
                text = if (isLiveCreator) {
                    if (fragment.roomInfo.isStudio) "主讲人" else if (fragment.roomInfo.isSpecialStudio) "咨询师" else "知心达人"
                } else "连麦中"
                isSelected = isLiveCreator
                delegate.applyViewDelegate {
                    if (isSelected) {
                        startColor = ContextCompat.getColor(context, R.color.color_FF9E40)
                        endColor = ContextCompat.getColor(context, R.color.color_FE6B10)
                    } else {
                        startColor = ContextCompat.getColor(context, R.color.color_74DD49)
                        endColor = ContextCompat.getColor(context, R.color.color_5BBE57)
                    }
                    radius = 8.dp
                }
            }
            val levelView = holder.getView<TextView>(R.id.level_text).apply {
                isVisible = !isLiveCreator && userInfo != null && userInfo.userLevel > 0 && !data.userOfficialAvatar
                if (userInfo != null) {
                    setPadding(0, 0, if (userInfo.userLevel > 9) dip(5) else dip(9), 0)
                    text = userInfo.userLevel.toString()
                    setBackgroundResource(getBackGroundLevelResource(userInfo.userLevel, LevelSize.Normal))
                }
            }
            val mentorView = holder.getView<ImageView>(R.id.live_mentor_tag_img).apply {
                isVisible = !isLiveCreator && userInfo?.isPublisher == true && userInfo.groupRole == 0 && !data.userOfficialAvatar
            }

            addOnLongClickListener(R.id.live_msg_menu, R.id.live_msg_content_tv)
            holder.getView<TextView>(R.id.live_msg_content_tv).let { contentTV ->
                contentTV.text = buildSpannedString {
                    if (exclusiveLivingUser) {
                        appendLink(data.summary, Color.parseColor("#FFCA00"))
                    } else {
                        appendSplitSpannable(data.content, Color.WHITE) {
                            Nav.toAmanLink(contentTV.context, it)
                        }
                        data.at.forEach {
                            buildLink("@ ${it.userName} ", Color.parseColor("#FFE5A0"))
                        }
                    }
                    var firstIndent = 0
                    if (roleTa.isVisible) firstIndent += fragment.dip(32)
                    if (roleView.isVisible) firstIndent += fragment.dip(40)
                    if (levelView.isVisible) firstIndent += fragment.dip(38)
                    if (mentorView.isVisible) firstIndent += fragment.dip(20)
                    if (serviceRepresentationView.isVisible) firstIndent += fragment.dip(55)
                    if (iosUserIcon.isVisible) firstIndent += fragment.dip(19)
                    if (studioMemberIcon.isVisible) firstIndent += fragment.dip(19)
                    buildFirstIndent(firstIndent)
                }
                contentTV.movementMethod = LinkMovementMethod.getInstance()
            }
            holder.getView<View>(R.id.live_msg_coupon).apply {
                isVisible = data.couponInfo != null
                        && (data.couponInfo?.publisherId == (fragment.activity as? VideoLiveRoomActivity)?.roomInfo?.publisherId)
                setOnClickListener {
                    (fragment.activity as? VideoLiveRoomActivity)?.apply {
                        if (roomInfo.isCreator) {
                            showUserInfoDialog(roomInfo, userId)
                        } else {
                            showCommonBottomDialog(data.couponInfo?.action.orEmpty())
                        }
                    }
                }
            }
        }
    }
}