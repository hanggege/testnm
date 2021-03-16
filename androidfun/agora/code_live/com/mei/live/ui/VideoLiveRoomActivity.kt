package com.mei.live.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.jaeger.library.StatusBarUtil
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.*
import com.mei.GrowingUtil
import com.mei.agora.agoraConfig
import com.mei.agora.event.AgoraEventHandler
import com.mei.base.common.*
import com.mei.dialog.myRose
import com.mei.init.spiceHolder
import com.mei.keyboard.KeyboardHeightProvider
import com.mei.keyboard.keyboardBindLife
import com.mei.live.LiveEnterType
import com.mei.live.action.IgnoreSystemAction
import com.mei.live.manager.*
import com.mei.live.parseLiveEnterType
import com.mei.live.ui.fragment.*
import com.mei.login.toLogin
import com.mei.orc.ActivityLauncher
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.*
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.keyboard.hideKeyBoard
import com.mei.orc.util.keyboard.showKeyBoardDelay
import com.mei.player.view.IgnorePlayerBar
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.mei.wood.cache.clearUserCache
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.constant.Preference.ROOM_ENTER
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.event.ISystemInviteJoinShow
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.subStringEnd
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.TabMainActivity
import com.mei.wood.util.AppManager
import com.mei.wood.util.logDebug
import com.mei.wood.util.parseValue
import com.net.MeiUser
import com.net.model.broadcast.PlayType
import com.net.model.chick.global.TipsGet
import com.net.model.chick.tab.tabbarConfig
import com.net.model.gift.GiftInfo
import com.net.model.gift.GiftListInfo
import com.net.model.room.RoomInfo
import com.net.model.room.RoomType
import com.net.model.user.UserInfo
import com.net.network.gift.GiftListInfoRequest
import com.net.network.room.*
import com.pili.getPlayInfo
import com.pili.player
import com.tencent.imsdk.TIMCallBack
import com.tencent.imsdk.TIMGroupManager
import com.trello.rxlifecycle3.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.include_net_error_layout.*
import kotlinx.android.synthetic.main.video_live_room_layout.*
import launcher.Boom
import launcher.MulField
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-11-25
 */
val giftInfoList = arrayListOf<GiftInfo>()

/**
 * 请求礼物列表信息
 */
fun requestGiftInfo(back: (GiftListInfo?) -> Unit = { _ -> }) {
    spiceHolder().apiSpiceMgr.executeKt(GiftListInfoRequest(), success = {
        it.data?.let { data ->
            giftInfoList.clear()
            giftInfoList.addAll(data.gifts.orEmpty())
        }
        back(it.data)
    }, failure = { back(null) })
}

fun RoomInfo.getRoomTypeForGrowingTrack(): String {
    return when {
        isSpecialStudio -> "工作室小直播间"
        isStudio -> "工作室大直播间"
        roomType == RoomType.EXCLUSIVE -> "专属房"
        else -> "普通房"
    }
}

class VideoLiveRoomActivity : MeiCustomActivity(), AgoraEventHandler, ISystemInviteJoinShow, IgnorePlayerBar, IgnoreSystemAction {

    @Boom
    var roomId: String = ""

    @Boom
    var fromType: LiveEnterType = LiveEnterType.none

    @MulField
    @Boom
    var roomInfo: RoomInfo = RoomInfo()

    @MulField
    @Boom
    var customInfo: String = ""

    @MulField
    @Boom
    var currTag: String = ""


    val agoraManager: AgoraSurfaceManager by lazy { getAgoraSurfaceManager(roomInfo.customBeautyConfigKey.orEmpty()) }

    /**排麦列表的弹窗*/
    var upstreamListDialog: BottomDialogFragment? = null

    /** 是否等待连线 **/
    var pendingUpStream = false
    var atUserMap = mutableMapOf<Int, UserInfo>()

    /**是否退出了房间，主要为了兼容服务器 先调用enterRoom接口后调用exitRoom接口*/
    private var isExitRoom = false

