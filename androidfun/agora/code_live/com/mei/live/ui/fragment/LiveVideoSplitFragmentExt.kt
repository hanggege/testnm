package com.mei.live.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.joker.TdType
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.SplitText
import com.joker.im.message.CustomMessage
import com.mei.GrowingUtil
import com.mei.agora.agoraConfig
import com.mei.agora.muteAllRemoteAudioStreams
import com.mei.agora.setClientRole
import com.mei.base.common.APPLY_UPSTREAM_ENABLE
import com.mei.base.common.HAS_BLACKLIST
import com.mei.base.common.UPSTREAM_ACTION
import com.mei.dialog.PayFromType
import com.mei.dialog.showPayDialog
import com.mei.im.ext.isCmdId
import com.mei.init.spiceHolder
import com.mei.live.LiveEnterType
import com.mei.live.ext.parseColor
import com.mei.live.manager.liveSendCustomMsg
import com.mei.live.manager.liveSendCustomMsg2
import com.mei.live.ui.LiveEndingActivityLauncher
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.dialog.*
import com.mei.live.ui.getRoomTypeForGrowingTrack
import com.mei.live.views.LiveVideoItemView
import com.mei.live.views.showUpstreamProtectHintPop
import com.mei.orc.Cxt
import com.mei.orc.event.postAction
import com.mei.orc.ext.*
import com.mei.orc.john.model.JohnUser
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.unit.TimeUnit
import com.mei.orc.util.click.isNotOnDoubleClick
import com.mei.orc.util.click.isOnDoubleClick
import com.mei.orc.util.date.formatTimeVideo
import com.mei.orc.util.permission.PermissionCheck
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialog
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.dialog.share.ShareData
import com.mei.wood.dialog.share.ShareManager
import com.mei.wood.dialog.share.wechat
import com.mei.wood.dialog.share.wechat_circle
import com.mei.wood.event.parseApplyType
import com.mei.wood.event.upstreamOutRoom
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.appendLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.logDebug
import com.net.MeiUser
import com.net.model.room.RoomApplyType
import com.net.model.room.RoomExit
import com.net.model.room.RoomInfo
import com.net.model.room.RoomType
import com.net.network.chat.ExclusiveHandleRequest
import com.net.network.room.*
import com.pili.player
import io.agora.rtc.Constants
import kotlinx.android.synthetic.main.fragment_live_video_split_layout.*
import kotlinx.android.synthetic.main.item_live_video_view.view.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-07
 */

