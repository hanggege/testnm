package com.mei.live.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.core.text.buildSpannedString
import androidx.core.util.set
import androidx.core.view.*
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.ServiceInfo
import com.joker.im.custom.chick.SplitText
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.GrowingUtil
import com.mei.agora.*
import com.mei.agora.event.AgoraEventHandler
import com.mei.base.common.*
import com.mei.im.ext.isCmdId
import com.mei.live.LiveEnterType
import com.mei.live.ext.parseColor
import com.mei.live.manager.AgoraFaceunityCombineManager
import com.mei.live.manager.AgoraSurfaceManager
import com.mei.live.manager.liveSendCustomMsg
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.dialog.*
import com.mei.live.ui.getRoomTypeForGrowingTrack
import com.mei.live.views.autoShowHintPop
import com.mei.orc.ActivityLauncher
import com.mei.orc.Cxt
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenWidth
import com.mei.orc.ext.selectBy
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.unit.TimeUnit.SECOND
import com.mei.orc.util.date.formatTimeVideo
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.dialog.NormalDialog
import com.mei.wood.event.upstreamOutRoom
import com.mei.wood.extensions.appendLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.util.AppManager
import com.mei.wood.util.logDebug
import com.net.MeiUser
import com.net.model.room.AgoraStatus
import com.net.model.room.RoomInfo
import com.net.model.room.RoomStatus
import com.net.model.room.RoomType
import com.net.network.chat.ExclusiveHandleRequest
import com.net.network.room.*
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMMessage
import com.trello.rxlifecycle3.android.FragmentEvent
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_live_video_split_layout.*
import java.util.concurrent.TimeUnit

/**
 * Created by hang on 2020-02-20.
 */
@Suppress("UNCHECKED_CAST")
class LiveVideoSplitFragment : CustomSupportFragment(), AgoraEventHandler {

    var applyUpstreamDialog: Pair<Int, NormalDialog?>? = null
    var applyUpstreamHintDialog: NormalDialog? = null
    var upstreamProtectHintPop: PopupWindow? = null

    /**直播结束的弹窗*/
    var liveEndDialog: NormalDialog? = null
    val inviteDialogList = arrayListOf<NormalDialog>()

    var roomInfo: RoomInfo = RoomInfo()
        @SuppressLint("CheckResult")
        set(value) {
            field = value
            /** 缓存用户信息 **/
            apiSpiceMgr.requestUserInfo(arrayOf(roomInfo.createUser?.userId
                    ?: 0), needRefresh = true)

            agoraConfig.setClientRole()
            agoraConfig.setAudioProfile(Constants.AUDIO_PROFILE_DEFAULT, value.scenario)
            activity?.joinLiveRoom(value.channelId, value.token)

            if (isAdded) {
                /**普通房间信息*/
                anchor_video_layout.roomInfo = roomInfo
                user_video_layout.roomInfo = roomInfo
            }
            if (value.needUpstream) {
                startStream(roomInfo.videoMode != 1) {
                    refreshChildView()
                }
            } else {
                if (isAdded) {
                    refreshChildView()
                }
            }
        }

    var serviceReportTime = 2
    var exitRunnable: Runnable? = null
    var reportRunnable: Runnable? = null
    val postHandler: Handler by lazy { Handler() }
    val agoraManager: AgoraSurfaceManager by lazy { (activity as VideoLiveRoomActivity).agoraManager }
    val upstreamUserIds = HashSet<Int>()


