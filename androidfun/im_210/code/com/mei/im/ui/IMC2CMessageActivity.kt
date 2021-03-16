package com.mei.im.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.forEach
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.commitNow
import com.gyf.immersionbar.ImmersionBar
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.listener.IMAllEventManager
import com.joker.im.listener.IMNotifyLevelInterface
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.base.common.SHIELD_USER
import com.mei.chat.ui.view.startPrivateUpstream
import com.mei.chat.ui.view.upstreamLoadingView
import com.mei.dialog.showSelectedReportReason
import com.mei.im.checkIMForceOffline
import com.mei.im.ui.dialog.showOneBtnCommonDialog
import com.mei.im.ui.ext.*
import com.mei.im.ui.fragment.IMMessageListFragment
import com.mei.im.ui.fragment.IMMessageListFragmentLauncher
import com.mei.im.ui.fragment.showInputState
import com.mei.live.ui.dialog.showSendGiftDialog
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.save.getBooleanMMKV
import com.mei.orc.util.string.getInt
import com.mei.player.view.IgnorePlayerBar
import com.mei.widget.actionbar.defaultRightView
import com.mei.widget.gradient.GradientTextView
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.MeiUtil
import com.net.MeiUser
import com.net.model.chick.report.ReportBean
import com.net.model.chick.tab.isShowShieldingBtn
import com.net.model.chick.tab.tabbarConfig
import com.net.model.rose.RoseFromSceneEnum
import com.net.model.user.UserInfo
import com.net.network.chick.report.ReportRequest
import com.pili.player
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.chat_c2c_activity.*
import kotlinx.android.synthetic.main.chat_c2c_header_private_layout.*
import launcher.Boom

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/20
 */
class IMC2CMessageActivity : MeiCustomActivity(), IMNotifyLevelInterface, IgnorePlayerBar {
    @Boom
    var chatId: String = ""

    @Boom
    var extraData: String = ""