    var liveVideoSplitFragment: LiveVideoSplitFragment by Delegates.notNull()
    val liveGiftBannerFragment by lazy { LiveBannerGiftFragment() }
    val liveFullScreenGiftFragment by lazy { FullScreenGiftFragment() }
    val liveTopContainerFragment by lazy { LiveTopContainerFragment() }
    private val liveRoomEnterAnimFragment by lazy { LiveRoomEnterAnimFragment() }
    val liveIMSplitFragment by lazy {
        LiveIMSplitFragment().apply {
            showKeyBoard = { showInputKey(null) }
        }
    }

    private var keyboardHeightProvider: KeyboardHeightProvider? = null

    override fun customStatusBar() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rtcEngine = agoraConfig.rtcEngine()
        if (rtcEngine == null) {
            UIToast.toast("初始化失败,请重新进入试试")
            finish()
            return
        }
        // 强制打开声网音频模块
        rtcEngine.enableAudio()

        readIntent(intent)
        clearUserCache()
        setContentView(R.layout.video_live_room_layout)
        if (Build.MANUFACTURER == "Meizu") {
            StatusbarColorUtils.setStatusBarDarkIcon(window, true)
        } else {
            StatusBarUtil.setColor(this, Color.parseColor("#36150E"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.navigationBarColor = Color.parseColor("#36150E")
            }
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        initFragment()
        initEvent()
        root_layout.post {
            if (roomInfo.roomId.orEmpty().isNotEmpty()) {
                startLiveCountDown()

                notifyRoomData(roomInfo)
            } else {
                requestRoomInfo()
            }
        }
        myRose()
        if (giftInfoList.isEmpty()) {
            requestGiftInfo()
        }
        /**发现登陆态发生变化 直接退出直播间*/
        bindAction<Boolean>(CHANG_LOGIN) {
            if (!JohnUser.getSharedUser().hasLogin()) {
                finish()
            }
        }
        /** 键盘监听 **/
        keyboardHeightProvider = this.keyboardBindLife(this) { height ->
            live_input_layout.isVisible = height > screenHeight / 4
            live_input_layout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = height
            }
        }
        /** 提前拉一遍自己的用户信息 **/
        MeiUser.resetUser(spiceHolder().apiSpiceMgr, null)
        if (AppManager.getInstance().hasActivitys(TabMainActivity::class.java).isNullOrEmpty()) {
            startActivity(Intent(this, TabMainActivity::class.java))
        }
    }

    private var countDownTimer: CountDownTimer? = null

