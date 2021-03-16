package com.mei.im.ui.fragment

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.joker.im.Message
import com.joker.im.custom.CustomInfo
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.SplitText
import com.joker.im.message.CustomMessage
import com.joker.im.message.ImageMessage
import com.joker.im.message.TextMessage
import com.joker.im.newMessages
import com.mei.GrowingUtil
import com.mei.base.common.CANCEL_SHIELD_USER
import com.mei.base.ui.nav.Nav
import com.mei.chat.ui.adapter.item.IMBaseMessageHolder
import com.mei.chat.ui.adapter.item.send.IM_CUSTOM_IS_PAY_FAIL
import com.mei.chat.ui.view.startPrivateUpstream
import com.mei.im.ui.IMC2CMessageActivity
import com.mei.im.ui.dialog.showIMServiceWebBottomFragmentDialog
import com.mei.im.ui.view.menu.ChatMenu
import com.mei.im.ui.view.showIMMenuBubblePopWindow
import com.mei.init.spiceHolder
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showCommonBottomDialog
import com.mei.live.ui.dialog.showSendGiftDialog
import com.mei.orc.Cxt
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.app.copyToClipboard
import com.mei.orc.util.save.getNonValue
import com.mei.orc.util.save.putValue
import com.mei.orc.util.string.getInt
import com.mei.picker.pickerImage
import com.mei.picker.tokePhoto
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfos
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MeiWebActivityLauncher
import com.mei.wood.web.MeiWebData
import com.net.MeiUser
import com.net.model.chat.ChatAuth
import com.net.model.rose.RoseFromSceneEnum
import com.net.network.chat.ChatAuthRequest
import com.net.network.chick.course.ToReceiveCourseRequest
import com.net.network.chick.report.ShieldingDeleteRequest
import kotlinx.android.synthetic.main.im_message_list_fragment.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/20
 */


@SuppressLint("ClickableViewAccessibility")
fun IMMessageListFragment.initView() {
    mentor_menu_layout.isVisible = MeiUser.getSharedUser().info?.isPublisher == true

    im_more_menu_layout.apply {
        im_more_menu_layout.isVisible = true
        im_quick_msg_layout.isVisible = true
        im_more_menu_layout.updateLayoutParams { height = 0 }
        im_quick_msg_layout.updateLayoutParams { height = 0 }
    }
    show_more_menu.setOnClickListener {
        showInputState(if (im_more_menu_layout.measuredHeight > 0) 0 else 2)
    }
    mentor_quick_msg_tv.setOnClickListener { showInputState(3) }
    mentor_show_keyboard.setOnClickListener { showInputState(1) }
    send_msg_tv.setOnClickListener {
        val text = im_input_edit.text?.toString().orEmpty()
        if (text.isNotEmpty()) {
            send_msg_tv.isClickable = false
            sendMessage(text) {
                if (it) im_input_edit.setText("")
                send_msg_tv.isClickable = true


            }

        }
    }

    im_input_edit.addTextChangedListener {
        //        TransitionManager.beginDelayedTransition(input_bar_layout)
        send_msg_tv?.isVisible = it?.toString().orEmpty().isNotEmpty()
        show_more_menu?.isVisible = !(send_msg_tv?.isVisible ?: false)
    }

    im_message_recycler.setOnTouchListener { _, _ ->
        showInputState(0)
        false
    }
    im_message_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //加上闲置的状态才加载更多，不然列表会出现滑动不了，导致滑不懂（2.2.6版本修改：小强）
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                    if (messageList.size > 0 && layoutManager.findFirstVisibleItemPosition() <= 4) {
                        // : joker 2019/3/18 加载更多
                        chatProvider.loadMessage()
                    }
                }
            }
            showInputState(0)
        }
    })
    im_coupon_hint.setOnClickListener {
        val url = if (MeiUser.getSharedUser().info?.isPublisher == true) {
            MeiUtil.appendParamsUrl(chatConfig?.couponTips?.action, "publisher" to imChatId)
        } else {
            MeiUtil.appendParamsUrl(chatConfig?.couponTips?.action,
                    "user" to JohnUser.getSharedUser().userID.toString(),
                    "publisher_id" to imChatId)
        }
        activity?.showCommonBottomDialog(url)
    }

    if (imChatId == "116") { // 知心小助手
        input_bar_layout.isVisible = false
        input_bottom_layout.setPadding(0)
    }
}

