@file:Suppress("CAST_NEVER_SUCCEEDS")

package com.mei.live.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.SplitText
import com.joker.im.message.CustomMessage
import com.mei.agora.leaveLiveRoom
import com.mei.base.common.APPLY_UPSTREAM_STATE
import com.mei.base.common.HAS_BLACKLIST
import com.mei.dialog.PayFromType
import com.mei.dialog.myRose
import com.mei.im.checkIMRefresh
import com.mei.im.ext.isCmdId
import com.mei.im.ui.dialog.showChatDialog
import com.mei.live.ext.createSplitSpannable
import com.mei.live.manager.liveSendCustomMsg
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.*
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.live.ui.ext.shieldingDialog
import com.mei.live.views.showLiveIMBubblePopWindow
import com.mei.message.SYSTEM_NOTIFY_USER_ID
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.widget.gradient.GradientTextView
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.logDebug
import com.net.MeiUser
import com.net.model.room.QueueMy
import com.net.model.room.RoomType
import com.net.model.rose.RoseFromSceneEnum
import com.net.model.user.UserInfo
import com.net.network.exclusive.SpecialServiceRoomListRequest
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMManager
import kotlinx.android.synthetic.main.layout_live_im_split_bottom.*
import kotlinx.android.synthetic.main.live_im_split_layout.*


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-11
 */
@SuppressLint("SetTextI18n")
fun LiveIMSplitFragment.initEvent() {
    icon_question.setOnClickListener {
        val msgState = when (checkUserStatus()) {
            0 -> 0
            else -> {
                if (roomInfo.roomType == RoomType.EXCLUSIVE) 1
                else 0
            }
        }
        icon_question.showConsultQuestionPop(roomInfo.roomId, roomInfo.isCreator, msgState)
    }
    send_gift.setOnClickListener {
        /** 拉起送礼面板 **/
        getCacheUserInfo(roomInfo.publisherId)?.let {
            activity?.showSendGiftDialog(it.userId, it.nickname.orEmpty(), roomInfo.roomId, roseFromScene =
            if (roomInfo.roomType == RoomType.EXCLUSIVE) RoseFromSceneEnum.EXCLUSIVE_ROOM else RoseFromSceneEnum.COMMON_ROOM)
        }
    }
    //有未读消息时显示红点
    activity?.checkIMRefresh {
        val count = TIMManager.getInstance().conversationList.filter {
            it.type == TIMConversationType.C2C
                    && it.peer != SYSTEM_NOTIFY_USER_ID
                    && !it.peer.toIntOrNull().isCmdId()
        }
                .sumBy { it.unreadMessageNum.toInt() }
        icon_im_enter_tip?.isVisible = count > 0
    }
    chat_message.setOnClickNotDoubleListener {
        //私信聊天入口
        activity.showChatDialog(from = "RoomConversation")
    }
//    apply_up_mic.setOnClickNotDoubleListener {
//        activity?.performApply()
//    }
    show_keyboard_view.setOnClickListener {
        showKeyBoard()
    }

    unread_message_hint.setOnClickListener {
        unReadMessageNum = 0
        (im_recycler_view.layoutManager as? LinearLayoutManager)?.scrollToPosition(msgList.size - 1)
    }
    im_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            unread_message_hint.isVisible = dy < 0 && unReadMessageNum > 0
            if (dy > 0) {
                unReadMessageNum = 0
            }
        }
    })

    more_operate.setOnClickListener {
        activity?.showShieldMangerDialog(roomInfo.roomId, roomInfo.moreMenu.orEmpty())
    }

    exclusive_service.setOnClickListener {
        val specialServiceId = roomInfo.recommend?.specialServiceId ?: 0
        val courseId = roomInfo.recommendCourse?.courseId ?: 0
        val serviceId = when {
            specialServiceId > 0 -> specialServiceId
            courseId > 0 -> courseId
            else -> 0
        }
        if (serviceId > 0) {
            val loadUrl = activity?.appendRoomHalfScreenParamsUrl(MeiUtil.appendParamsUrl(AmanLink.NetUrl.exclusive_service,
                    "service_id" to serviceId.toString(), "publisher_id" to roomInfo.publisherId.toString()), roomInfo).orEmpty()
            activity?.showExclusiveServiceDialog(loadUrl, roomInfo.roomId)
        }
    }
}