    val checkNewEvent: IMAllEventManager by lazy {
        object : IMAllEventManager() {
            /** 监听最新消息 **/
            @SuppressLint("SetTextI18n")
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                msgs.orEmpty().mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter {
                            (it.customMsgType == CustomType.apply_upstream_result || it.customMsgType == CustomType.invite_up)
                                    && it.peer.getInt(0).isCmdId()
                        }
                        /** 接收其它红娘发送的专属房间的同意连线 **/
//                        .filterNot { it.peer == roomInfo.groupId || it.peer == roomInfo.createUser?.userId?.toString() }
                        .forEach {
                            val data = it.customInfo?.data as? ChickCustomData
                            if (data != null && AppManager.getInstance().currentActivity() is VideoLiveRoomActivity) {
                                GrowingUtil.track(
                                        "push_popup_view",
                                        "popup_type",
                                        if (it.customMsgType == CustomType.invite_up) "知心达人邀请付费连线" else "用户端申请上麦二次确认",
                                        "room_type", data.roomTypeGrowing,
                                        "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString()
                                )
                                apiSpiceMgr.requestUserInfo(arrayOf(data.userId), data.roomId != roomInfo.roomId) {
                                    if (data.serviceInfo != null) {
                                        activity?.showSpecialServiceDialog(data) { dialog, back ->
                                            handInviteDialog(data, dialog, back)
                                        }?.apply {
                                            inviteDialogList.add(this)
                                        }
                                    } else {
                                        activity?.showLiveAvatarDialog(data) { dialog, back ->
                                            handInviteDialog(data, dialog, back)
                                        }?.apply {
                                            inviteDialogList.add(this)
                                        }
                                    }
                                }
                            }
                        }

                msgs.orEmpty().mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { it.customMsgType == CustomType.apply_exclusive && it.chatType == TIMConversationType.C2C }
                        .mapNotNull { it.customInfo?.data as? ChickCustomData }
                        .forEach { data ->
                            if (upstreamUserIds.none { it == JohnUser.getSharedUser().userID }) {
                                apiSpiceMgr.requestUserInfo(arrayOf(data.userId)) {
                                    if (MeiUser.getSharedUser().info?.isPublisher == true) {
                                        activity?.showLiveAvatarDialog(data) { dialog, back ->
                                            dialog.dismissAllowingStateLoss()
                                            apiSpiceMgr.executeToastKt(ExclusiveHandleRequest().apply {
                                                result = back
                                                userId = data.userId
                                            }, success = {
                                                if (it.isSuccess && back == 1) {
                                                    it.data?.roomInfo?.let { info ->
                                                        if (info.roomId == roomInfo.roomId) {
                                                            if (info.needUpstream) {
                                                                startStream()
                                                            }
                                                        } else {
                                                            VideoLiveRoomActivityLauncher.startActivity(activity, info.roomId, LiveEnterType.myself_apply_vip, info)
                                                        }
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
                        }

                msgs.orEmpty().mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { it.customMsgType == CustomType.quick_apply_exclusive && it.chatType == TIMConversationType.C2C }
                        .mapNotNull { it.customInfo?.data as? ChickCustomData }
                        .forEach { data ->
                            if ((roomInfo.isCreator && upstreamUserIds.size < 2) || !roomInfo.isCreator) {
                                activity?.showQuickConsultDialog(data) { dialog ->
                                    apiSpiceMgr.executeToastKt(SnapUpApplyHandleRequest().apply {
                                        userId = data.userId
                                        roomId = if (roomInfo.isCreator) roomInfo.roomId else ""
                                    }, success = {
                                        if (it.isSuccess) {
                                            if (it.data?.roomInfo == null) {
                                                if (!context.isUpstream()) {
                                                    startStream()
                                                }
                                            } else {
                                                it.data?.roomInfo?.let { info ->
                                                    VideoLiveRoomActivityLauncher.startActivity(activity, info.roomId, LiveEnterType.myself_apply_vip, info)
                                                }
                                            }
                                        } else if (it.rtn == 400) dialog.dismissAllowingStateLoss()
                                    })
                                }
                            }
                        }

                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        /** 只接收群消息与当前知心达人的消息   添加指令消息**/
                        .filter { it.peer == roomInfo.roomId || it.peer == roomInfo.createUser?.userId?.toString() }
                customList.forEach { handleIMEvent(it) }
                return super.onNewMessages(msgs)
            }
        }
    }

    private fun handInviteDialog(data: ChickCustomData, dialog: NormalDialog, back: Int) {
        GrowingUtil.track(
                "push_popup_click",
                "popup_type",
                if (data.type == CustomType.invite_up) "知心达人邀请付费连线" else "用户端申请上麦二次确认",
                "popup_click_type",
                if (back == DISSMISS_DO_OK)
                    data.inviteUp?.rightText ?: "立即连线"
                else
                    data.inviteUp?.leftText ?: "再想想",
                "room_type", roomInfo.getRoomTypeForGrowingTrack(),
                "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString()
        )
        if (back == 1) {
            inviteDialogList.forEach { inviteDialog ->
                inviteDialog.dismissAllowingStateLoss()
            }
            inviteDialogList.clear()
        } else {
            inviteDialogList.remove(dialog)

            if (data.type == CustomType.apply_upstream_result) {
                postAction(APPLY_UPSTREAM_STATE, false)
            }
        }
        dialog.dismissAllowingStateLoss()
        /**在当前直播间*/
        if (data.roomId == roomInfo.roomId) {
            roomInfo.videoMode = data.videoMode
            upstream(data.type, data, back, data.videoMode)
        } else {
            /**不在当前直播间*/
            activity?.upstreamOutRoom(data, false, back, needTopUp = true)
        }
    }

    var exclusiveTimeAccount: Long = 0L
    var remainTimeAccount = 0L
    var stateInfo: RoomStatus? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ActivityLauncher.bind(this)
        return inflater.inflate(R.layout.fragment_live_video_split_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        icon_upstream.paint.isFakeBoldText = true
        if (roomInfo.roomId.isNotEmpty()) {
            /**普通房间信息*/
            anchor_video_layout.roomInfo = roomInfo
            user_video_layout.roomInfo = roomInfo

            refreshChildView()
        }
        bindAction(arrayOf(MUTE_REMOTE_AUDIO_STREAMS, WINDOW_MODE_CHANGE, AUDIO_MODE_CHANGE, VIDEO_MODE_CHANGE, HAS_BLACKLIST)) { action, obj ->
            if (isAdded) {
                when (action) {
                    MUTE_REMOTE_AUDIO_STREAMS -> {
                        if (obj == true) {
                            activity?.muteAllRemoteAudioStreams(true)
                        } else {
                            if (isShowVideoWindow()) {
                                activity?.muteAllRemoteAudioStreams(false)
                            }
                        }
                    }
                    WINDOW_MODE_CHANGE -> {
                        (obj as? Int)?.let { performZoomAnim(it) }
                    }
                    AUDIO_MODE_CHANGE -> {
                        (obj as? Pair<Int, Int>)?.let { roomForbidden(it.first, "mic", it.second) }
                    }
                    VIDEO_MODE_CHANGE -> {
                        (obj as? Pair<Int, Int>)?.let { roomForbidden(it.first, "video", it.second) }
                    }
                    HAS_BLACKLIST -> {
                        //拉黑，断掉视频流
                        anchor_video_layout.checkUserOffline(anchor_video_layout.userId)
                        user_video_layout.checkUserOffline(user_video_layout.userId)
                    }
                }
            }
        }
    }

    /**主播连线的时间*/
    private var anchorUpstreamTime = 0
    var upstreamResidueDialog: NormalDialog? = null

    /**显示剩余连线时长弹窗在对应时间段是否弹出*/
    val showResidueDialogArray = SparseBooleanArray(1)


    /**
     * 开始计时
     */
    fun startTiming() {
        Observable.interval((roomInfo.isCreator).selectBy(5L, 0L), 1, TimeUnit.SECONDS)
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { time ->
                    if (!isAdded || isDetached) {
                        return@subscribeBy
                    }
                    val containerFragment = (activity as? VideoLiveRoomActivity)?.liveTopContainerFragment
                    if (anchor_video_layout.videoContainer.isNotEmpty()) {
                        anchor_video_layout.timeAccount++
                    }
                    containerFragment?.refreshStartLiveTime(anchor_video_layout.timeAccount)
                    containerFragment?.onLineTime = (containerFragment?.onLineTime ?: 0) + 1

                    /**连线计时逻辑**/
                    if (upstreamUserIds.size == 2 || roomInfo.isSpecialStudio) {
                        scheduleNotifyRoomStatus(time)

                        exclusiveTimeAccount++
                        if (exclusiveTimeAccount > 24 * 3600L || exclusiveTimeAccount < 0) exclusiveTimeAccount = 1

                        if (roomInfo.isSpecialStudio) {
                            containerFragment?.refreshSpecialStudioState(stateInfo?.groupRoomIsOpened
                                    ?: false)
                        }
                        containerFragment?.refreshUpstreamTime(exclusiveTimeAccount)

                        if (user_video_layout.videoContainer.isNotEmpty()) {
                            user_video_layout.timeAccount = exclusiveTimeAccount
                        }

                        remainTimeAccount--
                        if (remainTimeAccount < 0) remainTimeAccount = 0

                        refreshExclusiveRemainLabel()
                    }

                }
    }

    /**
     * 专属房间和工作室 每10s定时刷新RoomStatusRequest
     */
    fun scheduleNotifyRoomStatus(time: Long) {
        if (time % 10 == 0L || exclusiveTimeAccount < 1) {
            apiSpiceMgr.executeKt(RoomStatusRequest(roomInfo.roomId), success = {
                it.data?.apply {
                    stateInfo = this
                    showUpstreamStyle()

                    /**
                     * 连麦时间不够，主动抱用户下麦
                     */
                    if (activity.isUpstream() && !roomInfo.isCreator && remainDuration == 0) {
                        stopStream()
                    }
                    exclusiveTimeAccount = duration.toLong()
                    remainTimeAccount = if (roomInfo.upstreamCouponItem != null) couponRemainDuration.toLong() else remainDuration.toLong()

                    promptUserToRecharge(this)
                }
            })
        }
    }

    /**
     * 展示右侧私密连线
     */
    fun showUpstreamStyle() {
        if (isShowRemainTimeAccount()) {
            if (roomInfo.upstreamCouponItem != null) {
                showCouponUpstreamStyle()
                if (roomInfo.upstreamCouponItem?.couponNum == -1L) {
                    showExclusiveCountDownStyle()
                } else {
                    exclusive_count_down.isVisible = false
                }
            } else {
                discount_countdown.isVisible = false
                showExclusiveCountDownStyle()
            }
        }
    }

    private fun showCouponUpstreamStyle() {
        if (!roomInfo.isCreator) {
            discount_top_up.isVisible = true
            discount_top_up_text.paint.isFakeBoldText = true
        }
        startRightEnterAnim(discount_countdown)
        discount_progress.duration = (stateInfo?.totalAvailableDuration ?: 0) * SECOND
        discount_progress.startIndicatorAnimation()
    }

    private fun showExclusiveCountDownStyle() {
        if (!exclusive_count_down.isVisible) {
            if (upstreamProtectHintPop?.isShowing != true) {
                upstreamProtectHintPop = exclusive_count_down.autoShowHintPop(if (roomInfo.isSpecialService) 3 else 1)
            }
            startRightEnterAnim(exclusive_count_down)
        }
        refreshUpstreamIcon()
    }

    /**
     * 连麦时长不足时 弹窗提示
     */
    private fun promptUserToRecharge(roomStatus: RoomStatus) {
        if (activity.isUpstream() && roomStatus.remainDuration - roomStatus.showRechargeDialogTime <= 10) {
            if (!showResidueDialogArray[roomStatus.showRechargeDialogTime] || (showResidueDialogArray[roomStatus.showRechargeDialogTime] && upstreamResidueDialog?.dialog?.isShowing == true)) {
                roomStatus.alert?.let {
                    upstreamResidueDialog = activity?.showUpstreamResidueDialog(upstreamResidueDialog, roomInfo.isCreator, roomStatus) { dialog, back, _ ->
                        if (back == 0 || back == 1) {
                            dialog.dismissAllowingStateLoss()
                            upstreamResidueDialog = null
                        }
                    }
                    showResidueDialogArray.clear()
                    showResidueDialogArray[roomStatus.showRechargeDialogTime] = true
                }
            }
        }
    }

    /**
     * 更新私密连线剩余时长的label ui
     */
    private fun refreshExclusiveRemainLabel() {
        if (roomInfo.roomType == RoomType.EXCLUSIVE || roomInfo.isStudio) {

            if (stateInfo?.balanceTips?.text.orEmpty().isNotEmpty()) {
                remain_time.isVisible = roomInfo.upstreamCouponItem?.couponNum != -1L
                remain_time.text = buildSpannedString {
                    appendLink(stateInfo?.balanceTips?.text.orEmpty(), stateInfo?.balanceTips?.color.parseColor(Color.BLACK))
                }
            } else {
                remain_time.isVisible = true
                remain_time.text = buildSpannedString {
                    if (roomInfo.isSpecialService) {
                        appendLink(stateInfo?.balanceTips?.text.orEmpty(), stateInfo?.balanceTips?.color.parseColor(Color.WHITE))
                    } else {
                        appendLink(stateInfo?.timePrefix?.text.orEmpty() + remainTimeAccount.formatTimeVideo(), stateInfo?.timePrefix?.color.parseColor(Color.WHITE))
                    }
                }
            }

            discount_countdown_text.apply {
                paint.isFakeBoldText = true
                text = buildSpannedString {
                    appendLink(stateInfo?.balanceTips?.text.orEmpty(), stateInfo?.balanceTips?.color.parseColor(Color.WHITE))
                }
            }
            discount_progress.progress = remainTimeAccount * com.mei.orc.unit.TimeUnit.SECOND

            now_top_up.isVisible = user_video_layout.userId == JohnUser.getSharedUser().userID && stateInfo?.showRecharge == true
        }
    }

    fun refreshUpstreamIcon() {
        if (roomInfo.isSpecialService) {
            icon_upstream.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_special_protect_audience_small, 0, 0)
            icon_upstream.text = if (roomInfo.isCreator) "专属服务" else "我的专属"
        } else {
            icon_upstream.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_exclusive_protect_audience_small, 0, 0)
            icon_upstream.text = "私密连线"
        }
    }

    private fun performZoomAnim(userId: Int) {
        when (anchor_video_layout.state) {
            0 -> {
                /**
                 * 1.点击的是当前video item，放大至全屏
                 * 2.点击的不是当前video item，缩小
                 */
                if (anchor_video_layout.userId == userId) {
                    setCurrentScene(1, true)
                } else {
                    setCurrentScene(2, true)
                }
            }
            else -> {
                setCurrentScene(0, true)
            }
        }
    }


    fun setCurrentScene(scene: Int, trigger: Boolean = false) {
        if (trigger) {
            apiSpiceMgr.executeKt(RoomWindowModeRequest(roomInfo.roomId, scene))
        }
        fun getTopMargin() = if (roomInfo.isStudio) {
            if (roomInfo.isCreator) dip(125) else dip(90)
        } else {
            if (roomInfo.isCreator) dip(115) else dip(80)
        }
        /**通知服务器 当前窗口模式*/
        TransitionManager.beginDelayedTransition(split_root_layout, Fade())
        when (scene) {
            0 -> {
                /**
                 * 并列窗口
                 */
                anchor_video_layout.state = 0
                user_video_layout.state = 0
                anchor_video_layout.updateLayoutParams<RelativeLayout.LayoutParams> {
                    width = screenWidth / 2 - dip(1)
                    height = dip(250)

                    marginStart = dip(0)
                    topMargin = getTopMargin()
                }
                user_video_layout.updateLayoutParams<RelativeLayout.LayoutParams> {
                    width = screenWidth / 2 - dip(1)
                    height = dip(250)

                    marginEnd = dip(0)
                    topMargin = getTopMargin()
                }

                right_layout.updateLayoutParams<RelativeLayout.LayoutParams> {
                    topMargin = getTopMargin() + dip(270)
                }
            }
            1 -> {
                /**
                 * 知心达人大窗口
                 */
                anchor_video_layout.updateLayoutParams<RelativeLayout.LayoutParams> {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.MATCH_PARENT

                    marginStart = dip(0)
                    topMargin = dip(0)
                }
                user_video_layout.updateLayoutParams<RelativeLayout.LayoutParams> {
                    width = dip(120)
                    height = dip(180)

                    marginEnd = dip(10)
                    topMargin = getTopMargin()
                }

                right_layout.updateLayoutParams<RelativeLayout.LayoutParams> {
                    topMargin = getTopMargin() + dip(190)
                }
                anchor_video_layout.state = 1
                user_video_layout.state = 2
                user_video_layout.bringToFront()
                right_layout.bringToFront()
            }
            2 -> {
                /**
                 * 知心达人小窗口
                 */
                user_video_layout.updateLayoutParams<RelativeLayout.LayoutParams> {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.MATCH_PARENT

                    marginEnd = dip(0)
                    topMargin = dip(0)
                }
                anchor_video_layout.updateLayoutParams<RelativeLayout.LayoutParams> {
                    width = dip(120)
                    height = dip(180)

                    marginStart = dip(10)
                    topMargin = getTopMargin()
                }
                right_layout.updateLayoutParams<RelativeLayout.LayoutParams> {
                    topMargin = getTopMargin() + dip(190)
                }
                anchor_video_layout.state = 2
                user_video_layout.state = 1
                anchor_video_layout.bringToFront()
                right_layout.bringToFront()
            }
        }
    }

    /***
     * 切换普通专属房ui
     * @param isExclusive 是否转专属
     * @param specialService 专属服务数据
     */
    fun switchCommonExclusiveUi(isExclusive: Boolean, specialService: ServiceInfo? = null, allowUsers: HashSet<Int> = hashSetOf()) {
        logDebug("room_type_change", "switchCommonExclusiveUi-----isExclusive:${isExclusive}")
        split_root_layout.setBackgroundResource(when {
            roomInfo.isSpecialStudio -> R.drawable.bg_studio_room
            isExclusive -> if (roomInfo.isSpecialService) {
                R.drawable.bg_exclusive_room
            } else {
                R.drawable.bg_private_room
            }
            else -> R.drawable.live_room_bg
        })
        if (roomInfo.roomType == RoomType.EXCLUSIVE) {
            upstreamProtectHintPop?.dismiss()
            /**由专属房回到普通房时*/
            if (!isExclusive) {
                back_to_common_room_label.setBackgroundResource(if (roomInfo.isSpecialService)
                    R.drawable.special_back_to_common_room_label else R.drawable.back_to_common_room_label)
                roomInfo.special = false

                startRightEnterAnim(back_to_common_room_label, 3000)
            }
        }

        roomInfo.roomType = isExclusive.selectBy(RoomType.EXCLUSIVE, RoomType.COMMON)
        roomInfo.specialServiceSampleDto = specialService
        exclusiveTimeAccount = 0
        stateInfo = null

        allowUsers.addAll(upstreamUserIds)

        if (isShowVideoWindow()) {
            activity?.muteAllRemoteAudioStreams(false)
            upstreamUserIds.forEach {
                if (it == roomInfo.createUser?.userId) {
                    anchor_video_layout.addSurfaceView(it)
                } else {
                    user_video_layout.addSurfaceView(it)
                }
            }
            /**专属房守护的label*/
            if ((!roomInfo.isSpecialStudio && roomInfo.roomType == RoomType.EXCLUSIVE) || (roomInfo.isSpecialStudio && roomInfo.specialServiceSampleDto != null)) {
                showUpstreamTime(specialService != null || roomInfo.special)
            }
        } else {
            activity?.muteAllRemoteAudioStreams(true)
            upstreamUserIds.forEach {
                if (it == roomInfo.createUser?.userId) {
                    anchor_video_layout.checkUserOffline(it)
                } else {
                    user_video_layout.checkUserOffline(it)
                }
            }
        }

        postAction(ROOM_TYPE_CHANGE, allowUsers)
    }

    /**
     * 专属房或者回到普通房右侧进入动画
     * @param view 当前view
     * @param delay 大于0时表明延时多少秒收回去
     */
    fun startRightEnterAnim(view: View, delay: Long = 0) {
        if (isAdded && view.isGone) {
            view.isVisible = true
            if (view.measuredWidth == 0) view.measure(0, 0)
            view.translationX = view.measuredWidth.toFloat()
            view.animate().translationX(0f).withLayer().setDuration(800).withEndAction {
                if (delay > 0 && isAdded) {
                    postHandler.postDelayed({
                        if (isAdded) {
                            view.animate().translationX(view.measuredWidth.toFloat()).withLayer().setDuration(800).withEndAction {
                                if (isAdded) {
                                    view.isVisible = false
                                }
                            }.start()
                        }
                    }, delay)
                }
            }.start()
        }
    }


    private var isJoinChannel = false

    /**
     * 加入频道3秒后等待onRemoteVideoStateChanged回调完判断主播是否在线刷新界面
     */
    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        super.onJoinChannelSuccess(channel, uid, elapsed)
        if (uid == JohnUser.getSharedUser().userID) {
            isJoinChannel = true
            split_root_layout.postDelayed({
                if (isAdded) {
                    is_start_live_text.isVisible = !isCreatorOnline()
                }
            }, 3000)
        }
    }

    override fun onRejoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        super.onRejoinChannelSuccess(channel, uid, elapsed)
        isJoinChannel = true
    }

    override fun onLeaveChannel(stats: IRtcEngineEventHandler.RtcStats?) {
        super.onLeaveChannel(stats)
        isJoinChannel = false
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        if (isAdded && roomInfo.channelId.isNotEmpty()) {
            is_start_live_text.isVisible = false
            upstreamUserIds.add(uid)
            if (uid == roomInfo.createUser?.userId) {
                anchorUpstreamTime = 0
                liveEndDialog?.dismissAllowingStateLoss()
            }
            addRemoteSurfaceView(uid, 0, RoomType.EXCLUSIVE)
        }
    }

    /**
     * 是否显示视频窗口
     */
    private fun isShowVideoWindow(roomType: RoomType = roomInfo.roomType): Boolean {
        return activity.isUpstream() || roomType == RoomType.COMMON || roomInfo.roomRole == 2 || roomInfo.groupRights and 8 != 0
    }

    override fun onUserEnableLocalVideo(uid: Int, enabled: Boolean) {
        if (uid == user_video_layout.userId && !enabled) {
            setCurrentScene(1, roomInfo.isCreator)
            user_video_layout.disableVideo()
        }
    }

    /**
     * 收到用户上麦回调 请求上报接口，判断当前房间类型和videoMode 然后转换房间
     */
    override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        if (isAdded) {
            if (state == Constants.REMOTE_AUDIO_STATE_STARTING) {
                apiSpiceMgr.requestUserInfo(arrayOf(uid)) {
                    requestAgoraStatus(uid, 1)
                }
            }
        }
    }

    override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        if (isAdded) {
            if (state == Constants.REMOTE_VIDEO_STATE_DECODING) {
                apiSpiceMgr.requestUserInfo(arrayOf(uid)) {
                    requestAgoraStatus(uid, 2)
                }
                if (roomInfo.isCreator) {
                    apiSpiceMgr.executeKt(AgoraReportRequest(roomInfo.roomId, 111, uid))
                }
            }
        }
    }

    /**
     * 请求上报接口 返回RoomInfoChange回调
     */
    private fun requestAgoraStatus(uid: Int, videoState: Int) {
        requestRetryAgoraStatus { roomInfoChange ->
            if (upstreamUserIds.contains(uid)) {
                if (roomInfoChange.createUser?.userId == uid && uid != roomInfo.createUser?.userId) {
                    onCreateChanged(roomInfoChange.createUser)
                }
                addRemoteSurfaceView(uid, videoState, RoomType.parseValue(roomInfoChange.roomType))
                setCurrentScene(if (upstreamUserIds.size < 2) 1 else roomInfoChange.mode)

//                exclusive_layout.refreshInfo(roomInfoChange.allowUsers.toSet())

                val isOnlyCreateUpstream = uid == roomInfo.createUser?.userId && upstreamUserIds.size == 1

                if (!roomInfo.isMatchRoomType(roomInfoChange.roomType, roomInfoChange.serviceInfo?.specialServiceId
                                ?: 0) && !isOnlyCreateUpstream) {
                    switchCommonExclusiveUi(roomInfoChange.roomType == RoomType.EXCLUSIVE.name, roomInfoChange.serviceInfo)
                }
            }
        }
    }

    private fun requestRetryAgoraStatus(isRetry: Boolean = false, success: (AgoraStatus.RoomInfoChange) -> Unit = {}) {
        val status = if (isPushStreaming && agoraManager.isStreaming) 111 else 112
        apiSpiceMgr.executeToastKt(AgoraStatusRequest(roomInfo.roomId, status, roomInfo.roomType), success = {
            val roomInfoChange = it.data?.roomInfoChange
            if (roomInfoChange != null) {
                success(roomInfoChange)
            } else {
                if (!isRetry) {
                    requestRetryAgoraStatus(true, success)
                }
            }
        }, failure = {
            if (!isRetry) {
                requestRetryAgoraStatus(true, success)
            }
        })
    }


    override fun onUserOffline(uid: Int, reason: Int) {
        anchor_video_layout.checkUserOffline(uid)
        user_video_layout.checkUserOffline(uid)

        upstreamUserIds.remove(uid)
        exclusive_count_down.isVisible = isShowRemainTimeAccount() && roomInfo.upstreamCouponItem == null
        discount_countdown.isVisible = isShowRemainTimeAccount() && roomInfo.upstreamCouponItem != null
        /**用户下麦后  主播切换成大窗口*/
        if (upstreamUserIds.size == 1) {
            setCurrentScene(1, roomInfo.isCreator)
            roomInfo.videoMode = 0
        }
        if (upstreamUserIds.size < 2) {
            switchCommonExclusiveUi(roomInfo.isSpecialStudio)
        }

        if (uid == roomInfo.createUser?.userId) {
            /** 知心达人离开了 **/
            exitLiveRoomEvaluationUserLive()
        }
        upstreamResidueDialog?.dismissAllowingStateLoss()
        showResidueDialogArray.clear()
    }

    /**
     * 主讲人变化
     */
    private fun onCreateChanged(user: RoomInfo.CreateUser?) {
        roomInfo.createUser = user
        anchorUpstreamTime = 0
        liveEndDialog?.dismissAllowingStateLoss()
        postAction(CREATOR_UPSTREAM)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun addRemoteSurfaceView(uid: Int, videoState: Int, roomType: RoomType) {
        if (isShowVideoWindow(roomType)) {
            if (uid == roomInfo.createUser?.userId) {
                anchor_video_layout.addSurfaceView(uid, videoState)
            } else {
                user_video_layout.addSurfaceView(uid, videoState)
            }
        }
    }

    private var localCheckVideoAccount = 0
    private var isBackground = false
    var isPushStreaming = false
    override fun onResume() {
        super.onResume()
        isBackground = false
    }

    override fun onPause() {
        super.onPause()
        isBackground = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callServiceIsStop()
        clearTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearTimer()
    }

    fun clearTimer() {
        reportRunnable?.let { run -> postHandler.removeCallbacks(run) }
        exitRunnable?.let { run -> postHandler.removeCallbacks(run) }
    }

    override fun onClientRoleChanged(oldRole: Int, newRole: Int) {
        super.onClientRoleChanged(oldRole, newRole)
        if (isAdded) {
            if (newRole == IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER) {
                isPushStreaming = true
                (agoraManager as? AgoraFaceunityCombineManager)?.videoParent?.forEach { child ->
                    child.isVisible = child is SurfaceView
                }

                /**上麦主动上报 并转换房间类型*/
                requestRetryAgoraStatus { roomInfoChange ->
                    if (activity.isUpstream()) {
                        if (!roomInfo.isMatchRoomType(roomInfoChange.roomType, roomInfoChange.serviceInfo?.specialServiceId
                                        ?: 0)) {
                            switchCommonExclusiveUi(RoomType.parseValue(roomInfoChange.roomType) == RoomType.EXCLUSIVE, roomInfoChange.serviceInfo, roomInfoChange.allowUsers.toHashSet())
                        } else {
//                            exclusive_layout.refreshInfo(roomInfoChange.allowUsers.toSet())
                        }
                        roomInfo.videoMode = roomInfoChange.videoMode
                        if (roomInfoChange.videoMode == 1) {
                            setCurrentScene(1, roomInfo.isCreator)
                        } else {
                            setCurrentScene(if (upstreamUserIds.size == 2) roomInfoChange.mode else 1)
                        }
                    }
                }
            }
        }
    }

    override fun onRtcStats(stats: IRtcEngineEventHandler.RtcStats?) {
        if (isAdded && stats != null && liveVideoView().userId == JohnUser.getSharedUser().userID) {
            if (!isBackground) {
                /**视频连线*/
                if (roomInfo.isCreator || roomInfo.videoMode != 1) {
                    if (stats.txVideoKBitRate == 0) {
                        localCheckVideoAccount++
                    } else {
                        localCheckVideoAccount = 0
                    }
                    when (localCheckVideoAccount) {
                        3, 6, 9 -> startStream(roomInfo.isCreator || roomInfo.videoMode != 1)//采集失败时，重试两次进行采集
                        13 -> {
                            localCheckVideoAccount = 0
                            /**
                             * 视频推流失败 如果有音频推流 则转为音频连线
                             */
                            if (stats.txAudioKBitRate > 0 && !roomInfo.isCreator) {
                                disableVideo(JohnUser.getSharedUser().userID, 1, "因为网络原因降级为语音连线")

                                activity?.showSingleBtnCommonDialog(Cxt.getStr(R.string.convert_audio_upstream_hint_for_user)) {
                                    it.dismissAllowingStateLoss()
                                }

                                liveSendCustomMsg(roomInfo.roomId, CustomType.notify_video_convert_audio, applyData = {
                                    roomId = roomInfo.roomId
                                })
                            } else {
                                handConnectFailed()
                            }
                        }
                        else -> {
                        }
                    }
                } else {
                    /**音频连线*/
                    if (stats.txAudioKBitRate == 0) {
                        localCheckVideoAccount++
                    } else {
                        localCheckVideoAccount = 0
                    }

                    when (localCheckVideoAccount) {
                        3, 6, 9 -> startStream(false)
                        13 -> {
                            localCheckVideoAccount = 0
                            handConnectFailed()
                        }
                    }
                }
            }
        }
    }

    /**
     * 连接失败的处理
     */
    private fun handConnectFailed() {
        if (roomInfo.isCreator) {
            activity?.showDoubleBtnCommonView(content = "您已掉线，请点击重连", leftBtn = "取消", rightBtn = "确认", isShowCountDown = true) { dialog, state ->
                dialog.dismissAllowingStateLoss()
                when (state) {
                    1 -> startStream(true)
                    else -> {
                        UIToast.toast("您已掉线")
                        activity?.finish()
                    }
                }
            }
        } else {
            if (isPushStreaming) {
                UIToast.toast("您已掉线,请重新连线")
                liveSendCustomMsg(roomInfo.roomId, CustomType.toast, applyData = {
                    userIds = arrayListOf(roomInfo.createUser?.userId ?: 0)
                    content = arrayListOf(SplitText("用户已掉线,请重新连线"))
                })
            } else {
                UIToast.toast("连线超时未成功，请重试")
            }
            apiSpiceMgr.executeKt(AgoraStatusRequest(roomInfo.roomId, if (isPushStreaming) 113 else 114, roomInfo.roomType))
            stopStream()
        }
    }

    override fun onConnectionLost() {
        super.onConnectionLost()
        UIToast.toast(activity, "网络状态异常，直播已掉线，请重新尝试")
        if (roomInfo.isCreator) {
            activity?.finish()
        }
    }

    override fun onConnectionStateChanged(state: Int, reason: Int) {
        if (state == Constants.CONNECTION_STATE_FAILED && reason == Constants.CONNECTION_CHANGED_BANNED_BY_SERVER) {
            UIToast.toast("网络状态异常,请重新进入试试")
            activity?.finish()
        }
    }

    override fun onError(err: Int) {
        super.onError(err)
        if (!isJoinChannel && (err == Constants.ERR_INVALID_ARGUMENT || err == Constants.ERR_NOT_READY || err == Constants.ERR_REFUSED)) {
            UIToast.toast(activity, "加入频道失败，请重新尝试")
            activity?.finish()
        }
    }

}