fun LiveVideoSplitFragment.onBackPressed(isPhysicalBtn: Boolean) {
    when {
        agoraManager.isStreaming && upstreamUserIds.contains(JohnUser.getSharedUser().userID) -> {
            if (roomInfo.isCreator) {
                activity?.showDoubleBtnCommonView("您确认要关闭直播吗?", "", "当前人气: ${roomInfo.onlineCount}", leftBtn = "取消", rightBtn = "确定", isShowCountDown = false) { dialog, back ->
                    dialog.dismissAllowingStateLoss()
                    if (back == 1) {
                        LiveEndingActivityLauncher.startActivity(activity, roomInfo.roomId, roomInfo.broadcastId, anchor_video_layout.timeAccount)
                        activity?.finish()
                        clearTimer()
                    }
                }
            } else {
                activity?.showDoubleBtnCommonView("您当前正在连线中", "请选择", "", leftBtn = "退出直播间", rightBtn = "下麦", isShowCountDown = false, isHighLight = false) { dialog, back ->
                    dialog.dismissAllowingStateLoss()
                    if (back == 0) {
                        activity?.finish()
                        clearTimer()
                    } else if (back == 1) {
                        activity?.showEvaluationDialog(roomInfo.createUser?.userId
                                ?: 0, roomInfo.roomId)
                        stopStream()
                    }
                }
            }
        }
        (activity as? VideoLiveRoomActivity)?.pendingUpStream == true -> {
            activity?.showDoubleBtnCommonView("确认退出？", "当前还在排麦中\n退出直播间不影响排麦", "", leftBtn = "取消", rightBtn = "确定", isShowCountDown = false, isHighLight = false) { dialog, back ->
                dialog.dismissAllowingStateLoss()
                if (back == 1) {
                    activity?.finish()
                    clearTimer()
                }
            }
        }
        /**不是主播并且此用户连线过，需要弹起分享弹框*/
        !roomInfo.isCreator -> {
            apiSpiceMgr.executeKt(RoomExitFirstRequest(roomInfo.roomId), success = { response ->
                if (response != null && response.data != null && response.data?.backendGround?.isNotEmpty() == true) {
                    response.data.isExitRoom = true
                    GrowingUtil.track("push_popup_view", "popup_type", "连线关闭直播间分享弹窗", "view_type", if (response.data?.isFirstShare == true) "首次未分享" else "非首次未分享", "zx_push_popup_ab", "测试题${String.format("%02d", response.data.testNum)}",
                            "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
                    exitRoomShare(response)
                } else {
                    activity?.finish()
                    clearTimer()
                }
            }, failure = {
                activity?.finish()
                clearTimer()
            })

        }

        else -> {
            //物理按钮
            if (isPhysicalBtn) {
                //未连线时 提示再按一次退出房间
                if (isOnDoubleClick(3000)) {
                    activity?.finish()
                    clearTimer()
                } else {
                    UIToast.toast(activity, this.getString(R.string.two_back_finish_room))
                }
            } else {
                //布局按钮
                activity?.finish()
                clearTimer()
            }

        }
    }
}

private fun LiveVideoSplitFragment.exitRoomShare(it: RoomExit.Response) {
    fun getViewType(it: RoomExit.Response): String {
        return when {
            it.data.isExitRoom && it.data.isFirstShare -> "首次未分享 "
            it.data.isExitRoom && !it.data.isFirstShare -> "非首次未分享"
            !it.data.isExitRoom && it.data.isFirstShare -> " 首次分享成功"
            !it.data.isExitRoom && !it.data.isFirstShare -> " 非首次分享成功"
            else -> ""
        }
    }
    activity?.showExitRoomShareDialog(it.data, exit = {
        /** 点击退出直播间埋点*/
        GrowingUtil.track("push_popup_click", "popup_type", "连线关闭直播间分享弹窗", "popup_click_type", "退出直播间", "view_type", getViewType(it), "zx_push_popup_ab", "测试题${String.format("%02d", it.data.testNum)}",
                "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
        activity?.finish()
        clearTimer()
    }) { _, isWechatCircle ->
        /** 点击分享直播间埋点*/
        /** 点击退出直播间埋点*/
        val popupClickType = when {
            !it.data.isExitRoom && it.data.isFirstShare -> "再次分享"
            else -> "点击分享"
        }
        GrowingUtil.track("push_popup_click", "popup_type", "连线关闭直播间分享弹窗", "popup_click_type", popupClickType, "view_type", getViewType(it), "zx_push_popup_ab", "测试题${String.format("%02d", it.data.testNum)}",
                "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
        //分享
        activity?.let { activity ->
            ShareManager.instance.handleShareChoice(
                    activity,
                    if (isWechatCircle) wechat_circle else wechat,
                    ShareData("", "", it.data.questionImage.orEmpty(), "", "", R.mipmap.ic_launcher, false, TdType.image_share), backResult = { result ->
                //分享成功后再次请求并弹框显示分享
                if (result) {
                    apiSpiceMgr.executeKt(AgainRoomExitRequest(), success = {
                        if (it.data != null && it.data.backendGround.isNotEmpty()) {
                            it.data.isExitRoom = false
                            GrowingUtil.track("push_popup_view", "popup_type", "连线关闭直播间分享弹窗", "view_type", if (it.data?.isFirstShare == true) " 首次分享成功" else "非首次分享成功", "zx_push_popup_ab", "测试题${String.format("%02d", it.data.testNum)}",
                                    "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
                            it.data.isWechatCircle = isWechatCircle
                            exitRoomShare(it)
                        }
                    })
                }


            })
        }

    }
}


/**
 * 上下麦定时器通知服务器
 */
fun LiveVideoSplitFragment.uploadMicStatusTimers() {
    reportRunnable = null
    if (isAdded) {
        val status = if (isPushStreaming) 111 else 112
        val run = Runnable {
            uploadMicStatusTimers()
            reportRunnable = null
        }
        reportRunnable = run
        apiSpiceMgr.executeKt(AgoraStatusRequest(roomInfo.roomId, status, roomInfo.roomType), success = {
            if (it.isSuccess) {
                if (isAdded) {
                    it.data?.roomInfoChange?.let { roomInfoChange ->
                        if (roomInfoChange.applyUpstreamEnable != roomInfo.applyUpstreamEnable && !roomInfo.isCreator) {
                            roomInfo.applyUpstreamEnable = roomInfoChange.applyUpstreamEnable
                            postAction(APPLY_UPSTREAM_ENABLE, roomInfoChange.applyUpstreamEnable)
                        }
                        roomInfo.upstreamCouponItem = roomInfoChange.upstreamCouponItem
//                    if (roomInfo.roomType == RoomType.EXCLUSIVE && RoomType.parseValue(roomInfoChange.roomType) == RoomType.COMMON) {
//                        switchCommonExclusiveUi(false, roomInfoChange.serviceInfo)
//                    }
//                    showUpstreamTime(roomInfoChange)
                    }
                }
            } else if (it.rtn == 555) {
                if (isAdded) {
                    UIToast.toast("您的网络异常链接失败，请重新申请连线")
                    stopStream()
                }
            }
            serviceReportTime = it.data?.after_call ?: serviceReportTime
            if (serviceReportTime <= 0) serviceReportTime = 2
            postHandler.postDelayed(run, serviceReportTime * 1000L)
        }, failure = {
            postHandler.postDelayed(run, serviceReportTime * 1000L)
        })
    }
}

fun LiveVideoSplitFragment.showUpstreamTime(isSpecial: Boolean) {
    /**计时器*/
    if (activity.isUpstream() && upstreamUserIds.size == 2 && !(exclusive_count_down.isVisible || discount_countdown.isVisible)) {
        apiSpiceMgr.executeKt(RoomStatusRequest(roomInfo.roomId), success = {
            it.data?.let { status ->
                stateInfo = status
                if (status.begin > 0) {
                    exclusiveTimeAccount = status.duration.toLong()
                    remainTimeAccount = if (roomInfo.upstreamCouponItem != null) status.couponRemainDuration.toLong() else status.remainDuration.toLong()
                    remain_time.text = buildSpannedString {
                        if (isSpecial) {
                            appendLink(stateInfo?.balanceTips?.text.orEmpty(), stateInfo?.balanceTips?.color.parseColor(Color.WHITE))
                        } else {
                            appendLink(stateInfo?.timePrefix?.text.orEmpty() + remainTimeAccount.formatTimeVideo(), status.timePrefix?.color.parseColor(Color.WHITE))
                        }
                    }
                    discount_countdown_text.apply {
                        paint.isFakeBoldText = true
                        text = buildSpannedString {
                            appendLink(stateInfo?.balanceTips?.text.orEmpty(), stateInfo?.balanceTips?.color.parseColor(Color.WHITE))
                        }
                    }
                    discount_progress.progress = remainTimeAccount * TimeUnit.SECOND
                    showUpstreamStyle()
                }
            }
        })
    }
}

fun LiveVideoSplitFragment.callServiceIsStop() {
    isPushStreaming = false
    /** 通知服务器保证成功，重试三次 **/
    spiceHolder().apiSpiceMgr.executeKt(AgoraStatusRequest(roomInfo.roomId, 112, roomInfo.roomType), failure = {
        spiceHolder().apiSpiceMgr.executeKt(AgoraStatusRequest(roomInfo.roomId, 112, roomInfo.roomType), failure = {
            spiceHolder().apiSpiceMgr.executeKt(AgoraStatusRequest(roomInfo.roomId, 112, roomInfo.roomType))
        })
    })
}

fun LiveVideoSplitFragment.refreshChildView() {
    if (isAdded) {
        is_start_live_text.text = roomInfo.roomStatusTips.orEmpty()

        setCurrentScene((roomInfo.guestUser == null).selectBy(1, roomInfo.mode))
        startTiming()

        val allowUsers = hashSetOf(roomInfo.createUser?.userId ?: 0, roomInfo.guestUser?.userId
                ?: 0).filter { it > 0 }
        switchCommonExclusiveUi(roomInfo.roomType == RoomType.EXCLUSIVE, roomInfo.specialServiceSampleDto,
                allowUsers.toHashSet())
    }
}

fun LiveVideoSplitFragment.initEvent() {
    uploadMicStatusTimers()
    checkNewEvent.bindEventLifecycle(this)
    now_top_up.setOnClickListener {
        activity?.showPayDialog(PayFromType.BROADCAST_UP)
    }
    discount_top_up.setOnClickListener {
        activity?.showPayDialog(PayFromType.BROADCAST_FREE_COUPON)
    }
    exclusive_count_down.setOnClickListener {
        if (upstreamProtectHintPop?.isShowing != true) {
            upstreamProtectHintPop = exclusive_count_down.showUpstreamProtectHintPop(if (roomInfo.isSpecialService) 3 else 1)
        }
    }
}

/**
 * 所有的直播视频
 */
fun LiveVideoSplitFragment.allVideoView(): MutableList<LiveVideoItemView> =
        arrayListOf(anchor_video_layout, user_video_layout)

/**
 * 获取当前用户级别可进行连线等操作的[LiveVideoItemView]
 */
fun LiveVideoSplitFragment.liveVideoView(): LiveVideoItemView {
    return when {
        /** 当前直播红娘 **/
        roomInfo.isCreator -> anchor_video_layout
        else -> user_video_layout
    }
}

/**
 * 连线
 */
@SuppressLint("CheckResult")
fun LiveVideoSplitFragment.startStream(enableVideo: Boolean = true, back: () -> Unit = {}) {
    /** 如果需要连线则需要请求权限 **/
    activity?.requestMulPermissionZip(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA) {
        // 两个权限都需要才能连线
        if (it) {
            if (agoraManager.isStreaming) {
                runAsyncCallBack({ agoraManager.stopBroadcast() }, { startStream(enableVideo, back) })
            } else {
                liveVideoView().apply {
                    player.stop()
                    checkUserOffline(userId)
                    videoContainer.removeAllViews()
                    live_video_info_container.removeAllViews()
                    agoraManager.startBroadcast(videoContainer, enableVideo)
                    agoraConfig.rtcEngine()?.muteLocalAudioStream(false)
                    agoraConfig.rtcEngine()?.enableAudio()
                    userId = JohnUser.getSharedUser().userID
                    if (!enableVideo) {
                        disableVideo()
                    }else{
                        videoState = 2
                    }
                    upstreamUserIds.add(userId)
                    timeAccount = 1
                    refreshUserInfo()
                }
                is_start_live_text.isVisible = false
                postAction(UPSTREAM_ACTION, true)
            }
        } else {
            agoraConfig.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
            NormalDialogLauncher.newInstance().showDialog(activity, "用户连线需要摄像头和录音权限，请打开相应权限", back = {
                if (it == DISSMISS_DO_OK) {
                    PermissionCheck.gotoPermissionSetting(activity)
                }
                if (roomInfo.isCreator) activity?.finish()
            })
        }
        back()
    }
}

/**
 * 下麦
 */
fun LiveVideoSplitFragment.stopStream() {
    (activity as? VideoLiveRoomActivity)?.pendingUpStream = false
    isPushStreaming = false
    agoraConfig.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
    if (agoraManager.isStreaming) {
        runAsync {
            agoraManager.stopBroadcast()
        }
    }
    liveVideoView().checkUserOffline(JohnUser.getSharedUser().userID)
    upstreamUserIds.remove(JohnUser.getSharedUser().userID)
    exclusive_count_down.isVisible = false
    discount_countdown.isVisible = false
    setCurrentScene(1)
    switchCommonExclusiveUi(roomInfo.isSpecialStudio)
    upstreamResidueDialog?.dismissAllowingStateLoss()
    showResidueDialogArray.clear()
    postAction(UPSTREAM_ACTION, false)
}

/**
 * 主播退出房间
 */
fun LiveVideoSplitFragment.exitLiveRoomEvaluationUserLive() {
    isNotOnDoubleClick(3000, "close_live_room_evaluation") {
        val selfLiveVideoView = liveVideoView()
        when {
            roomInfo.isCreator -> {
                //红娘直接下麦
                activity?.finish()
            }
            selfLiveVideoView.userId == JohnUser.getSharedUser().userID -> {
                val matchId = roomInfo.createUser?.userId ?: 0
                allVideoView().forEach { it.videoContainer.removeAllViews() }
                activity?.showEvaluationDialog(matchId, roomInfo.roomId)
                stopStream()
            }
            else -> {
                if (liveEndDialog?.isHidden != false) {
                    liveEndDialog?.dismissAllowingStateLoss()
                    liveEndDialog = activity?.showSingleBtnCommonDialog("当前直播已结束", canceledOnTouchOutside = false) {
                        it.dismissAllowingStateLoss()
                        liveEndDialog = null
                    }
                    exitRunnable = Runnable {
                        exitRunnable = null
                        if (isAdded && liveEndDialog?.isHidden == false) {
                            liveEndDialog?.dismissAllowingStateLoss()
                        }
                    }
                    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    postHandler.postDelayed(exitRunnable, 30_000)
                }
            }
        }
        upstreamUserIds.clear()
        is_start_live_text.isVisible = true
    }
}

const val EXCLUSIVE_SHOW_UPSTREAM_HINT = "EXCLUSIVE_SHOW_UPSTREAM_HINT"
const val SPECIAL_SHOW_UPSTREAM_HINT = "SPECIAL_SHOW_UPSTREAM_HINT"

/**
 * 处理系统通知事件
 */
@SuppressLint("SetTextI18n")
fun LiveVideoSplitFragment.handleIMEvent(msg: CustomMessage) {
    val type = msg.customMsgType
    logDebug("handleIMEvent: ${type.name}")
    (msg.customInfo?.data as? ChickCustomData)?.also { data ->
        when (type) {
            CustomType.auto_kick_offline -> {
                /** 被踢下麦 **/
                if (data.userIds.any { it == JohnUser.getSharedUser().userID }) {
                    if (data.targetUserId == JohnUser.getSharedUser().userID) {
                        when {
                            roomInfo.isCreator -> {
                                activity?.finish()
                            }
                            data.reason == 4 -> {
                                val name = if (roomInfo.groupInfo?.groupName.isNullOrEmpty()) "知心达人【${roomInfo.createUser?.nickName.orEmpty()}】" else "【${roomInfo.groupInfo?.groupName.orEmpty()}】"
                                postAction(HAS_BLACKLIST, name)
                                activity?.finish()
                            }
                            else -> {
                                /** 下麦评价相亲对象 **/
                                activity?.showEvaluationDialog(roomInfo.createUser?.userId
                                        ?: 0, roomInfo.roomId)
                                stopStream()
                            }
                        }
                    }
                    val content = (roomInfo.isCreator).selectBy(data.publisherTips, data.targetTips)
                    UIToast.toast(content)
                }
            }
            CustomType.room_mode_change -> {
                /**房间窗口模式变更*/
                if (anchor_video_layout.state != data.mode) {
                    logDebug("setCurrentScene", "room_mode_change:mode:${data.mode}----upstreamUserIds.size:${upstreamUserIds.size}")
                    setCurrentScene((user_video_layout.userId > 0).selectBy(data.mode, 1))
                }
            }
            CustomType.room_forbidden -> {
                /** 关闭/打开连线用户的声音 **/
                if (data.targetUserId == JohnUser.getSharedUser().userID) {
                    when (data.status) {
                        "video" -> {
                            disableVideo(data.userId, data.mode)
                            liveSendCustomMsg(roomInfo.roomId, CustomType.toast, applyData = {
                                userIds = arrayListOf(anchor_video_layout.userId)
                                content = arrayListOf(SplitText(when (data.userId) {
                                    anchor_video_layout.userId -> "已将用户切换为语音连线"
                                    user_video_layout.userId -> "由于用户视频卡顿，用户已主动切换为语音连线"
                                    else -> "超管已将用户切换为语音连线"
                                }))
                            })
                        }
                        else -> disableAudio(data.userId, data.mode)
                    }
                }
            }
            CustomType.room_transform -> {
                /** 转换专属房间申请,用户进行同意处理 **/
                if (data.roomId == roomInfo.roomId && data.userIds.contains(JohnUser.getSharedUser().userID)) {
                    GrowingUtil.track(
                            "push_popup_view",
                            "popup_type", "转私密房弹窗",
                            "room_type", roomInfo.getRoomTypeForGrowingTrack(),
                            "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString()
                    )
                    activity?.showLiveAvatarDialog(data) { dialog, back ->
                        GrowingUtil.track(
                                "push_popup_click",
                                "popup_type", "转私密房弹窗",
                                "popup_click_type", if (back == 1) data.rightText else data.leftText,
                                "room_type", roomInfo.getRoomTypeForGrowingTrack(),
                                "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString()
                        )
                        dialog.dismissAllowingStateLoss()
                        val request = ApplyHandleRequest().apply {
                            userId = JohnUser.getSharedUser().userID
                            this.roomId = roomInfo.roomId
                            this.type = RoomApplyType.TRANSFORM
                            this.result = back
                        }
                        apiSpiceMgr.executeToastKt(request, success = {
                            if (it.rtn == 700) {
                                activity?.showPayDialog(PayFromType.BROADCAST)
                            }
                        })

                    }
                }
            }
            CustomType.room_type_change -> {
                /**房间类型变更*/
                if (data.roomId == roomInfo.roomId && data.reason == 1) {//reason== 1 表示房间是从普通转为私密
                    val isExclusive = RoomType.parseValue(data.roomType) == RoomType.EXCLUSIVE
                    logDebug("room_type_change", "roomType:${data.roomType}----data.serviceInfo:${data.serviceInfo?.specialServiceOrderId}")
                    /**过滤掉重复的信息*/
                    if (roomInfo.roomType != RoomType.parseValue(data.roomType) || roomInfo.specialServiceSampleDto?.specialServiceOrderId != data.serviceInfo?.specialServiceOrderId) {
                        roomInfo.videoMode = data.videoMode
                        switchCommonExclusiveUi(isExclusive, data.serviceInfo)
                    }
                }
            }
            CustomType.room_reject_up -> {
                /** 用户放弃连线，只需要给红娘弹提示，嘉宾和围观人不弹提示 **/
                if (data.roomId == roomInfo.roomId && roomInfo.isCreator) {
                    UIToast.toast(activity, data.summary)
                }
            }
            CustomType.people_changed -> {
                if (applyUpstreamDialog?.second?.dialog?.isShowing == true) {
                    /** 当前申请框中的用户跑到其它直播间去申请连线了 **/
                    apiSpiceMgr.executeKt(QueueShowRequest(roomInfo.roomId, 1, 1, 20), success = { user ->
                        val userList = user.data?.userList?.list?.mapNotNull { u -> u.userId }.orEmpty()
                        if (!userList.contains(applyUpstreamDialog?.first)) {
                            UIToast.toast("用户已在其他直播间连线成功")
                            applyUpstreamDialog?.second?.dismissAllowingStateLoss()
                        }
                    })
                }
            }
            CustomType.invite_friend_result -> {
                val list = allVideoView()
                apiSpiceMgr.requestUserInfo(list.map { it.userId }.toTypedArray(), needRefresh = true) {
                    list.forEach { it.refreshUserInfo() }
                }
            }
            CustomType.room_end -> {
                if (roomInfo.roomId == data.roomId && !roomInfo.isCreator) {
                    //连线用户拉起评价弹窗
                    exitLiveRoomEvaluationUserLive()
                }
            }
            CustomType.apply_warning -> {
                if (roomInfo.roomId == data.roomId && data.targetUserId == JohnUser.getSharedUser().userID && roomInfo.isCreator) {
                    applyUpstreamHintDialog = activity?.showApplyUpstreamHintDialog(applyUpstreamHintDialog, data, autoDismissTime = 30) { dialog, back ->
                        dialog.dismissAllowingStateLoss()
                        if (back == 1) {
                            (activity as? VideoLiveRoomActivity)?.upstreamListDialog?.dismissAllowingStateLoss()
                            applyUpstreamDialog?.second?.dismissAllowingStateLoss()
                            activity?.showLiveListDialog(roomInfo.roomId, back = {
                                (activity as? VideoLiveRoomActivity)?.upstreamListDialog = it
                            }, dismiss = { (activity as? VideoLiveRoomActivity)?.upstreamListDialog = null })
                        }
                    }
                }
            }
            CustomType.apply_upstream -> {
                if (roomInfo.roomId == data.roomId && roomInfo.isCreator) {
                    (activity as? VideoLiveRoomActivity)?.upstreamListDialog?.dismissAllowingStateLoss()
                    applyUpstreamDialog?.second?.dismissAllowingStateLoss()
                    GrowingUtil.track(
                            "push_popup_view",
                            "popup_type", "主播端收到用户申请上麦",
                            "room_type", roomInfo.getRoomTypeForGrowingTrack(),
                            "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString()
                    )

                    fun showApplyUpstreamDialog(data: ChickCustomData, back: (NormalDialog, Int) -> Unit = { _, _ -> }): NormalDialog? {
                        return if (data.serviceInfo != null) {
                            activity?.showSpecialServiceDialog(data, back)
                        } else {
                            activity?.showLiveAvatarDialog(data, back)
                        }
                    }
                    apiSpiceMgr.requestUserInfo(arrayOf(data.userId)) {
                        applyUpstreamDialog = Pair(data.userId, showApplyUpstreamDialog(data) { dialog, back ->
                            applyUpstreamDialog = null
                            dialog.dismissAllowingStateLoss()
                            GrowingUtil.track(
                                    "push_popup_click",
                                    "popup_type", if (data.serviceInfo != null) "主播端收到用户申请专属服务" else "主播端收到用户申请上麦",
                                    "popup_click_type", if (back == 0) data.leftText else data.rightText,
                                    "room_type", roomInfo.getRoomTypeForGrowingTrack(),
                                    "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString()
                            )
                            if (back == 1) {
                                val request = ApplyHandleRequest().apply {
                                    this.userId = data.userId
                                    this.roomId = roomInfo.roomId
                                    this.type = RoomApplyType.UPSTREAM
                                    this.result = 1
                                }
                                apiSpiceMgr.executeToastKt(request)
                            }
                        })
                    }
                }
            }
            CustomType.notify_mic_state -> {
                if (roomInfo.roomId == data.roomId && data.userId == user_video_layout.userId && data.userId != JohnUser.getSharedUser().userID) {
                    user_video_layout.onUserMuteAudio(data.userId, data.mode == 1)
                }
            }
            CustomType.apply_exclusive -> {
                if (upstreamUserIds.none { it == JohnUser.getSharedUser().userID }) {
                    if (MeiUser.getSharedUser().info?.isPublisher == true) {
                        activity?.showLiveAvatarDialog(data) { dialog, back ->
                            dialog.dismissAllowingStateLoss()
                            apiSpiceMgr.executeToastKt(ExclusiveHandleRequest().apply {
                                result = back
                                userId = data.userId
                            }, success = {
                                if (it.isSuccess && back == 1) {
                                    it.data?.roomInfo?.let { info ->
                                        VideoLiveRoomActivityLauncher.startActivity(activity, info.roomId, LiveEnterType.myself_apply_vip, info)
                                    }
                                }

                            })
                        }
                    } else {
                        activity?.showLiveAvatarDialog(data) { dialog, back ->
                            dialog.dismissAllowingStateLoss()
                            activity?.upstreamOutRoom(data, false, back, needTopUp = true)
                        }
                    }
                }
            }
            CustomType.send_gift -> {
                /**
                 * 充值成功的消息 主播端隐藏用户余额不足弹窗
                 */
                if (roomInfo.roomId == data.roomId && data.gift?.giftId == RECHARGE_SUCCESS_ID && data.whitelist.contains(JohnUser.getSharedUser().userID)) {
                    upstreamResidueDialog?.dismissAllowingStateLoss()
                    showResidueDialogArray.clear()
                }
            }
            CustomType.notify_video_convert_audio -> {
                /**提示主播用户由于网络原因转为语音连线*/
                if (roomInfo.roomId == data.roomId) {
                    if (roomInfo.isCreator) {
                        activity?.showSingleBtnCommonDialog(Cxt.getStr(R.string.convert_audio_upstream_hint_for_anchor)) {
                            it.dismissAllowingStateLoss()
                        }
                    }
                    roomInfo.videoMode = 1
                }
            }
            CustomType.toast -> {
                if (!msg.sender.getInt().isCmdId() && (data.userIds.isNullOrEmpty() || data.userIds.any { it == JohnUser.getSharedUser().userID })) {
                    UIToast.toast(msg.getSummary())
                }
            }
            CustomType.mute_remote_stream -> {
                /**音频流或者视频流关闭失败的防错处理*/
                if (roomInfo.roomId == data.roomId && data.ignoreIds.none { it == JohnUser.getSharedUser().userID }) {
                    if (data.mode == 0 || data.mode == 1) {
                        activity?.muteAllRemoteAudioStreams(true)
                    }
                    if (data.mode == 0 || data.mode == 2) {
                        upstreamUserIds.forEach {
                            if (it == roomInfo.createUser?.userId) {
                                anchor_video_layout.checkUserOffline(it)
                            } else {
                                user_video_layout.checkUserOffline(it)
                            }
                        }
                    }
                }
            }
            CustomType.deduct_change -> {
                if (data.userIds.contains(JohnUser.getSharedUser().userID)) {
                    val tips = if (roomInfo.isCreator) data.publisherTips else data.targetTips
                    NormalDialog().showDialog(activity, DialogData(cancelStr = null, message = tips,
                            okTimeOut = 5, canceledOnTouchOutside = false, backCanceled = false), okBack = null)
                    roomInfo.upstreamCouponItem = null
                    scheduleNotifyRoomStatus(0)
                }
            }
            CustomType.user_upstream -> {
                if (data.userId == JohnUser.getSharedUser().userID) {
                    startStream(roomInfo.isCreator || roomInfo.videoMode != 1)
                }
            }
            else -> {
            }
        }
    }
}

fun LiveVideoSplitFragment.upstream(type: CustomType, data: ChickCustomData, back: Int, videoMode: Int = 0) {
    apiSpiceMgr.executeToastKt(ApplyHandleRequest().apply {
        userId = JohnUser.getSharedUser().userID
        this.roomId = roomInfo.roomId
        this.type = data.parseApplyType()
        this.result = back
        this.videoMode = videoMode
    }, success = {
        if (it.isSuccess && back == 1) {
            val option = it.data?.option
            if (option == null) {
                startStream(videoMode != 1)
            } else {
                val payFromType = if (data.type == CustomType.invite_up) PayFromType.INVITED_UP_APPLY else PayFromType.UP_APPLY
                activity?.showApplyPrivateUpstreamDialog(option, payFromType) { back, close ->
                    var callback = back
                    if (back == 0) {
                        callback = 3
                    } else if (back == 2) {
                        callback = 4
                    }
                    upstream(type, data, callback, if (close) 1 else 0)
                }
            }
        } else if (it.rtn == 406 || it.rtn == 700) {
            /**余额不足**/
            activity?.showPayDialog(PayFromType.BROADCAST)
        }
    })
}

/**
 * @param userId 发送者id
 * @param mode 1-》关麦 0-》开麦
 */
fun LiveVideoSplitFragment.disableAudio(userId: Int, mode: Int) {
    agoraConfig.rtcEngine()?.apply {
        var state = muteLocalAudioStream(mode == 1)
        if (state != 0) {
            /** 开关声音失败重试一次 **/
            state = muteLocalAudioStream(mode == 1)
        }
        if (state == 0) {
            liveSendCustomMsg(roomInfo.roomId, CustomType.notify_mic_state, applyData = {
                this.mode = mode
                roomId = roomInfo.roomId
                this.userId = userId
            })
            liveVideoView().onUserMuteAudio(JohnUser.getSharedUser().userID, mode == 1)
            if (mode == 1 && userId != JohnUser.getSharedUser().userID) {
                UIToast.toast(activity, "你已被闭麦")
            }

            apiSpiceMgr.executeKt(UserForbiddenRequest().apply {
                roomId = roomInfo.roomId
                forbidden = mode
                type = "mic"
                fromUserId = userId
            })
        }
    }
}

fun LiveVideoSplitFragment.disableVideo(userId: Int, mode: Int, reason: String = "主动关闭") {
    agoraConfig.rtcEngine()?.apply {
        var state = enableLocalVideo(mode != 1)
        if (state != 0) {
            state = enableLocalVideo(mode != 1)
        }
        if (state == 0 && mode == 1) {
            upstreamUserIds.forEach {
                roomForbidden(it, "mic", 0)
            }
            apiSpiceMgr.executeKt(UserForbiddenRequest().apply {
                roomId = roomInfo.roomId
                forbidden = mode
                type = "video"
                fromUserId = userId
                this.reason = reason
            })

            setCurrentScene(1, roomInfo.isCreator)
            liveVideoView().disableVideo()
            roomInfo.videoMode = 1
            when (userId) {
                JohnUser.getSharedUser().userID -> {
                    UIToast.toast("你已切换为语音连线")
                    liveSendCustomMsg(roomInfo.roomId, CustomType.toast, applyData = {
                        userIds = arrayListOf(anchor_video_layout.userId)
                        content = arrayListOf(SplitText("由于用户视频卡顿，用户已主动切换为语音连线"))
                    })
                }
                anchor_video_layout.userId -> {
                    UIToast.toast("由于当前您的视频卡顿，知心达人已将您切换为语音连线")
                }
                else -> {
                    UIToast.toast("由于当前您的视频卡顿，已将您切换为语音连线")
                }
            }
        }
    }
}


/**
 * 开关麦
 * @param userId 接受者id
 * @param mode 1-》关麦 0-》开麦
 */
fun LiveVideoSplitFragment.roomForbidden(userId: Int, type: String, mode: Int) {
    roomFeatureCheck(roomInfo.roomId, type) {
        if (userId == JohnUser.getSharedUser().userID) {
            /**闭自己麦*/
            when (type) {
                "mic" -> disableAudio(userId, mode)
                "video" -> disableVideo(userId, mode)
            }
        } else {
            /** 通知用户开启或关闭音频推送 **/
            liveSendCustomMsg2(roomInfo.roomId, CustomType.room_forbidden, applyData = {
                this.userId = JohnUser.getSharedUser().userID
                this.targetUserId = userId
                roomId = roomInfo.roomId
                status = type
                this.mode = mode
            })
        }
    }
}

fun LiveVideoSplitFragment.roomFeatureCheck(roomId: String, type: String, back: () -> Unit) {
    if (type == "video") {
        apiSpiceMgr.executeToastKt(RoomFeatureCheckRequest().apply {
            this.roomId = roomId
            feature = "ONLY_AUDIO_CHAT"
        }, success = {
            if (it.isSuccess) {
                back()
            }
        })
    } else {
        back()
    }
}

/**
 * 是否显示剩余时长
 */
fun LiveVideoSplitFragment.isShowRemainTimeAccount() =
        upstreamUserIds.size == 2
                && (roomInfo.roomType == RoomType.EXCLUSIVE || roomInfo.isStudio)
                && stateInfo?.begin ?: 0 > 0
                && (roomInfo.roomRole == 2 || roomInfo.isCreator || activity.isUpstream())
                && stateInfo?.showRemainDuration == true


/**
 * 主播是否在线
 */
fun LiveVideoSplitFragment.isCreatorOnline() = upstreamUserIds.any { it == roomInfo.createUser?.userId }


/**
 * 拼接直播间拉起半屏web页的通用参数
 */
fun Context.appendRoomHalfScreenParamsUrl(url: String, roomInfo: RoomInfo) = MeiUtil.appendParamsUrl(url,
        "is_living" to "1", "screen_width" to px2dip(screenWidth).toString(),
        "is_screen" to px2dip(screenHeight).toString(),
        "group_id" to (roomInfo.groupInfo?.groupId ?: 0).toString(),
        "from" to "broadcast",
        "room_id" to roomInfo.roomId)

fun Context.appendRoomHalfScreenParamsUrl(url: String) = MeiUtil.appendParamsUrl(url,
        "is_living" to "1", "screen_width" to px2dip(screenWidth).toString(),
        "is_screen" to px2dip(screenHeight).toString())

fun FragmentActivity.getSpecialServiceDetailUrl(serviceId: Int, roomInfo: RoomInfo) =
        appendRoomHalfScreenParamsUrl(MeiUtil.appendParamsUrl(AmanLink.NetUrl.exclusive_service_details,
                "service_id" to serviceId.toString()), roomInfo)

fun FragmentActivity.getCourseDetailUrl(courseId: Int, roomInfo: RoomInfo) =
        appendRoomHalfScreenParamsUrl(MeiUtil.appendParamsUrl(AmanLink.NetUrl.course_introduce, "course_id" to courseId.toString()), roomInfo)

