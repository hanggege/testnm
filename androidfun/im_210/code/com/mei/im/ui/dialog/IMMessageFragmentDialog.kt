package com.mei.im.ui.dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.base.common.COUPON_TO_LIVE
import com.mei.base.common.MESSAGE_DIALOG_SHIELD_USER
import com.mei.im.ext.isCmdId
import com.mei.im.ui.fragment.IMMessageListFragment
import com.mei.im.ui.fragment.IMMessageListFragmentLauncher
import com.mei.im.ui.fragment.showInputState
import com.mei.live.ui.dialog.CommonBottomFragmentLauncher
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenHeight
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.util.logDebug
import com.net.MeiUser
import com.net.model.chick.tab.tabbarConfig
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.fragment_message_dialog_layout.*
import launcher.Boom
import kotlin.math.abs

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/22
 */
class IMMessageFragmentDialog : CustomSupportFragment() {

    @Boom
    var chatId: String = ""

    @Boom
    var extraData: String = ""
    var backDismissCallback: () -> Unit = {}

    var imMsgListFragment: IMMessageListFragment? = null
    private var keyBoardHeight = 0
    private var keyBoardIsShow = false
    private val checkNewEvent: IMAllEventManager by lazy {
        object : IMAllEventManager() {
            /** 监听最新消息 **/
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { !it.isDeleted() }//去掉已删除的消息

                /** 只接收发送指令的消息 **/
                customList.filter { it.peer.toIntOrNull().isCmdId() }
                        .forEach { handleIMEvent(it) }
                return super.onNewMessages(msgs)
            }
        }
    }

    private fun handleIMEvent(msg: CustomMessage) {
        val type = msg.customMsgType
        logDebug("IMMessageListDialog_handleIMEvent: ${type.name}")
        (msg.customInfo?.data as? ChickCustomData)?.let { data ->
            when (type) {
                CustomType.exclusive_block_notify -> {
                    chat_message_v.isVisible = data.userIds.any { it == chatId.getInt(0) }
                }
                else -> {

                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindAction<String>(COUPON_TO_LIVE) {
            gotoCouponDialog(it.orEmpty())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ActivityLauncher.bind(this)
        return inflater.inflate(R.layout.fragment_message_dialog_layout, container, false)
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNewEvent.bindEventLifecycle(this)
        chat_message_v.isVisible = false
        back_view.setOnClickNotDoubleListener {
            if (keyBoardIsShow) {
                KeyboardUtil.hideKeyboard(back_view)
            } else {
                parentFragment?.childFragmentManager?.popBackStackImmediate()
            }
            backDismissCallback()
        }
        fragment_message_dialog_layout.updateLayoutParams { height = (screenHeight * 0.7).toInt() }
        childFragmentManager.commitNow(allowStateLoss = true) {
            val fragment = IMMessageListFragmentLauncher.newInstance(chatId, extraData)
            imMsgListFragment = fragment
            replace(R.id.chat_message_container, fragment)
        }
        apiSpiceMgr.requestUserInfo(arrayOf(chatId.toIntOrNull() ?: 0)) { list ->
            val info = list.firstOrNull()
            top_title_tv.text = info?.nickname.orEmpty()
            val service = if (MeiUser.getSharedUser().info?.groupInfo != null) {
                info?.medalMap.orEmpty()[MeiUser.getSharedUser().info?.groupInfo?.groupId.toString()]
            } else {
                info?.medalMap.orEmpty()[JohnUser.getSharedUser().userIDAsString]
            }
            live_im_service_representation_dialog.apply {
                isVisible = service != null
                text = service?.medal.orEmpty()
            }
            if (info?.isPublisher == MeiUser.getSharedUser().info?.isPublisher) {
                UIToast.toast(activity, if (info?.isPublisher == true) "暂不支持知心达人之间的私聊功能" else "暂不支持用户之间的私聊功能")
                backDismissCallback()
            }
        }
        view.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            v?.let {
                /** meizu手机监听有问题 **/
                if (!isBrandMeizu()) {
                    if (oldBottom > 0 && abs(bottom - oldBottom) > dip(100)) {
                        keyBoardHeight = abs(bottom - oldBottom)
                    }
                    val isShow = oldBottom - bottom > dip(100)
                    val keyContentHeight = if (isShow) screenHeight * 0.7 - abs(keyBoardHeight) else screenHeight * 0.7
                    if (bottom != oldBottom) {
                        keyBoardIsShow = isShow
                        if (keyBoardIsShow) imMsgListFragment?.showInputState(1)
                        fragment_message_dialog_layout.updateLayoutParams { height = keyContentHeight.toInt() }
                        chat_message_container.updateLayoutParams { height = (keyContentHeight - dip(15)).toInt() }
                    }
                }
            }
        }
        chat_message_v.setOnClickListener {
            /**如果本人被拉黑，和对方的会话将会弹框提示结束*/
            showMessageListShieldDialog()
        }
        showMessageListShieldDialog()
    }

    private fun showMessageListShieldDialog() {
        apiSpiceMgr.requestImUserInfo(arrayOf(chatId.getInt(0)), needRefresh = true, back = {
            val info = it.firstOrNull()
            if (info?.isSelfToBlackList == true) {
                /**弹框提示对方离线*/
                val tips = when {
                    info.isPublisher && info.groupId != 0 -> {
                        tabbarConfig.groupBlacklistUser
                    }
                    info.isPublisher -> {
                        tabbarConfig.publisherBlacklistUser
                    }
                    else -> {
                        tabbarConfig.userBlacklistPublisher
                    }
                }
                activity?.showOneBtnCommonDialog(tips.orEmpty(), "退出私聊", false, okBack = {
                    back_view.performClick()
                })
                postAction(MESSAGE_DIALOG_SHIELD_USER, info.userId.toString())
            } else if (info?.isTheOtherToBlackList == true) {
                /**提示已拉黑对方，需要解除*/
                UIToast.toast(tabbarConfig.hasBlacklistTips.orEmpty())
                backDismissCallback()
                back_view.performClick()
                postAction(MESSAGE_DIALOG_SHIELD_USER, info.userId.toString())

            }
        })
    }

    private fun isBrandMeizu(): Boolean {
        return "meizu".equals(Build.BRAND, ignoreCase = true) || "meizu".equals(Build.MANUFACTURER, ignoreCase = true) || "22c4185e".equals(Build.BRAND, ignoreCase = true)
    }

    private fun gotoCouponDialog(url: String) {
        parentFragment?.childFragmentManager?.commit(true) {
            setCustomAnimations(R.anim.fragment_push_bottom_in,
                    R.anim.fade_out,
                    android.R.anim.fade_in,
                    R.anim.fragment_push_bottom_out)
            replace(R.id.chat_container, CommonBottomFragmentLauncher.newInstance(url), "CommonBottomFragment")
            addToBackStack("CommonBottomFragment")
        }
    }
}