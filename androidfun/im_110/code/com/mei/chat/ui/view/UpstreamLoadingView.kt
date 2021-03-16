package com.mei.chat.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.gyf.immersionbar.ImmersionBar
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.joker.im.registered
import com.joker.im.unregistered
import com.mei.GrowingUtil
import com.mei.chat.ext.CallStatus
import com.mei.dialog.PayFromType
import com.mei.live.LiveEnterType
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.dialog.showApplyPrivateUpstreamDialog
import com.mei.live.ui.dialog.showDoubleBtnCommonView
import com.mei.live.ui.dialog.showSingleBtnCommonDialog
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.ext.screenHeight
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.permission.PermissionCheck
import com.mei.wood.R
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.logDebug
import com.net.MeiUser
import com.net.model.chat.ExclusiveApply.WaitInfo
import com.net.model.room.RoomApplyType
import com.net.network.chat.*
import com.net.network.room.ApplyHandleRequest
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.view_upstream_loading.view.*

/**
 * Created by hang on 2020/4/30.
 */
/**
 * 开始私密连线
 * @param callBack 0 取消 1 showLoading(对于主播 相当于开启直播间) 2 hideLoading
 * @param orderId 不为空表示发起专属服务连线
 */
fun MeiCustomActivity.startPrivateUpstream(chatId: Int, orderId: String = "", from: String = "im", couponNum: Long = 0, proCateId: Int = 0, callBack: (Int) -> Unit = {}) {
    Log.e("startPrivateUpstream", "${ImmersionBar.with(this).hashCode()}: ");
    requestMulPermissionZip(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA) {
        // 两个权限都需要才能连线
        if (it) {
            if (MeiUser.getSharedUser().info?.isPublisher == true) {
                val content = if (orderId.isNotEmpty()) "是否开启直播间\n并发起专属服务连线" else "是否开启直播间并发起连线"
                showDoubleBtnCommonView(content = content, rightBtn = "确认", isCanClose = true, back = { dialog, back ->
                    dialog.dismissAllowingStateLoss()
                    if (back == 1) {
                        requestExclusiveApply(chatId, from, orderId = orderId, callBack = callBack)
                    } else {
                        callBack(0)
                    }
                })
            } else {
                checkBalance(chatId, from, orderId, couponNum = couponNum, proCateId = proCateId) { waitInfo, videoMode ->
                    if (waitInfo != null) {
                        getUpstreamLoadingView(chatId, from, orderId).showAnim(waitInfo, callBack)
                        requestExclusiveApply(chatId, from, orderId = orderId, videoMode = videoMode, couponNum = couponNum, proCateId = proCateId)
                    } else {
                        callBack(0)
                    }
                }
            }
        } else {
            NormalDialogLauncher.newInstance().showDialog(activity, "用户连线需要摄像头和录音权限，请打开相应权限", back = {
                if (it == DISSMISS_DO_OK) {
                    PermissionCheck.gotoPermissionSetting(activity)
                }
            })
            callBack(0)
        }
    }
}

/**
 * 检测余额是否充足
 */
private fun MeiCustomActivity.checkBalance(chatId: Int, from: String, specialServiceId: String = "", couponNum: Long = 0, proCateId: Int = 0, back: (WaitInfo?, Int) -> Unit) {
    apiSpiceMgr.executeToastKt(ExclusiveApplyRequest().apply {
        targetUserId = chatId
        justCheckBalance = 1
        specialServiceOrderId = specialServiceId
        this.from = from
        this.couponNum = couponNum
        this.categoryId = proCateId
    }, success = {
        val data = it.data
        if (it.isSuccess && data != null) {
            if (data.option == null) {
                back(it.data?.waitInfo, 0)
            } else {
                showApplyPrivateUpstreamDialog(data.option, PayFromType.IM_UP_APPLY) { code, isCloseVideo ->
                    if (code == 1) {
                        back(it.data?.waitInfo, if (isCloseVideo) 1 else 0)
                    } else {
                        back(null, 0)
                    }
                }
            }
        } else {
            back(null, 0)
        }
    }, failure = { back(null, 0) })
}

