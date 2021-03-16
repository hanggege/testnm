package com.mei.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.joker.im.provider.ChatMessageView
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.ui.nav.Nav
import com.mei.im.quickLoginIM
import com.mei.login.toLogin
import com.mei.message.adapter.NOTIFY_ITEM_DIVIDER
import com.mei.message.adapter.NOTIFY_ITEM_HISTORY_TAG
import com.mei.message.adapter.NotifyMessageAdapter
import com.mei.message.im.IMMessageReceiver
import com.mei.orc.event.bindAction
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.util.parseValue
import com.net.network.chick.friends.HomeStatusRequest
import com.net.network.report.MessagePushReport
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.empty_notify_layout.*
import kotlinx.android.synthetic.main.empty_to_login_layout.*
import kotlinx.android.synthetic.main.fragment_notify.*

/**
 * author : Song Jian
 * date   : 2020/2/13
 * desc   : 通知
 */

const val SYSTEM_NOTIFY_USER_ID = "99"

class NotifyFragment : BaseBannerFragment() {

    override val bannerAdapter: BaseQuickAdapter<Any, BaseViewHolder> by lazy { notifyAdapter }
    override val dataChange: () -> Unit by lazy {
        {
            to_login_layout.isGone = JohnUser.getSharedUser().hasLogin()
        }
    }

    val msgList: ArrayList<Any> = arrayListOf()
    val newMsgList: ArrayList<Any> = arrayListOf()
    private val notifyAdapter: NotifyMessageAdapter by lazy {
        NotifyMessageAdapter(msgList).apply {
            setOnItemClickListener { _, _, position ->
                ((msgList.getIndexOrNull(position) as? CustomMessage)?.customInfo?.data as? ChickCustomData)?.let { data ->
                    val action = data.action
                    if (data.report.isNotEmpty()) {
                        apiSpiceMgr.executeKt(MessagePushReport(data.report.parseValue("seq_id", "0")))
                    }
                    when {
                        action.matches(AmanLink.URL.jump_to_video_live.toRegex()) -> {
                            apiSpiceMgr.executeToastKt(HomeStatusRequest(data.userId), success = {
                                if (it.data?.isLiving == true) Nav.toAmanLink(activity, action)
                                else UIToast.toast(activity, "直播已结束")
                            })
                        }
                        action.isNotEmpty() -> Nav.toAmanLink(activity, action)
                        else -> {
                        }
                    }
                }
            }
            loadMoreModule.setOnLoadMoreListener {
                //加载更多
                if (JohnUser.getSharedUser().hasLogin()) {
                    quickLoginIM(success = {
                        messageReceiver.loadMessage()
                    })
                }
            }


        }
    }

    private val messageReceiver: IMMessageReceiver by lazy {
        IMMessageReceiver(SYSTEM_NOTIFY_USER_ID, chatView = object : ChatMessageView {
            override fun showMessage(msgs: List<TIMMessage>, isNewMsg: Boolean) {
                showLoading(false)
                if (isNewMsg && msgList.isNotEmpty() && !msgList.any { it == NOTIFY_ITEM_HISTORY_TAG }) {
                    msgList.add(0, NOTIFY_ITEM_HISTORY_TAG)
                }
                newMsgList.clear()
                msgs.mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { it.customMsgType == CustomType.notify }
                        .filter { !newMsgList.mapNotNull { it as? CustomMessage }.any { old -> old.timMessage.msgId == it.timMessage.msgId } }
                        .reversed()
                        .apply {
                            val isFirstNewMsg = msgList.firstOrNull() == NOTIFY_ITEM_HISTORY_TAG
                            forEachIndexed { index, message ->
                                if (index < size && !isFirstNewMsg) newMsgList.add(0, NOTIFY_ITEM_DIVIDER)
                                newMsgList.add(0, message)
                            }
                            /** 缓存用户信息 **/
                            apiSpiceMgr.requestUserInfo(this.mapNotNull { (it.customInfo?.data as? ChickCustomData)?.userId }.toSet().toTypedArray()) {
                                bannerAdapter.notifyDataSetChanged()
                            }
                            if (isNewMsg) {
                                msgList.addAll(0, newMsgList)
                            } else {
                                msgList.addAll(msgList.size, newMsgList)
                            }
                            if (isNotEmpty()) {
                                bannerAdapter.loadMoreModule.loadMoreComplete()
                            } else {
                                bannerAdapter.loadMoreModule.loadMoreEnd()
                            }
                            bannerAdapter.loadMoreModule.isEnableLoadMore = msgList.size > 5
                            bannerAdapter.notifyDataSetChanged()
                            if (isAdded) notification_empty_layout?.isVisible = msgList.isEmpty()
                        }
            }

        })
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notify_msg_recycler.adapter = notifyAdapter
//        buildHeader()
        refreshIMList()
        messageReceiver.bindEventLifecycle(this)
        goto_login_view.setOnClickListener { activity?.toLogin() }
        empty_hint_tv.text = "暂无通知"
        empty_icon_iv.setImageResource(R.drawable.icon_empty_notifiction)
        bindAction<Boolean>(CHANG_LOGIN) {
            refreshIMList()
        }
    }

    override fun willAppear() {
        super.willAppear()
        if (JohnUser.getSharedUser().hasLogin()) {
            apiSpiceMgr.requestUserInfo(msgList.mapNotNull { (it as? CustomMessage)?.sender?.toIntOrNull() }.toTypedArray()) {
                if (isAdded) notifyAdapter.notifyDataSetChanged()
            }
        } else {
            msgList.clear()
            if (isAdded) notifyAdapter.notifyDataSetChanged()
        }
    }

    override fun refreshIMList() {
        msgList.clear()
        showLoading(true)
        if (JohnUser.getSharedUser().hasLogin()) {
            runDelayedOnUiThread(500) {
                quickLoginIM(success = {
                    messageReceiver.loadMessage()
                })
            }

        }
        if (isAdded) notifyAdapter.notifyDataSetChanged()
    }
}