fun IMMessageListFragment.initMenu(list: List<ChatMenu>? = null) {
    val menuList = arrayListOf<ChatMenu>()
    //如果接口请求失败使用默认配置数据
    if (list.isNullOrEmpty()) {
        val menuNames: ArrayList<String>?
        val menuTypes: ArrayList<Int>?
        val menuIcons: ArrayList<Int>? = arrayListOf()
        if (MeiUser.getSharedUser().info?.isPublisher == true) {
            menuNames = Cxt.getRes().getStringArray(R.array.publisher_menu_text).toList() as? ArrayList<String>
            val ar = Cxt.getRes().obtainTypedArray(R.array.publisher_menu_res)
            for (i in 0 until ar.length()) {
                menuIcons?.add(ar.getResourceId(i, 0))
            }
            ar.recycle()
            menuTypes = Cxt.getRes().getIntArray(R.array.publisher_menu_type).toList() as? ArrayList<Int>
        } else {
            menuNames = Cxt.getRes().getStringArray(R.array.common_menu_text).toList() as? ArrayList<String>
            val ar = Cxt.getRes().obtainTypedArray(R.array.common_menu_res)
            for (i in 0 until ar.length()) {
                menuIcons?.add(ar.getResourceId(i, 0))
            }
            ar.recycle()
            menuTypes = Cxt.getRes().getIntArray(R.array.common_menu_type).toList() as? ArrayList<Int>
        }
        val isLiveActivity = context is VideoLiveRoomActivity

        if (isLiveActivity) {
            menuNames?.removeAt(2)
            menuTypes?.removeAt(2)
            menuIcons?.removeAt(2)
        } else if (context is IMC2CMessageActivity) {
            if (MeiUser.getSharedUser().info?.groupRole ?: 0 > 0) {
                //如果是分析师，删掉私密连线 和 课程服务
                menuNames?.removeAt(2)
                menuTypes?.removeAt(2)
                menuIcons?.removeAt(2)
            }
        }
        (menuNames.orEmpty().indices).forEach {
            val name = menuNames?.getOrNull(it)
            val type = menuTypes?.getOrNull(it) ?: 0
            val icon = menuIcons?.getOrNull(it) ?: 0
            if (!name.isNullOrEmpty() && type > 0 && icon > 0) {
                menuList.add(ChatMenu().apply {
                    this.menuIconId = icon
                    this.menuName = name
                    menuType = type
                })
            }
        }
    } else {
        menuList.addAll(list)
    }

    im_more_menu_layout.setMenuList(menuList, clickItem = {
        when (it?.menuType) {
            ChatMenu.MenuType.TYPE_PIC -> {
                activity?.pickerImage(1) { list ->
                    list.firstOrNull()?.let { sendMessage(it, true) }
                }
            }
            ChatMenu.MenuType.TYPE_CAMERA -> {
                activity?.tokePhoto { file ->
                    file?.apply { if (exists()) sendMessage(absolutePath, true) }
                }
            }
            ChatMenu.MenuType.TYPE_ATTACHMENT -> {
                (activity as? MeiCustomActivity)?.startPrivateUpstream(imChatId.getInt())
            }
            ChatMenu.MenuType.TYPE_SEND_GIFT -> {
                //普通用户点击
                apiSpiceMgr.requestUserInfo(arrayOf(imChatId.toIntOrNull() ?: 0)) { list ->
                    val info = list.firstOrNull()
                    activity?.showSendGiftDialog(imChatId.toInt(), info?.nickname.orEmpty(), "", roseFromScene = RoseFromSceneEnum.EXCLUSIVE_CHAT, fromType = "私信送礼")
                }
            }
            ChatMenu.MenuType.TYPE_COURSE_SERVICE,
            ChatMenu.MenuType.TYPE_SEND_TEST -> {
                val url = MeiUtil.appendParamsUrl(it.actionUrl
                        , "publisher_id" to JohnUser.getSharedUser().getUserID().toString()
                        , "is_screen" to if (activity is IMC2CMessageActivity) "false" else "true",
                        "group_id" to (MeiUser.getSharedUser().info?.groupInfo?.groupId
                                ?: 0).toString(),
                        "from" to "im")
                activity?.showIMServiceWebBottomFragmentDialog(url) { result ->
                    result?.also { sendCustomMsg(result) }
                }
            }
        }
    })


}

fun IMMessageListFragment.adapterItemLongClick(view: View, position: Int) {
    messageList.getIndexOrNull(position)?.let { msg: Message ->
        val list = when {
            msg is TextMessage -> arrayListOf("删除", "复制")
            (msg as? CustomMessage)?.customInfo?.data is ChickCustomData -> {
                when ((msg as? CustomMessage)?.customMsgType) {
                    CustomType.notify,
                    CustomType.send_text,
                    CustomType.action_notify -> arrayListOf("删除", "复制")
                    else -> arrayListOf("删除")
                }
            }
            else -> arrayListOf("删除")
        }
        val location = IntArray(2)
        im_message_recycler.getLocationInWindow(location)
        val maxHeight = location[1] + im_message_recycler.measuredHeight + dip(45)
        view.showIMMenuBubblePopWindow(list, offsetY = -dip(3), maxHeight = maxHeight) {
            when (it) {
                0 -> {
                    msg.removeMessage()
                    messageList.remove(msg)
                    messageList.firstOrNull()?.checkHasTime(null)
                    messageAdapter.notifyDataSetChanged()
                }
                1 -> {
                    if (activity?.copyToClipboard(msg.getCopySummary()) == true) UIToast.toast(activity, "复制成功")
                }
            }
        }
    }

}