/**
 * 快速咨询匹配
 *
 * @param couponNum 具体优惠券类型
 * @param categoryId 选择咨询方向
 * @param videoMode 是否取消匹配  0为申请,1为取消匹配
 * @param userType 用户类型 "新用户_有券","新用户-无券","老用户"
 * 快速咨询优化版：弃用之前盲目匹配一个知心达人就开始连线，解决给用户带来突兀的冲击
 * */
fun MeiCustomActivity.requestExclusiveMatchApply(publisherId: Int, couponNum: Long, categoryId: Int, videoMode: Int = 0, userType: String = "", callBack: (Int) -> Unit = {}) {
    showLoading(true)
    val applyView by lazy { getUpstreamLoadingView(publisherId, "", couponNum = couponNum, proCateId = categoryId).apply { this.userType = userType } }
    apiSpiceMgr.executeKt(ExclusiveMatchApplyRequest().apply {
        this.couponNum = couponNum
        this.categoryId = categoryId
        this.videoMode = videoMode
        this.publisherId = publisherId
    }, success = {
        UIToast.toast(it.data?.toast)
        when (it.rtn) {
            0 -> {
                val option = it.data?.option
                when {
                    option != null -> {
                        showApplyPrivateUpstreamDialog(option, PayFromType.QUICK_CONSULT_INSUFFICIENT_BALANCE) { code, isCloseVideo ->
                            if (code == 1) {
                                requestExclusiveMatchApply(publisherId, couponNum, categoryId, if (isCloseVideo) 1 else 0, userType)
                            }
                        }
                    }
                    it.data?.waitInfo != null -> {
                        applyView.showAnim(it.data.waitInfo, null)
                        GrowingUtil.track("function_view",
                                "function_name", "快速咨询",
                                "function_page", "匹配知心人页",
                                "status", userType,
                                "click_type", "")
                    }
                    else -> {
                        VideoLiveRoomActivityLauncher.startActivity(activity, it.data?.roomId.orEmpty(), LiveEnterType.im_private_page)
                        applyView.hideAnim(500)
                    }
                }
            }
            /**正在排麦中,发起连线将退出原来的麦序*/
            703 -> {
                val split = it.errMsg.split("|")
                showDoubleBtnCommonView(split.getOrNull(0).orEmpty(), split.getOrNull(1).orEmpty(), " ", rightBtn = "确定", isShowCountDown = false, canceledOnTouchOutside = false, isCanClose = true, back = { dialog, back ->
                    dialog.dismissAllowingStateLoss()
                    if (back == 1) {
                        requestExclusiveMatchApply(publisherId, couponNum, categoryId, userType = userType)
                    } else {
                        applyView.apply {
                            hideAnim()
                            cancelApply()
                        }
                    }
                })
            }
            else -> {
                UIToast.toast(it.errMsg)
            }
        }
    }, failure = {
        callBack(0)
        UIToast.toast(it?.currMessage.orEmpty())
    }, finish = { showLoading(false) })
}


/**
 * 连线申请请求
 *
 * 主播不显示等待页 才需要处理callBack
 */
