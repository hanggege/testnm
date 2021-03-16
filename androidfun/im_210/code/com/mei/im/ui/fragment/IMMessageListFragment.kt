package com.mei.im.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.joker.im.Message
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.mapToMeiMessage
import com.joker.im.provider.ChatMessageView
import com.mei.base.common.DELETE_BLACKLIST
import com.mei.chat.ui.adapter.IMMessageListAdapter
import com.mei.im.presenters.IMChatMessageBusiness
import com.mei.im.resetNotifyById
import com.mei.im.ui.dialog.IMChatFragmentDialog
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.handler.GlobalUIHandler
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.ui.TabMainActivity
import com.mei.wood.util.AppManager
import com.mei.wood.util.logDebug
import com.mei.wood.web.MeiWebActivity
import com.net.model.chat.ChatConfig
import com.net.network.chat.ChatConfigRequest
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.im_message_list_fragment.*
import launcher.Boom

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/16
 */
class IMMessageListFragment : CustomSupportFragment(), ChatMessageView {

    @Boom
    var imChatId: String = ""

    @Boom
    var extraData: String = ""

    //是否统计进入聊天页面没有发送过内容
    var mIsStatistics: Boolean = true

    val chatProvider by lazy { IMChatMessageBusiness(imChatId, TIMConversationType.C2C, this) }
    val messageList = arrayListOf<Message>()
    val messageAdapter by lazy {
        IMMessageListAdapter(messageList).apply {
            setOnItemChildLongClickListener { _, view, position ->
                adapterItemLongClick(view, position)
                true
            }
        }
    }


    val quickMsgFragment by lazy {
        IMQuickMessageFragment().apply {
            selectSendMsg = { msg ->
                setCommonWord(msg)
            }
        }
    }
    var chatConfig: ChatConfig? = null
        set(value) {
            field = value
            quickMsgFragment.refreshMsg(value?.chatPhrase.orEmpty())
        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ActivityLauncher.bind(this)
        return inflater.inflate(R.layout.im_message_list_fragment, container, false)
    }

    /**
     * 设置常用语
     */
    private fun setCommonWord(msg: String) {
        im_input_edit.setText(msg)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.commit(allowStateLoss = true) { replace(R.id.im_quick_msg_layout, quickMsgFragment) }
        initView()
        im_message_recycler.adapter = messageAdapter
        /**
         * 发送自定义消息
         */
        sendIntentMsg()
        chatProvider.loadMessage()

        //通知拉黑
        bindAction<String>(DELETE_BLACKLIST) {
            it?.let { it1 -> shieldedDelete(it1) }
        }
        refreshChatConfig()

    }

    private fun sendIntentMsg() {
        getCacheUserInfo(imChatId.getInt(0))?.let {
            if (!it.isBlackList) {
                extraData.json2Obj<ChickCustomData.Result>()?.apply {
                    if (data?.type?.name?.isNotEmpty() == true) {
                        sendCustomMsg(this)
                        GlobalUIHandler.schedule({
                            showInputState(1)
                        }, 300)
                    }
                    extraData = ""
                }
            }

        }

    }

    fun refreshChatConfig() {
        val preActivity = AppManager.getInstance().preActivity()
        val parentFragment = parentFragment?.parentFragment
        val prePager = when {
            parentFragment is DialogFragment -> {
                if (parentFragment is IMChatFragmentDialog) {
                    parentFragment.from
                } else {
                    parentFragment.javaClass.simpleName
                }
            }
            preActivity is TabMainActivity -> {
                preActivity.fragmentMap[preActivity.getSelectedIndex()]?.javaClass?.simpleName.orEmpty()
            }
            preActivity is MeiWebActivity -> {
                preActivity.webData.url
            }
            else -> {
                preActivity?.javaClass?.simpleName.orEmpty()
            }
        }
        logDebug("chatPrePager",prePager)
        apiSpiceMgr.executeKt(ChatConfigRequest(imChatId, prePager), success = {
            chatConfig = it.data
            refreshChatInputConfig()
            refreshCouponHint()

            initMenu(chatConfig?.moreMenu.orEmpty())
        },failure = {
            initMenu()
        })
    }

    /****************************[ChatMessageView]************************************/
    override fun showMessage(msgs: List<TIMMessage>, isNewMsg: Boolean) {
        if (isAdded) {
            val layoutManager = im_message_recycler.layoutManager as? LinearLayoutManager
            if (msgs.isNotEmpty() && layoutManager != null) {
                resetNotifyById(imChatId)
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                var newMsgNum = 0
                msgs.filterNot { msg -> messageList.any { it.timMessage.msgId == msg.msgId } }
                        .mapNotNull { it.mapToMeiMessage() }
                        .forEach {
                            newMsgNum++
                            if (isNewMsg) messageList.add(it)
                            else messageList.add(0, it)
                        }
                checkHasTime()
                preloadCacheInfo()
                messageAdapter.notifyDataSetChanged()
                if (newMsgNum > 0) {
                    if (messageList.size == newMsgNum) {
                        /** 初次加载 **/
                        layoutManager.scrollToPosition(newMsgNum - 1)
                    } else if (isNewMsg) {
                        /** 添加新消息 **/
                        if (messageList.lastOrNull()?.isSelf == true || messageList.size - firstVisiblePosition < 15) {
                            layoutManager.scrollToPosition(messageList.size - 1)
                        }
                    } else if (messageList.size > newMsgNum && newMsgNum > 0) {
                        /** 加载更多消息 , 40dp的偏移量是为了第一条消息有个时间戳，合并的时候会出现消息列表跳动的情况，这里是消除跳动 **/
                        layoutManager.scrollToPositionWithOffset(newMsgNum, dip(40))
                    }
                }
            }
        }
    }

    //滑动到最后一条
    fun bottomRecycler() {
        (im_message_recycler.layoutManager as? LinearLayoutManager)?.scrollToPosition(messageList.size - 1)
    }

    override fun refreshMessage(msg: TIMMessage?, replace: Boolean) {
        if (msg != null) {
            val index = messageList.indexOfLast { it.timMessage.msgId == msg.msgId }
            val newMsg = msg.mapToMeiMessage()
            if (index >= 0) {
                if (replace && newMsg != null) {
                    messageList.removeAt(index)
                    messageList.add(index, newMsg)
                }
                messageAdapter.notifyItemChanged(index)
            }
        } else messageAdapter.notifyDataSetChanged()
    }

    override fun showError(code: Int, msg: String) {
        UIToast.toast(activity, msg)
    }
}