    private fun reportPopupWindow(): PopupWindow {
        val contentView = layoutInflaterKt(R.layout.c2c_report_popu_layout)
        val popupWindow = PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isOutsideTouchable = true
            isFocusable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.attributes = window.attributes.apply { alpha = 0.4f }
            setOnDismissListener { window.attributes = window.attributes.apply { alpha = 1f } }
        }
        contentView.findViewById<TextView>(R.id.tv_c2c_shielding).apply {
            isVisible = if (MeiUser.getSharedUser().info?.isPublisher != true) {
                isShowShieldingBtn()
            } else true
            info?.let { info ->
                text = if (info.isBlackList) "解除拉黑" else "拉黑"
                setOnClickListener {
                    //拉黑
                    if (info.isBlackList) {
                        shieldedDelete(info.userId.toString())
                    } else {
                        shieldAddUser(info.userId.toString())
                    }
                    popupWindow.dismiss()
                }
            }
        }
        contentView.findViewById<TextView>(R.id.tv_c2c_report).setOnClickListener {
            //举报
            popupWindow.dismiss()
            showSelectedReportReason {
                apiSpiceMgr.executeToastKt(ReportRequest(ReportBean().apply {
                    reasonId = it.type
                    reportUser = chatId.toIntOrNull() ?: 0
                }), success = {
                    if (it.isSuccess) UIToast.toast(this, "举报成功")
                })
            }
        }
        return popupWindow
    }

    val imMsgListFragment: IMMessageListFragment by lazy { IMMessageListFragmentLauncher.newInstance(chatId, extraData) }

    val rightText by lazy {
        GradientTextView(this@IMC2CMessageActivity).apply {
            layoutParams = RelativeLayout.LayoutParams(dip(52), dip(26)).apply {
                addRule(RelativeLayout.CENTER_VERTICAL)
            }
            gravity = Gravity.CENTER
            text = ""
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextColor(getColor(R.color.color_333333))
            }
        }
    }

    // 主要是为了监听专属服务连线结束通知
    val checkNewEvent by lazy {
        object : IMAllEventManager() {
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val isVibration = "new_message_vibration_sb${MeiUser.getSharedUser().info?.userId}"
                        .getBooleanMMKV(true)
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { !it.isDeleted() && it.chatType == TIMConversationType.C2C }//去掉已删除的消息
                msgs?.forEach {
                    if (!it.isSelf && it.mapToMeiMessage()?.chatType == TIMConversationType.C2C) {
                        val data = (it.mapToMeiMessage() as? CustomMessage)?.customInfo?.data as? ChickCustomData
                        if (isVibration &&
                                (data == null || data.type != CustomType.exclusive_system_notify)) {
                            startVibrator()
                        }
                    }
                }
                customList.forEach {
                    handleNewMsgEvent(it)
                }
                return super.onNewMessages(msgs)
            }
        }
    }

    override fun customStatusBar(): Boolean = true
    override fun onResume() {
        super.onResume()
        if (getCacheUserInfo(chatId.getInt()) == null) {
            apiSpiceMgr.requestUserInfo(arrayOf(chatId.getInt()), needRefresh = true)
        }
        player.pause()
        statImC2cBrowseEvent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readChatId(intent)
        setContentView(R.layout.chat_c2c_activity)
        initView()
        requestData()
        supportFragmentManager.commitNow(allowStateLoss = true) { replace(R.id.im_msg_list_fragment, imMsgListFragment) }
        checkIMForceOffline {
            UIToast.toast(this, "聊天被踢，请重新登录")
            finish()
        }
        checkNewEvent.bindEventLifecycle(this)
        bindActionState()
    }


    /**
     *个人信息
     */
    var info: UserInfo? = null

    fun requestData() {
        apiSpiceMgr.requestUserInfo(arrayOf(chatId.toIntOrNull() ?: 0), true) { list ->
            info = list.firstOrNull()
            title = info?.nickname.orEmpty()
            mei_action_bar.centerContainer.forEach { (it as? TextView)?.text = info?.nickname.orEmpty() }
            mei_action_bar.rightContainer.forEach {
                (it as? RelativeLayout)?.isGone = info?.userId == 116 //知心小助手隐藏举报按钮
            }
            info?.isPublisher?.let {
                publisherInfo(chatId, it)
            }
            if (info?.isPublisher == MeiUser.getSharedUser().info?.isPublisher) {
                UIToast.toast(this, if (info?.isPublisher == true) "暂不支持知心达人之间的私聊功能" else "暂不支持用户之间的私聊功能")
                finish()
            }
            c2c_mask_view.isVisible = info?.isSelfToBlackList == true || info?.isTheOtherToBlackList == true
        }
    }

    /**拉黑处理*/
    private fun showShieldingDialog() {
        if (info?.isSelfToBlackList == true) {
            /**弹框提示对方离线*/
            val tips = when {
                info?.isPublisher == true && info?.groupId != 0 -> {
                    tabbarConfig.groupBlacklistUser
                }
                info?.isPublisher == true -> {
                    tabbarConfig.publisherBlacklistUser
                }
                else -> {
                    tabbarConfig.userBlacklistPublisher
                }
            }
            showOneBtnCommonDialog(tips.orEmpty(), "退出私聊", false, okBack = {
                postAction(SHIELD_USER, arrayListOf(info?.userId))
                finish()
            })

        } else if (info?.isTheOtherToBlackList == true) {
            /**提示已拉黑对方，需要解除*/
            postAction(SHIELD_USER, arrayListOf(info?.userId))
            UIToast.toast(tabbarConfig.hasBlacklistTips.orEmpty())
            finish()
        }
    }

    fun refreshChatConfig() {
        imMsgListFragment.refreshChatConfig()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.apply {
            /** 进行了新对话，销毁当前，重新创建一个对话 **/
            finish()
            startActivity(intent)
        }
    }

    private fun initView() {
        ImmersionBar.with(this).apply {
            statusBarDarkFont(true)
            statusBarColorInt(Color.WHITE)
            transparentStatusBar()
            statusBarView(status_bar_view)
            supportActionBar(showActionBar())
            keyboardEnable(true)
            setOnKeyboardListener { isPopup, _ ->
                if (isPopup) imMsgListFragment.showInputState(1)
            }
        }.init()
//        im_user_option_layout.isVisible = MeiUser.getSharedUser().info?.isPublisher == false
        c2c_mask_view.apply {
            isVisible = false
            setOnClickListener { showShieldingDialog() }
        }

        mei_action_bar.apply {
            bottomLine.isVisible = false
            rightContainer.addView(RelativeLayout(this@IMC2CMessageActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, resources.displayMetrics).toInt(),
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics).toInt()
                )
                addView(rightText)
                addView(defaultRightView().apply {
                    setOnClickListener {
                        reportPopupWindow().showAsDropDown(it, -dip(50), -dip(15))
                        window.attributes = window.attributes.apply { alpha = 0.4f }
                    }
                })
            })
            leftContainer.removeAllViews()
            leftContainer.addView(ImageView(context).apply {
                setPadding(dip(20), dip(16), dip(20), dip(16))
                setImageResource(R.drawable.bg_black_back_arrow)
            })
            leftContainer.setOnClickListener { finish() }
        }

        apply_private_live_layout.setOnClickNotDoubleListener {
            startPrivateUpstream(chatId.getInt())
        }

        send_private_gift_layout.setOnClickListener {
            //            imMsgListFragment.setGiftBannerVisible(true)
            apiSpiceMgr.requestUserInfo(arrayOf(chatId.toIntOrNull() ?: 0)) { list ->
                val info = list.firstOrNull()
                showSendGiftDialog(chatId.toInt(), info?.nickname.orEmpty(), "", roseFromScene = RoseFromSceneEnum.EXCLUSIVE_CHAT, fromType = "私信送礼")
            }
        }
    }


    private fun readChatId(intent: Intent) {
        val linkUrl = intent.dataString.orEmpty()
        if (linkUrl.isNotEmpty()) {
            /** 读取内链 **/
            chatId = MeiUtil.getOneID(linkUrl, AmanLink.URL.USER_CHAT_PATTERN)
        } else {
            ActivityLauncher.bind(this)
        }
        if (chatId == "") {
            UIToast.toast(this, "未找到该用户")
            finish()
        } else if (chatId == JohnUser.getSharedUser().userIDAsString) {
            UIToast.toast(this, "不能和自己聊天哦")
            finish()
        }
    }

    override fun isAllIgnore(): Boolean = false

    override fun IgnoreByID(): String = chatId

    override fun onPause() {
        super.onPause()
        if (imMsgListFragment.mIsStatistics) {
            statisticsGoC2cUnSendContent()
        }
    }

    override fun onBackPressed() {
        if (upstreamLoadingView?.isShow == true && upstreamLoadingView?.context is IMC2CMessageActivity) {
            upstreamLoadingView?.cancelApply()
        } else {
            super.onBackPressed()
        }
    }


}