private fun MeiCustomActivity.requestExclusiveApply(chatId: Int
                                                    , from: String
                                                    , ignore: Int = 0
                                                    , force: Int = 0
                                                    , orderId: String = ""
                                                    , videoMode: Int = 0
                                                    , couponNum: Long = 0
                                                    , proCateId: Int = 0
                                                    , callBack: ((Int) -> Unit)? = null) {
    showLoading(true)
    val applyView by lazy { getUpstreamLoadingView(chatId, from, orderId) }
    apiSpiceMgr.executeKt(ExclusiveApplyRequest().apply {
        targetUserId = chatId
        ignorePreviousApply = ignore
        forceApply = force
        specialServiceOrderId = orderId
        this.from = from
        this.videoMode = videoMode
        this.couponNum = couponNum
        this.categoryId = proCateId
    }, success = {
        UIToast.toast(it.data?.toast)
        when (it.rtn) {
            0 -> {
                it.data?.let { data ->
                    if (MeiUser.getSharedUser().info?.isPublisher == true) {
                        if (data.roomInfo != null) {
                            if (data.roomInfo.alertTips.isNullOrEmpty()) {
                                VideoLiveRoomActivityLauncher.startActivity(activity, data.roomInfo.roomId, LiveEnterType.im_private_page, data.roomInfo)
                            } else {
                                showSingleBtnCommonDialog("系统检测到您上场直播异常掉线，当前用户还在直播间内连线，点击确定恢复连线状态", btnText = "确认恢复") { dialog ->
                                    dialog.dismissAllowingStateLoss()
                                    VideoLiveRoomActivityLauncher.startActivity(activity, data.roomInfo.roomId, LiveEnterType.im_private_page, data.roomInfo)
                                }
                            }
                            if (callBack != null) {
                                callBack(1)
                            }
                        } else {
                            if (callBack != null) {
                                callBack(0)
                            }
                        }
                    } else {
                        if (data.waitInfo != null) {
                            applyView.showAnim(data.waitInfo, null)
                        } else {
                            VideoLiveRoomActivityLauncher.startActivity(activity, data.roomId, LiveEnterType.im_private_page)
                            applyView.hideAnim(500)
                        }
                    }
                }
            }
            /**知心达人已开启直播,是否立即前往并发起私密连线*/
            702 -> {
                applyView.postDelayed({
                    if (applyView.isShow) {
                        val split = it.errMsg.split("|")
                        showDoubleBtnCommonView(split.getOrNull(0).orEmpty(), split.getOrNull(1).orEmpty(), " ", rightBtn = "立即前往", isShowCountDown = false, canceledOnTouchOutside = false, isCanClose = true, back = { dialog, back ->
                            dialog.dismissAllowingStateLoss()
                            if (back == 1) {
                                requestExclusiveApply(chatId, from, force = 1, orderId = orderId, videoMode = videoMode, couponNum = couponNum, proCateId = proCateId)
                            } else {
                                applyView.apply {
                                    hideAnim()
                                    cancelApply()
                                }
                            }
                        })
                    }
                }, 2000)
            }
            /**正在排麦中,发起连线将退出原来的麦序*/
            703 -> {
                val split = it.errMsg.split("|")
                showDoubleBtnCommonView(split.getOrNull(0).orEmpty(), split.getOrNull(1).orEmpty(), " ", rightBtn = "确定", isShowCountDown = false, canceledOnTouchOutside = false, isCanClose = true, back = { dialog, back ->
                    dialog.dismissAllowingStateLoss()
                    if (back == 1) {
                        requestExclusiveApply(chatId, from, ignore = 1, orderId = orderId, videoMode = videoMode, couponNum = couponNum, proCateId = proCateId)
                    } else {
                        applyView.apply {
                            hideAnim()
                            cancelApply()
                        }
                    }
                })
            }
            /**知心达人正忙，暂时无法接听*/
            704 -> {
                applyView.postDelayed({
                    if (applyView.isShow) {
                        val split = it.errMsg.split("|")
                        showDoubleBtnCommonView(split.getOrNull(0).orEmpty(), split.getOrNull(1).orEmpty(), rightBtn = "知道了", isShowCountDown = false, canceledOnTouchOutside = false, isCanClose = true) { dialog, _ ->
                            dialog.dismissAllowingStateLoss()
                            applyView.hideAnim()
                        }
                    }
                }, 2000)
            }
            else -> {
                UIToast.toast(it.errMsg)
            }
        }
    }, failure = {
        UIToast.toast(it?.currMessage.orEmpty())
        if (callBack != null) {
            callBack(0)
        }
    }, finish = { showLoading(false) })
}