fun IMMessageListFragment.goToServiceDetailPage(msg: Message?) {
    if (msg is CustomMessage) {
        val info = msg.customInfo
        if (info is CustomInfo) {
            if (info.data is ChickCustomData) {
                val infoData = info.data
                if (infoData is ChickCustomData) {
                    val serviceAd = infoData.serviceInfo
                    val url = MeiUtil.appendShareUrl(AmanLink.NetUrl.exclusive_service_details)
                    MeiWebActivityLauncher.startActivity(activity
                            , MeiWebData(MeiUtil.appendParamsUrl(url, "service_id" to "${serviceAd?.specialServiceId}")
                            , 0).apply {
                        need_reload_web = 1
                    })
                }


            }
        }
    }

}

/**
 * @param state 0:默认状态，什么都不打开
 *              1：打开键盘
 *              2：打开菜单
 *              3：打开快捷回复
 */
fun IMMessageListFragment.showInputState(state: Int = 0) {
    if (isAdded) {
//        if (state > 0) TransitionManager.beginDelayedTransition(input_bottom_layout, ChangeBounds().apply { duration = 200 })
        mentor_show_keyboard.isVisible = false
        mentor_quick_msg_tv.isVisible = true
        when (state) {
            1 -> {
                im_input_edit.requestFocus()
                KeyboardUtil.showKeyboard(im_input_edit)
                im_more_menu_layout.updateLayoutParams { height = 0 }
                im_quick_msg_layout.updateLayoutParams { height = 0 }
                im_message_recycler.scrollToPosition(messageAdapter.itemCount - 1)
            }
            2 -> {
                im_input_edit.clearFocus()
                KeyboardUtil.hideKeyboard(im_input_edit)
                im_more_menu_layout.updateLayoutParams { height = ViewGroup.LayoutParams.WRAP_CONTENT }
                im_quick_msg_layout.updateLayoutParams { height = 0 }
                im_message_recycler.scrollToPosition(messageAdapter.itemCount - 1)
            }
            3 -> {
                im_input_edit.clearFocus()
                KeyboardUtil.hideKeyboard(im_input_edit)
                im_more_menu_layout.updateLayoutParams { height = 0 }
                im_quick_msg_layout.updateLayoutParams { height = dip(240) }
                im_message_recycler.scrollToPosition(messageAdapter.itemCount - 1)
                mentor_show_keyboard.isVisible = true
                mentor_quick_msg_tv.isVisible = false
            }
            else -> {
                im_input_edit.clearFocus()
                KeyboardUtil.hideKeyboard(im_input_edit)
                im_more_menu_layout.updateLayoutParams { height = 0 }
                im_quick_msg_layout.updateLayoutParams { height = 0 }
            }
        }
    }
}

/** 检查时间戳与兼容老版本的撤回消息 **/
fun IMMessageListFragment.checkHasTime() {
    messageList.forEachIndexed { index, message ->
        val preMsg = messageList.getOrNull(index - 1)
        message.checkHasTime(preMsg?.timMessage)
    }
}

/**
 * 缓存相关信息
 */
fun IMMessageListFragment.preloadCacheInfo() {
    val ids = messageList.map { it.sender.getInt() }.filter { it > 0 }.toSet()

    /** 提前缓存用户的相关信息，缓存的信息会在消息列表中使用[IMBaseMessageHolder.refreshBase] **/
    val list = getCacheUserInfos(ids.toTypedArray())
    if (list.size != ids.size) {
        apiSpiceMgr.requestUserInfo(ids.toTypedArray(), back = {
            messageAdapter.notifyDataSetChanged()
        })
    }
}

fun IMMessageListFragment.refreshChatInputConfig() {
    im_input_edit.hint = chatConfig?.specialServiceTips.orEmpty()
}

fun IMMessageListFragment.refreshCouponHint() {
    im_coupon_hint.isVisible = chatConfig?.couponTips != null
            && chatConfig?.targetUserId != 116 // 知心小助手
            && activity !is VideoLiveRoomActivity
    chatConfig?.couponTips?.apply {
        coupon_hint_title.text = discountText
        coupon_hint_count_text.text = btnText
    }
}

/**
 * 发送消息封装
 */