fun LiveIMSplitFragment.checkLiveInitIM(delay: Long = 1500) {
    try {
        if (isAdded && roomInfo.welcomeItems.orEmpty().isNotEmpty()) {
            runDelayedOnUiThread(delay) {
                roomInfo.welcomeItems = roomInfo.welcomeItems.orEmpty().sortedBy { it.after }.toMutableList()
                roomInfo.welcomeItems.firstOrNull()?.apply {
                    roomInfo.welcomeItems.removeAt(0)
                    val contentList = content
                    imWelcomeRunnable = Runnable {
                        val msg = CustomMessage(ChickCustomData.Result(CustomType.send_text, ChickCustomData().apply {
                            roomType = roomInfo.roomType.name
                            roomId = roomInfo.roomId
                            content = contentList

                        })).apply {
                            sender = roomInfo.createUser?.userId.toString()
                        }
                        msgList.add(msg)
                        if (isAdded) {
                            imAdapter.notifyDataSetChanged()
                            val layoutManager = im_recycler_view.layoutManager as? LinearLayoutManager
                            val lastVisiblePosition = layoutManager?.findLastVisibleItemPosition()
                                    ?: 0
                            if (msgList.size > 8 && msgList.size - lastVisiblePosition < 8) {
                                im_recycler_view.scrollToPosition(msgList.size - 1)
                            }
                        }
                        checkLiveInitIM(50)
                    }
                    if (isAdded) im_recycler_view.postDelayed(imWelcomeRunnable, after * 1000L)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun LiveIMSplitFragment.adapterItemChildLongClick(msg: CustomMessage, view: View) {
    /** 当前直播红娘与超管才有权限删除消息与禁言用户 **/
    if (roomInfo.roomRole > 0 || roomInfo.groupRole > 0) {
        val userId = (if (msg.sender.toIntOrNull().isCmdId()) {
            (msg.customInfo?.data as? ChickCustomData)?.userId
        } else msg.sender.toIntOrNull()) ?: 0

        apiSpiceMgr.requestUserInfo(arrayOf(userId)) {
            it.firstOrNull()?.let { userInfo ->
                if (hasEditPermission(userInfo)) {
                    blockUser(view, msg, userInfo)
                }
            }
        }
    }
}

/**
 * 当前用户对指定消息用户是否有操作权限
 */
private fun LiveIMSplitFragment.hasEditPermission(userInfo: UserInfo): Boolean {
    when {
        roomInfo.roomRole == 2 -> {
            return true
        }
        userInfo.roomRole == 2 -> {
            return false
        }
        else -> {
            when {
                roomInfo.isSpecialStudio -> {
                    return roomInfo.isCreator || roomInfo.groupRights and 8 != 0
                }
                roomInfo.isStudio -> {
                    return if (roomInfo.isCreator || roomInfo.groupRights and 8 != 0) {
                        true
                    } else if (roomInfo.groupRights and 1 != 0) {
                        userInfo.groupId != roomInfo.groupInfo?.groupId || (userInfo.userId != roomInfo.createUser?.userId && userInfo.groupRights and 8 == 0)
                    } else {
                        false
                    }
                }
                else -> {
                    return roomInfo.isCreator || (roomInfo.roomRole == 3 && userInfo.userId != roomInfo.createUser?.userId)
                }
            }
        }
    }
}

/**
 * 是否是普通用户(未区分普通直播间助教)
 */
private fun LiveIMSplitFragment.isCommonMember(userInfo: UserInfo): Boolean {
    if (userInfo.userId < 1000) {
        return false
    }
    return if (roomInfo.isCommonRoom) {
        userInfo.roomRole != 2 && userInfo.userId != roomInfo.createUser?.userId && userInfo.userId != JohnUser.getSharedUser().userID
    } else {
        userInfo.groupId != roomInfo.groupInfo?.groupId || userInfo.groupRole == 0
    }
}

/**
 * 当前用户对指定消息用户是否有拉黑权限
 */
private fun LiveIMSplitFragment.hasShieldPermission(userInfo: UserInfo): Boolean {
    return when {
        roomInfo.isSpecialStudio -> {
            (roomInfo.isCreator || roomInfo.groupRights and 8 != 0 || roomInfo.roomRole == 2) && isCommonMember(userInfo)
        }
        roomInfo.isStudio -> {
            (roomInfo.isCreator || roomInfo.groupRights and 8 != 0 || roomInfo.roomRole == 2 || roomInfo.groupRights and 1 != 0) && isCommonMember(userInfo)
        }
        else -> {
            roomInfo.roomRole > 0 && isCommonMember(userInfo)
        }
    }
}


private fun LiveIMSplitFragment.blockUser(view: View, msg: CustomMessage, userInfo: UserInfo) {
    val showView = if (view is ViewGroup) view else (view.parent as? View)

    /**比对方权限高并消息是可删除的*/
    val isShowDelete = (msg.customInfo?.data as? ChickCustomData)?.deleteEnable == true

    showView?.showLiveIMBubblePopWindow(isShowDelete, hasShieldPermission(userInfo)) { it ->
        if (it == 0) {
            /** 删除消息，包含用他用户的消息都需要删除 **/
            liveSendCustomMsg(roomInfo.roomId, CustomType.delete_message, applyData = {
                msgId = msg.timMessage.msgId
                roomId = roomInfo.roomId
                roomType = roomInfo.roomType.name
            }) {
                if (it == 0) {
                    UIToast.toast(activity, "删除成功")
                    //发送删除成功消息
                    val name = if (roomInfo.roomRole == 2) "知心官方" else MeiUser.getSharedUser().info?.nickname.orEmpty()
                    val contentList = arrayListOf(SplitText(name, "#FFA3E2FB"),
                            SplitText(" 删除了 ", "#B3FFFFFF"),
                            SplitText(userInfo.nickname.orEmpty(), "#FFA3E2FB"),
                            SplitText("的留言:", "#B3FFFFFF"))
                    (msg.customInfo?.data as? ChickCustomData)?.content.orEmpty().forEachIndexed { index, splitText ->
                        if (index > 0) {
                            contentList.add(splitText)
                        }
                    }
                    liveSendCustomMsg(roomInfo.roomId, CustomType.send_text, applyData = {
                        content = contentList
                        msgState = 4
                        roomId = roomInfo.roomId
                        roomType = roomInfo.roomType.name
                        deleteEnable = false
                        userOfficialAvatar = roomInfo.roomRole == 2
                    })

                }
            }
        } else if (it == 1) {
            /** 拉黑 **/
            activity?.shieldingDialog("是否确定拉黑 ${userInfo.nickname.orEmpty()}?", userInfo.userId.toString(), roomInfo.roomId, dialogMiss = {
                (activity as? MeiCustomBarActivity)?.apiSpiceMgr?.requestImUserInfo(arrayOf(userInfo.userId), needRefresh = true)
            })
        }
    }
}


/**
 * 开始真正的申请排麦
 */
fun VideoLiveRoomActivity.startApplyUpstream(option: QueueMy.Option, couponNum: Int) {
    fun showApplyDialog(callback: (Int, Int) -> Unit) {
        if (roomInfo.isCommonRoom) {
            showApplyPrivateUpstreamDialog(option, PayFromType.UP_APPLY) { back, isCloseVideo ->
                callback(back, if (isCloseVideo) 1 else 0)
            }
        } else {
            showApplyStudioUpstreamDialog(option) {
                callback(it, 1)
            }
        }
    }

    val applyType = if (roomInfo.isStudio) RoomType.COMMON else RoomType.EXCLUSIVE
    showApplyDialog { back, isCloseVideo ->
        if (back == 1) {
            applyUpStream(roomInfo.roomId, applyType, videoMode = isCloseVideo, couponNum = couponNum) { info ->
                if (info != null) {
                    postAction(APPLY_UPSTREAM_STATE, true)
                    pendingUpStream = true
                    /**申请成功弹窗*/
                    activity.showSingleBtnCommonDialog(info.title, info.subTitle, info.tips) {
                        it.dismissAllowingStateLoss()
                    }
                }
            }
        }
    }
}
//
//fun LiveIMSplitFragment.getApplyCountInitMicState() {
//    apiSpiceMgr.executeKt(PeopleCountRequest(roomInfo.roomId), success = { info ->
//        val rank = info.data?.applyRank ?: 0
//        val count = info.data?.applyCount ?: 0
//        (activity as? VideoLiveRoomActivity)?.pendingUpStream = rank > 0
//        logDebug("getApplyCountInitMicState", "applyCount:${count}")
//        notifyUpstreamBtnState(count)
//    })
//}


@SuppressLint("SetTextI18n")
fun LiveIMSplitFragment.handleIMEvent(msg: CustomMessage) {
    val type = msg.customMsgType
    logDebug("im_handleIMEvent: ${type.name}")
    (msg.customInfo?.data as? ChickCustomData)?.let { data ->
        when (type) {
            CustomType.delete_message -> {//远程删除消息
                if (data.userIds.isNotEmpty()) {
                    msgList.filter { data.userIds.contains(it.sender.toIntOrNull()) }.forEach {
                        it.removeMessage()
                        msgList.remove(it)
                    }
                } else if (data.msgId.isNotEmpty()) {
                    msgList.find { it.timMessage.msgId == data.msgId }?.apply {
                        removeMessage()
                        msgList.remove(this)
                    }
                }
                imAdapter.notifyDataSetChanged()
            }
            CustomType.exclusive_block_notify -> {
                /**拉黑,被拉黑用户关闭视频流输出，弹框提示退出直播间*/
                if ((data.targetUserId == JohnUser.getSharedUser().userID
                                || data.targetUserId == MeiUser.getSharedUser().info?.groupId)
                        && (roomInfo.createUser?.userId == data.userId
                                || roomInfo.groupInfo?.groupId == data.userId)
                        && data.userInfoChange?.status == 7) {
                    activity?.leaveLiveRoom()
                    postAction(HAS_BLACKLIST, "")
                }
            }
            CustomType.auto_kick_offline -> {
                when (data.reason) {
                    4 -> {
                        if (data.roomId == roomInfo.roomId && roomInfo.roomType == RoomType.COMMON && MeiUser.getSharedUser().info?.userId == data.targetUserId) {
                            //拉黑删除留言
                            msgList.filter { data.userIds.contains(it.sender.toIntOrNull()) }.forEach {
                                it.removeMessage()
                                msgList.remove(it)
                            }
                        }
                    }
                }
//                /** 被红娘踢下麦 **/
//                if (data.userIds.any { it == JohnUser.getSharedUser().userID }) {
//                    if (data.targetUserId == JohnUser.getSharedUser().userID) {
//                        chatMsgReceiver.identify = roomInfo.roomId
//                    }
//                }
            }
            CustomType.people_changed -> {
                if (data.reason == 4 && data.targetUserId == JohnUser.getSharedUser().userID) {
                    getServiceRefreshList()
                }
            }
            CustomType.user_info_change -> {
                /**
                 * 角色变化
                 */
                if (data.roomId == roomInfo.roomId) {
                    apiSpiceMgr.requestUserInfo(arrayOf(data.targetUserId), true) {
                        if (it.isNotEmpty()) imAdapter.notifyDataSetChanged()
                    }
                    if (data.targetUserId == JohnUser.getSharedUser().userID) {
                        //通知自己刷新消息
                        roomInfo.roomRole = data.userInfoChange?.roomRole ?: roomInfo.roomRole
                        chatMsgReceiver.showMessage(arrayListOf(msg), true)
                    } else if (roomInfo.roomRole != 0) {
                        //通知主播，房管，超管刷新消息
                        chatMsgReceiver.showMessage(arrayListOf(msg), true)
                    }
                }
            }
            CustomType.action_notify -> {
                /** 专属房连麦用户 **/
                val exclusiveSteamer = checkUserStatus() == 2 && roomInfo.roomType == RoomType.EXCLUSIVE
                if (data.roomId == roomInfo.roomId && !exclusiveSteamer) {
                    live_follow_container.apply {
                        apiSpiceMgr.requestUserInfo(arrayOf(data.userId)) {
                            isVisible = true
                            val liveUserRoleView = findViewById<GradientTextView>(R.id.live_user_role_view)
                            liveUserRoleView.apply {
                                liveUserRoleView.isSelected = true
                                delegate.applyViewDelegate {
                                    startColor = ContextCompat.getColor(context, R.color.color_FF9E40)
                                    endColor = ContextCompat.getColor(context, R.color.color_FE6B10)
                                    radius = 8.dp
                                }
                                isVisible = data.userId == roomInfo.createUser?.userId
                            }
                            val levelText = findViewById<TextView>(R.id.level_text)
                            val userInfo = it.firstOrNull()
                            if (userInfo != null) {
                                levelText.isVisible = liveUserRoleView.isGone && userInfo.userLevel > 0
                                levelText.setPadding(0, 0, if (userInfo.userLevel > 9) dip(5) else dip(10), 0)
                                levelText.text = userInfo.userLevel.toString()
                                levelText.setBackgroundResource(getBackGroundLevelResource(userInfo.userLevel, LevelSize.Normal))
                            } else {
                                levelText.isVisible = false
                            }
                            leaveRunnable?.let { removeCallbacks(it) }
                            clearAnimation()
                            findViewById<TextView>(R.id.live_follow_anim_tv).text = data.content.createSplitSpannable(Color.WHITE)
                            doOnLayout {
                                val width = -measuredWidth - dip(10).toFloat()
                                translationX = width
                                animate().translationX(0f).setDuration(500).start()
                                leaveRunnable = Runnable {
                                    if (isAdded) animate().translationX(-measuredWidth - dip(10).toFloat()).setDuration(500).start()
                                }
                                postDelayed(leaveRunnable, 4000)
                            }
                        }

                    }
                }
            }
            CustomType.room_type_change -> {
                if (data.roomId == roomInfo.roomId) {
                    if (checkUserStatus() == 2 && RoomType.parseValue(data.roomType) != roomType) {
                        msgList.clear()
                        if (RoomType.parseValue(data.roomType) == RoomType.COMMON) chatMsgReceiver.identify = roomInfo.roomId
                    }
                    getServiceRefreshList()
                    roomType = RoomType.parseValue(data.roomType)
                    imAdapter.notifyDataSetChanged()
                }
            }
            CustomType.user_level_change -> {
                if (data.roomId == roomInfo.roomId) {
                    apiSpiceMgr.requestUserInfo(arrayOf(data.userId), true) {
                        if (it.isNotEmpty()) imAdapter.notifyDataSetChanged()
                    }
                }
            }
            CustomType.user_balance_changed -> {
                //钱包余额变化通知客户端刷新用户余额
                activity.myRose()
            }
            CustomType.send_gift -> {
                //收到专属服务充值礼物
                if (data.roomId == roomInfo.roomId && data.gift?.giftId == RECHARGE_SERVICE_SUCCESS_ID) {
                    getServiceRefreshList()
                    apiSpiceMgr.requestUserInfo(arrayOf(data.userId), true) {
                        if (it.isNotEmpty()) imAdapter.notifyDataSetChanged()
                    }
                }
            }
            CustomType.coupon -> {
                val couponInfo = data.couponInfo
                if (data.roomId == roomInfo.roomId && couponInfo != null && data.userId == JohnUser.getSharedUser().userID) {
                    mPacketCouponInfo.coupons = couponInfo
                    setPacketCouponBannerData()
                }
            }
            else -> {
            }
        }
    }

}

/**
 * 获取用户角色
 * 0:普通用户，1：导师，2：连麦用户
 * roleId   0:用户，1：知心达人 2：管理员 3：助教
 * //msgState 消息类型，0：公开房间的消息，1：专属房间的消息,2:全部可见消息4知心达人，助教，超管可见，工作室公开直播间(有开播权限的主播)
 */
fun LiveIMSplitFragment.checkUserStatus(uid: Int = JohnUser.getSharedUser().userID) = when {
    roomInfo.isCreator || roomInfo.roomRole == 2 || roomInfo.groupRights and 8 != 0 -> 1
    activity.upstreamUserIds().any { it == uid } -> 2
    roomInfo.roomRole == 3 || (roomInfo.groupRights and 1 != 0 && roomInfo.isStudio) -> 3//助教
    else -> 0
}

fun LiveIMSplitFragment.checkMsgState(msg: CustomMessage): Boolean {
    val msgState = (msg.customInfo?.data as? ChickCustomData)?.msgState ?: 0
    return when (checkUserStatus()) {
        0 -> msgState == 0 || msgState == 2
        2 -> if (roomInfo.roomType == RoomType.EXCLUSIVE) msgState == 1 || msgState == 2 else msgState == 0 || msgState == 2
        3 -> msgState == 0 || msgState == 2 || msgState == 4//助教可以看到所有消息
        else -> true
    }
}

//上麦用户
fun Context?.upstreamUserIds() = (this as? VideoLiveRoomActivity)?.liveVideoSplitFragment?.upstreamUserIds
        ?: hashSetOf()

/**
 * 当前用户是否在麦上
 */
fun Context?.isUpstream() = upstreamUserIds().contains(JohnUser.getSharedUser().userID)


fun LiveIMSplitFragment.getServiceRefreshList() {
    if (activity.upstreamUserIds().none { it == JohnUser.getSharedUser().userID } && !tabBar.review) {
        apiSpiceMgr.executeKt(SpecialServiceRoomListRequest(JohnUser.getSharedUser().userID, roomInfo.publisherId, roomInfo.roomId), success = {
            it?.data?.service?.let { list ->
//                if (list.size > 0) {
//                    server_frame_container.setBannerData(list)
//                }
//                roomInfo.hasAvailableSpecialService = list.isNotEmpty()
//                server_frame_container.isVisible = list.isNotEmpty()
//                notifyUpstreamBtnState()
            }
        })
    }
}

fun LiveIMSplitFragment.setPacketCouponBannerData() {
    packetCouponList.clear()
    mPacketCouponInfo.coupons?.let {
        packetCouponList.add(it)
    }
//    packet_coupon_container.setBannerData(packetCouponList)
}