var upstreamLoadingView: UpstreamLoadingView? = null
private fun FragmentActivity.getUpstreamLoadingView(chatId: Int, from: String, specialServiceId: String = "", couponNum: Long = 0, proCateId: Int = 0): UpstreamLoadingView {
    if (upstreamLoadingView == null || upstreamLoadingView?.chatId != chatId || upstreamLoadingView?.activity != this) {
        upstreamLoadingView = UpstreamLoadingView(this).apply {
            this.chatId = chatId
            this.specialServiceId = specialServiceId
            this.from = from
            this.mCouponNum = couponNum
            this.proCateId = proCateId
        }
    }
    return upstreamLoadingView!!
}

class UpstreamLoadingView(context: Context) : FrameLayout(context) {

    private var info: WaitInfo? = null
    var chatId = 0 // -1代表快速咨询过来
    var from = ""
    var specialServiceId = ""
    var userType: String = ""
    var mCouponNum = 0L
    var proCateId = 0
    val activity = context as? MeiCustomActivity
    private var callBack: (Int) -> Unit = {}
    private val mediaPlayer by lazy { MediaPlayer.create(context, R.raw.upstream_call).apply { isLooping = true } }

    val checkNewEvent: IMAllEventManager by lazy {
        object : IMAllEventManager() {
            /** 监听最新消息 **/
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { !it.isDeleted() && it.chatType == TIMConversationType.C2C }//去掉已删除的消息
//                        .mapNotNull { it.customInfo?.data as? ChickCustomData }
                customList.forEach {
                    handleIMEvent(it)
                }
                return super.onNewMessages(msgs)
            }
        }
    }

    private fun handleIMEvent(msg: CustomMessage) {
        (msg.customInfo?.data as? ChickCustomData)?.let { data ->
            logDebug("handleIMEvent: ${data.type.name}")
            when (data.type) {
                CustomType.call_status_changed -> {
                    when (data.exclusiveResult?.status) {
                        CallStatus.TIMEOUT.name -> {
                            if (msg.isSelf && data.summary.isNotEmpty()) {
                                activity?.showDoubleBtnCommonView(content = data.summary, leftBtn = data.leftText, rightBtn = data.rightText, isShowCountDown = false, canceledOnTouchOutside = false) { dialog, back ->
                                    dialog.dismissAllowingStateLoss()
                                    if (back == 1) {
                                        activity.startPrivateUpstream(chatId, specialServiceId, from, mCouponNum, proCateId, callBack)
                                    } else {
                                        hideAnim()
                                    }
                                }
                            } else {
                            }
                        }
                        CallStatus.REFUSED.name -> {
                            if (msg.isSelf) {
                                if (MeiUser.getSharedUser().info?.isPublisher == true) {
                                    if (isShow) {
                                        UIToast.toast("对方已拒绝")
                                    }
                                }
                                hideAnim()
                            } else {
                                if (isShow) {
                                    UIToast.toast("该知心达人正忙")
                                } else {
                                }
                            }
                        }
                        CallStatus.CANCELLED.name -> {

                        }
                        else -> {

                        }
                    }
                }
                CustomType.invite_up, CustomType.apply_upstream_result -> {
                    if (isShow && (data.userId == chatId || chatId == -1)) {
                        activity?.apiSpiceMgr?.executeToastKt(ApplyHandleRequest().apply {
                            roomId = data.roomId
                            type = RoomApplyType.INVITE_UPSTREAM
                            result = 1
                        }, success = {
                            if (it.isSuccess || it.rtn == 502) {
                                VideoLiveRoomActivityLauncher.startActivity(activity, data.roomId, LiveEnterType.quick_consult_page_wait)
                            }
                        }, finish = { hideAnim(500) })
                    } else {
                    }
                }
                CustomType.reject_quick_apply_exclusive -> {
                    /**快速咨询拒绝消息*/
                    if (data.targetUserId == JohnUser.getSharedUser().userID) {
                        if (isShow) {
                            UIToast.toast(data.title)
                        }
                        hideAnim()
                    } else {
                    }

                }
                else -> {
                }
            }
        }
    }


    init {
        layoutInflaterKtToParentAttach(R.layout.view_upstream_loading)

        (parent as? ViewGroup)?.removeView(this)
        activity?.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)?.addView(this)
        translationY = screenHeight.toFloat() + dip(100)

        checkNewEvent.registered()
        activity?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    checkNewEvent.unregistered()
                    upstreamLoadingView = null
                }
            }
        })

        icon_cancel.setOnClickListener {
            cancelApply()
            if (proCateId != 0) {
                GrowingUtil.track("function_click",
                        "function_name", "快速咨询",
                        "function_page", "匹配知心人页",
                        "status", userType,
                        "click_type", "取消")
            }
        }
    }

    fun cancelApply() {
        (context as? MeiCustomActivity)?.let { activity ->
            activity.apiSpiceMgr.executeToastKt(if (proCateId != 0) ExclusiveMatchCancelRequest() else ExclusiveCancelRequest(), success = {
                if (it.isSuccess) {
                    hideAnim()
                }
            })
        }
    }

    var isShow = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        spread_avatar_view.startAnim()
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        spread_avatar_view.cancelAnim()
        try {
            mediaPlayer.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showAnim(info: WaitInfo?, callBack: ((Int) -> Unit)?) {
        this.info = info
        callBack?.let {
            this.callBack = it
        }
        callBack(1)
        KeyboardUtil.hideKeyboard(this)
        notifyView()
        if (!isShow) {
            isShow = true
            translationY = screenHeight.toFloat() + dip(100)
            animate().translationY(0f).setDuration(200).start()

            val timeout = info?.timeout ?: 0L
            if (timeout > 0) {
                postDelayed(hideRunnable.apply {
                    toast = info?.timeoutToastForInitiator.orEmpty()
                    couponId = info?.couponId ?: 0L
                    categoryId = info?.categoryId ?: 0
                }, timeout * 1000)
            }
            try {
                mediaPlayer.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val hideRunnable = object : Runnable {
        var toast = ""
        var couponId = 0L
        var categoryId = 0
        override fun run() {
            (context as? MeiCustomActivity)?.apiSpiceMgr?.executeToastKt(ExclusiveMatchTimeoutRequest().apply {
                mCouponId = couponId
                mCategoryId = categoryId
            })
            hideAnim(0, toast)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun notifyView() {
        mentor_avatar.glideDisplay(info?.avatar, info?.gender.genderAvatar())
        contact_text.text = info?.title.orEmpty()

        upstream_text.isVisible = info?.specialService == null
        upstream_text.text = info?.subTitle.orEmpty()
        coupon_icon.glideDisplay(info?.couponBackgroundImage.orEmpty())
        coupon_icon.isVisible = info?.couponBackgroundImage.orEmpty().isNotEmpty()
        coupon_text.isVisible = info?.couponBackgroundName.orEmpty().isNotEmpty()
        coupon_text.text = info?.couponBackgroundName.orEmpty()

        special_service_layout.isVisible = info?.specialService != null
        service_name.text = info?.specialService?.serviceName.orEmpty()
        service_duration.text = "${info?.specialService?.timeRemaining ?: 0}分钟"
    }

    fun hideAnim(delay: Long = 0, toast: String = "") {
        removeCallbacks(hideRunnable)
        runDelayedOnUiThread(delay) {
            callBack(2)
            if (isShow) {
                UIToast.toast(toast)
                isShow = false
                animate().translationY(screenHeight.toFloat() + dip(100)).setDuration(100).start()
                try {
                    mediaPlayer.pause()
                    mediaPlayer.seekTo(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 消费触摸事件
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }
}