    /**
     * 开启直播倒计时
     */
    private fun startLiveCountDown() {
        count_down_text.isVisible = true
        countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onFinish() {
                count_down_text.isVisible = false
            }

            override fun onTick(millisUntilFinished: Long) {
                count_down_text.text = (millisUntilFinished / 1000 + 1).toString()
            }
        }.start()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val newId = intent?.getStringExtra("com.mei.live.ui.roomIdIntentKey").orEmpty()
        val newRoomInfo = intent?.getSerializableExtra("com.mei.live.ui.roomInfoIntentKey") as? RoomInfo
        val customInfo = intent?.getStringExtra("com.mei.live.ui.customInfoIntentKey")
        if (newId.isNotEmpty() && newId != roomId) {
            /**关掉所有弹窗*/
            supportFragmentManager.fragments.forEach {
                (it as? DialogFragment)?.dismissAllowingStateLoss()
            }
            apiSpiceMgr.cancelAllRequest()
            stopSurfaceManager()
            agoraConfig.workThread.leaveChannel()
            TIMGroupManager.getInstance().quitGroup(roomInfo.roomId, object : TIMCallBack {
                override fun onSuccess() {
                }

                override fun onError(p0: Int, p1: String?) {
                    Log.e("info", " error: $p0  $p1 ")
                }

            })
            setIntent(intent)
            readIntent(intent)
            spiceHolder().apiSpiceMgr.executeKt(RoomEnterRequest(newId), finish = {
                postAction(ROOM_ENTER)
            })
            initFragment()
            if (newRoomInfo?.roomId.orEmpty().isNotEmpty()) {
                startLiveCountDown()

                roomInfo = newRoomInfo!!
                notifyRoomData(roomInfo)
            } else {
                requestRoomInfo()
            }
        } else if (newId.isNotEmpty()) {
            //还在当前的直播间，如果收到了发送自定义的消息
            this.customInfo = customInfo ?: ""
            sendCustomInfoMsg(roomInfo)

            /**
             * 如果用户收到消息回到直播间 主动上报查询是否需要上麦
             */
            if (!liveVideoSplitFragment.isPushStreaming && !agoraManager.isStreaming) {
                apiSpiceMgr.executeKt(AgoraStatusRequest(roomInfo.roomId, 112, roomInfo.roomType), success = {
                    it.data?.roomInfoChange?.let { roomInfoChange ->
                        roomInfo.upstreamCouponItem = roomInfoChange.upstreamCouponItem
                        if (roomInfoChange.allowUsers.orEmpty().contains(JohnUser.getSharedUser().userID)) {
                            val videoMode = roomInfoChange.videoMode
                            liveVideoSplitFragment.startStream(videoMode != 1)
                        }
                    }
                })
            }
        }
    }

    private fun readIntent(intent: Intent?) {
        val uriString = intent?.dataString.orEmpty()
        if (uriString.isNotEmpty()) {
            roomId = uriString.parseValue("room_id")
            fromType = parseLiveEnterType(uriString.parseValue("from_type"))
        } else {
            ActivityLauncher.bind(this)
        }
    }

    private fun initFragment() {
        liveVideoSplitFragment = LiveVideoSplitFragment()
        supportFragmentManager.commitNow(true) {
            replace(R.id.video_split_layout, liveVideoSplitFragment)
        }
        supportFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.live_top_container, liveTopContainerFragment)
            replace(R.id.live_bottom_container, liveIMSplitFragment)
        }
    }

    private fun initEvent() {
        supportFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.live_gift_banner, liveGiftBannerFragment)
            replace(R.id.full_screen_gift_fragment, liveFullScreenGiftFragment)
            replace(R.id.room_enter_fragment, liveRoomEnterAnimFragment)
            hide(liveFullScreenGiftFragment)
            hide(liveRoomEnterAnimFragment)
        }
        back_choice.setOnClickListener { finish() }
        net_error_layout.setOnBtnClick(View.OnClickListener {
            if (JohnUser.getSharedUser().hasLogin()) requestRoomInfo()
            else toLogin { succ, _ -> if (succ) requestRoomInfo() }
        })
        live_input_text_edit.addTextChangedListener {
            live_send_message_tv.apply {
                isSelected = it.isNotEmpty()
                if (isSelected) {
                    setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    delegate.applyViewDelegate {
                        startColor = ContextCompat.getColor(context, R.color.color_FF7F33)
                        endColor = ContextCompat.getColor(context, R.color.color_FF3F36)
                        radius = 13.dp
                    }
                } else {
                    setTextColor(ContextCompat.getColor(context, R.color.color_999999))
                    delegate.applyViewDelegate {
                        startColor = ContextCompat.getColor(context, R.color.color_f8f8f8)
                        radius = 13.dp
                    }
                }
            }
            if (it.isEmpty()) atUserMap.clear()
        }
        live_send_message_tv.setOnClickListener {
            /** 发送消息 **/
            val inputContent = live_input_text_edit.text.toString()
            if (inputContent.trim().isNotEmpty()) {
                /**直播间消息发送埋点*/
                GrowingUtil.track("broadcast_send_click",
                        "room_id", roomId, "broadcast_id", roomInfo.broadcastId,
                        "user_id", roomInfo.createUser?.userId.toString(),
                        "room_type", roomInfo.getRoomTypeForGrowingTrack(),
                        "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())

                live_send_message_tv.isClickable = false
                checkMuteState(roomInfo.roomId) { hasPermission ->
                    if (hasPermission) {
                        liveSendCustomMsg(roomInfo.roomId, CustomType.send_text, applyData = {
                            val name = MeiUser.getSharedUser().info?.nickname.orEmpty()
                            if (MeiUser.getSharedUser().info?.userId ?: 0 <= 0) {
                                MeiUser.resetUser(spiceHolder().apiSpiceMgr, null)
                            }
                            content = arrayListOf<SplitText>().apply {
                                if (name.isNotEmpty()) add(SplitText("$name：", "#B3FFFFFF"))
                                add(SplitText(inputContent))
                            }
                            roomType = roomInfo.roomType.name
                            fromUserHandle = true
                            at = atUserMap.values.map { AtUserInfo(it.userId.toString(), it.nickname) }
                            msgState = when (liveIMSplitFragment.checkUserStatus()) {
                                0 -> 0
                                2 -> if (roomInfo.roomType == RoomType.EXCLUSIVE) 1 else 0
                                else -> {
                                    /** 主播At连麦用户时，连麦用户可以看到消息,ad **/
                                    val upStreamList = liveVideoSplitFragment.upstreamUserIds.filter { it != JohnUser.getSharedUser().userID }
                                    if (roomInfo.roomType == RoomType.EXCLUSIVE && upStreamList.any { at.map { it.id }.contains(it.toString()) }) 1
                                    else 0
                                }
                            }
                        }) { code ->
                            when (code) {
                                0 -> {
                                    atUserMap.clear()
                                    live_input_text_edit.setText("")
                                    live_input_text_edit.hideKeyBoard()
                                    roomInfo.hasSendMessage = true
                                }
                                10017 -> UIToast.toast(activity, "你已被禁言")
                                else -> UIToast.toast(activity, "发送失败$code，请重试")
                            }
                            live_send_message_tv.isClickable = code != 0
                        }
                    } else {
                        live_send_message_tv.isClickable = true
                        UIToast.toast(this, "您已被禁言，无法发送消息")
                    }
                }
            } else {
                UIToast.toast(this, "发送内容不能为空")
            }
        }
        bindAction<String>(HAS_BLACKLIST) {
            showShieldingDialog("当前直播已结束")
        }
        bindAction<List<Int>>(SHIELD_USER) {
            if (it?.any { id -> id == roomInfo.createUser?.userId } == true) {
                postAction(HAS_BLACKLIST)
            }
        }
    }


    /**
     * 打开输入键盘
     */
    @SuppressLint("SetTextI18n")
    fun showInputKey(userInfo: UserInfo? = null) {
        live_send_message_tv.isClickable = true
        live_input_layout.isVisible = true
        live_input_text_edit.requestFocus()
        live_input_text_edit.showKeyBoardDelay()

        userInfo?.let { info ->
            atUserMap[info.userId] = info
            if (!live_input_text_edit.text.endsWith("@ ${info.nickname} ")) {
                live_input_text_edit.setText("${live_input_text_edit.text}@ ${info.nickname} ")
            }
            live_input_text_edit.setSelection(live_input_text_edit.text.length)
        }
    }

    /**
     * 请求房间信息
     */
    private fun requestRoomInfo() {
        showLoadingCover()
        apiSpiceMgr.executeKt(RoomInfoRequest(roomId, "", currTag, fromType.name), success = {
            val data = it.data?.roomInfo
            if (it.isSuccess && data != null) {
                roomInfo = data
                roomId = data.roomId
                notifyRoomData(data)
                net_error_layout.apply {
                    isVisible = false
                    setBtnText(if (JohnUser.getSharedUser().hasLogin()) "重新加载" else "点击登录")
                }
                back_choice.isVisible = false
            } else {
                net_error_layout.apply {
                    isVisible = true
                    setEmptyText(it.errMsg)
                    setBtnText(if (JohnUser.getSharedUser().hasLogin()) "重新加载" else "点击登录")
                }
                back_choice.isVisible = true
                when (it.rtn) {
                    569 -> {
                        UIToast.toast(this, "直播已结束")
                        finish()
                    }
                    571 -> {
                        //拉黑
                        net_error_layout.apply {
                            isVisible = true
                            setEmptyText("")
                            setEmptyImage(0)
                            setBtnVisible(false)
                        }
                        showShieldingDialog(it.errMsg)
                    }

                    403, 560, 570 -> {
                        UIToast.toast(this, it.errMsg)
                        finish()
                    }
                }
            }
        }, failure = {
            net_error_layout.apply {
                isVisible = true
                setEmptyText(getString(R.string.no_network))
                setBtnText(if (JohnUser.getSharedUser().hasLogin()) "重新加载" else "点击登录")
            }
            back_choice.isVisible = true
        }, finish = {
            showLoading(false)
        })
    }

    /**拉黑弹框*/
    private fun showShieldingDialog(content: String) {
        val successDialog = NormalDialogLauncher.newInstance()
        val view = layoutInflaterKt(R.layout.shielded_layout_dialog)
        view.findViewById<TextView>(R.id.invite_title)?.text = content
        view.findViewById<TextView>(R.id.confirm_dialog_btn).apply {
            text = "知道了"
            setOnClickListener {
                successDialog.dismissAllowingStateLoss()
                finish()
            }
        }
        successDialog.showDialog(activity, DialogData(customView = view, canceledOnTouchOutside = false, backCanceled = false), okBack = {}, back = {
            successDialog.dismissAllowingStateLoss()
            finish()
        })
    }

    private var hasMSGSend = false
    private fun notifyRoomData(data: RoomInfo) {
        GrowingUtil.track(
                "zhixinli_app_room_uv",
                "room_id", roomId,
                "broadcast_id", data.broadcastId,
                "user_id", data.createUser?.userId.toString(),
                "room_type", data.getRoomTypeForGrowingTrack(),
                "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString()
        )

        applyJoinGroup {
            liveTopContainerFragment.roomInfo = roomInfo
            liveVideoSplitFragment.roomInfo = roomInfo
            liveIMSplitFragment.roomInfo = roomInfo

            spiceHolder().apiSpiceMgr.executeKt(RoomEnterRequest(roomId), finish = {
                if (isExitRoom) {
                    spiceHolder().apiSpiceMgr.executeKt(RoomExitRequest(roomId))
                }
                postAction(ROOM_ENTER)
            })

            if (it && !hasMSGSend) {
                runDelayedOnUiThread(500) {
                    apiSpiceMgr.requestUserInfo(arrayOf(JohnUser.getSharedUser().userID)) { list ->
                        hasMSGSend = true
                        val name = list.firstOrNull()?.nickname
                                ?: MeiUser.getSharedUser().info?.nickname.orEmpty()
                        //是否隐身
                        val roomInvisible = MeiUser.getSharedUser().info?.roomInvisible == 1
                        if (!roomInvisible) {
                            /** 进入房间发送一个进入直播间信息 **/
                            liveSendCustomMsg(data.roomId, CustomType.join_group, applyData = {
                                content = arrayListOf(SplitText(name, "#A3E2FB"),
                                        SplitText(" 来了 ", "#B3FFFFFF"))
                                userId = JohnUser.getSharedUser().userID
                                roomType = roomInfo.roomType.name
                            })
                        }

                        if (!roomInvisible) {
                            /** 大于等于4级的进入直播间才有进场效果，且有进场效果时，不需要展示进直播间的飘屏消息 **/
                            val info = list.firstOrNull()
                            if (info != null && info.userLevel >= 4 && tabbarConfig.showUserLevel && !roomInfo.isCreator) {
                                requestEnterRoomConfig { effect ->
                                    effect.list.firstOrNull { config -> config.level == info.userLevel }?.let { configItem ->
                                        liveSendCustomMsg(data.roomId, CustomType.room_enter_anim, applyData = {
                                            userId = roomInfo.createUser?.userId ?: 0
                                            roomEnterAnim = RoomEnterAnim(info.userId, info.userLevel, configItem.text.orEmpty())
                                        })
                                    }
                                }
                            } else {
                                liveSendCustomMsg(data.roomId, CustomType.action_notify, applyData = {
                                    content = arrayListOf(SplitText("${name.subStringEnd(6)} 来了"))
                                    roomId = data.roomId
                                    userId = JohnUser.getSharedUser().userID
                                    roomType = roomInfo.roomType.name
                                })
                            }
                        }
                        /**如果有自定义消息就需要发送一个自定义消息*/
                        sendCustomInfoMsg(data)
                    }
                }
            }
        }

        if (!roomInfo.isCreator) {
            startCountDown()
        }

        room_enter_fragment.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = if (roomInfo.isCreator) screenHeight - dip(360) else dip(250)
        }
    }

    /**
     * 发送自定义消息
     */
    private fun sendCustomInfoMsg(data: RoomInfo) {
        if (customInfo.isNotEmpty()) {
            customInfo.json2Obj<ChickCustomData.Result>()?.let { cInfo ->
                val customType = cInfo.getType()
                cInfo.data?.let { cData ->
                    liveSendCustomMsg2(data.roomId, customType, data = cData)
                }
            }
        }
    }

    private fun applyJoinGroup(back: (succ: Boolean) -> Unit) {
        TIMGroupManager.getInstance().applyJoinGroup(roomInfo.roomId, "", object : TIMCallBack {
            override fun onSuccess() {
                back(true)
            }

            override fun onError(p0: Int, p1: String?) {
                logDebug("group apply join", "code:$p0  msg:$p1")
                when (p0) {
                    10013 -> back(true)
                    10006 -> applyJoinGroup(back)
                    else -> {
                        back(false)
                        UIToast.toast(this@VideoLiveRoomActivity, "请重新进入试试 code:${p0}")
                        this@VideoLiveRoomActivity.finish()
                    }
                }

            }
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val currentFocus = currentFocus
            if (currentFocus != null && isShouldHideInput(currentFocus, ev)) {
                currentFocus.hideKeyBoard()
                currentFocus.clearFocus()
            }
            return super.dispatchTouchEvent(ev)
        }
        if (window?.superDispatchTouchEvent(ev) == true) {
            return true
        }
        return onTouchEvent(ev)
    }

    private fun isShouldHideInput(currentFocus: View?, ev: MotionEvent): Boolean {
        if (currentFocus != null && currentFocus is EditText) {
            val leftTop = intArrayOf(0, 0)
            live_input_layout.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val right = left + live_input_layout.width
            val bottom = top + live_input_layout.height
            return !(ev.x > left && ev.x < right && ev.y > top && ev.y < bottom)
        }
        return false
    }

    override fun onBackPressed() {
        liveVideoSplitFragment.onBackPressed(false)
    }

    override fun finish() {
        quitRoom()
        liveVideoSplitFragment.apply {
            callServiceIsStop()
            reportRunnable?.let { run -> postHandler.removeCallbacks(run) }
        }
        super.finish()
        AppManager.getInstance().removeActivity(this)
    }


    override fun onResume() {
        super.onResume()
        refreshServiceMute()
        if (getPlayInfo().mPlayType != PlayType.NONE) {
            postAction(MUTE_REMOTE_AUDIO_STREAMS, false)
        }
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        quitRoom()
        if (fromType == LiveEnterType.quick_consult_page_wait) { //直播间下麦后刷新快速咨询web
            postAction(WEB_REFRESH, true)
        }
    }

    private fun quitRoom() {
        isExitRoom = true
        countDownTimer?.cancel()
        stopSurfaceManager()
        upstreamListDialog = null
        agoraConfig.workThread.leaveChannel()
        spiceHolder().apiSpiceMgr.executeKt(RoomExitRequest(roomId))
        if (roomInfo.isCreator) {
            /** 通知用户红娘下麦 **/
            liveSendCustomMsg(roomInfo.roomId, CustomType.room_end, applyData = {
                roomRelated = RoomMsgData().apply { roomId = roomInfo.roomId }
                roomType = roomInfo.roomType.name
            })
        } else {
            liveSendCustomMsg(roomInfo.roomId, CustomType.quit_group, applyData = {
                userId = JohnUser.getSharedUser().userID
            }) {
                TIMGroupManager.getInstance().quitGroup(roomInfo.roomId, object : TIMCallBack {
                    override fun onSuccess() {
                    }

                    override fun onError(p0: Int, p1: String?) {
                        Log.e("info", " error: $p0  $p1 ")
                    }
                })
            }
        }
    }

    /**
     * 如果用户在直播间呆超过10分钟，接口通知服务器
     */
    private fun startCountDown() {
        Observable.timer(10, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeBy {
                    apiSpiceMgr.executeKt(StatisticsWatchRequest(10))
                }
    }

    override fun isShow(sendId: String, info: InviteJoinInfo): Boolean {
        return info.roomId != roomId
    }

    override fun isShow(data: TipsGet.ActionInfo?): Boolean {
        return data?.roomId != roomId && !isUpstream()
    }

    private fun stopSurfaceManager() {
        if (agoraManager.isStreaming) {
            runAsync {
                agoraManager.stopBroadcast()
            }
        }
    }
}