fun IMMessageListFragment.sendMessage(msg: String, isPicture: Boolean = false, finish: (success: Boolean) -> Unit = {}) {
    showLoading(true)
    checkAuth(imChatId, success = {
        mIsStatistics = false
        showLoading(false)
        if (isPicture) {
            chatProvider.sendPicture(msg, true)
        } else {
            chatProvider.sendText(msg)
        }
        val isMentor = MeiUser.getSharedUser()?.info?.isPublisher == true
        val firstSendKey = "is_first_send_key"
        if (firstSendKey.getNonValue(true) && !isMentor && chatConfig?.specialServiceTips.orEmpty().isEmpty()) {
            val customMsg = CustomMessage(CustomInfo(CustomType.system_notify.name, ChickCustomData().apply {
                val tips = chatConfig?.tips.orEmpty()
                setText(if (tips.isEmpty()) "私聊1心币/每条" else tips)
            }))
            saveLocalMessage(arrayOf(customMsg))
            firstSendKey.putValue(false)
        }
        chatConfig?.specialServiceTips = it?.specialServiceTips.orEmpty()
        refreshChatInputConfig()
        statSendMessageEvent()
        finish(true)
    }, failure = { code, errorMsg ->
        showLoading(false)
        when (code) {
            -1 -> {
                UIToast.toast(activity, "网络出错,请重试$errorMsg")
                finish(false)
            }
            -2 -> {
                UIToast.toast(activity, "$errorMsg")
                finish(false)
            }
            571 -> {
                //拉黑提示
                finish(false)
            }
            else -> {
                /** 插入一条付钱失败的本地消息 **/
                val sendMsg = if (isPicture) ImageMessage(msg) else TextMessage(msg)
                sendMsg.putCustomValue(IM_CUSTOM_IS_PAY_FAIL, true)
                val customMsg = if (code == 700) CustomMessage(CustomInfo(CustomType.system_notify.name, ChickCustomData().apply {
                    val tips = if (errorMsg.isEmpty()) "私聊1心币/每条，余额不足，" else errorMsg
                    content = arrayListOf(SplitText(tips), SplitText("立即充值", "#3481FF", "${AmanLink.URL.show_pay_dialog}?from=im"))
                })) else null
                saveLocalMessage(arrayOf(sendMsg, customMsg).filterNotNull().toTypedArray())
                finish(true)
            }
        }
    })
}

/**
 * 本地插入一条消息
 */
fun IMMessageListFragment.saveLocalMessage(msgs: Array<Message>) {
    msgs.forEach { chatProvider.conversation.saveMessage(it.timMessage, JohnUser.getSharedUser().userIDAsString, true) }
    newMessages(msgs.map { it.timMessage })
}

/**
 * 进行发消息前的扣费检查
 */
fun checkAuth(userId: String, success: (chatAuth: ChatAuth?) -> Unit, failure: (code: Int, errorMsg: String) -> Unit) {
    spiceHolder().apiSpiceMgr.executeToastKt(ChatAuthRequest(userId), success = {
        if (it.isSuccess) success(it.data)
        else failure(it.rtn, it.errMsg)
    }, failure = {
        failure(-1, it?.currMessage.orEmpty())
    })
}

fun IMMessageListFragment.sendCustomMsg(data: ChickCustomData.Result?) {
    data?.let {
        chatProvider.sendCustomMessage(it)
    }
}

fun IMMessageListFragment.shieldedDelete(userID: String) {
    apiSpiceMgr.executeKt(ShieldingDeleteRequest(userID, ""), success = { it ->
        postAction(CANCEL_SHIELD_USER, arrayListOf(userID.getInt(0)))
        UIToast.toast(it.msg)
    })
}

/**领取课程*/
fun MeiCustomActivity.toReceiveCourse(userId: String, publisherId: String, referType: String, referId: String) {
    showLoading(true)
    apiSpiceMgr.executeKt(ToReceiveCourseRequest(userId.getInt(0), publisherId.getInt(0), referType, referId), success = {
        UIToast.toast(it.errMsg)
        if (it.isSuccess) {
            if (this@toReceiveCourse is VideoLiveRoomActivity) {
                Nav.toAmanLink(this, it.data?.detailHalfLink.orEmpty())
            } else {
                Nav.toAmanLink(this, it.data?.detailLink.orEmpty())
            }
        }

    }, failure = {
        UIToast.toast(it?.currMessage)
    }, finish = {
        showLoading(false)
    })
}

/**私聊页浏览统计*/
fun statSendMessageEvent() = try {
    GrowingUtil.track("common_page_click", "page_name", "私聊页",
            "page_type", "",
            "click_content", "",
            "click_type", "发送",
            "time_stamp", "${System.currentTimeMillis() / 1000}")
} catch (e: Exception) {
    e.printStackTrace()
}