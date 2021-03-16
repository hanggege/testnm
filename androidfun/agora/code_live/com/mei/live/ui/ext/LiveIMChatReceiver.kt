package com.mei.live.ui.ext

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.imLoginId
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToConversation
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.live.ui.fragment.LiveIMSplitFragment
import com.mei.live.ui.fragment.checkUserStatus
import com.mei.orc.ext.runAsyncCallBack
import com.mei.orc.john.model.JohnUser
import com.net.model.chick.tab.tabbarConfig
import com.net.model.room.RoomType
import com.tencent.imsdk.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/1
 */
/** 直播间目前支持的消息列表 **/
private val supportMsgType: List<CustomType> = arrayListOf(CustomType.send_text, CustomType.delete_message,
        CustomType.join_group, CustomType.user_level_change, CustomType.special_service_card, CustomType.course_card,CustomType.coupon,CustomType.general_card)

class LiveIMChatReceiver(val imFragment: LiveIMSplitFragment,
                         val loginSuccess: LiveIMChatReceiver.() -> Unit,
                         val showMessage: (msgs: List<CustomMessage>, isNewMsg: Boolean) -> Unit)
    : IMAllEventManager() {

    var layoutManager: LinearLayoutManager?
        get() = (imRecyclerView?.layoutManager as? LinearLayoutManager)
        set(_) {}

    var imRecyclerView: RecyclerView? = null
        set(value) {
            field = value
            value?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    /** 专属连麦用户禁止查看历史消息 **/
                    val userForbidLoad = imFragment.checkUserStatus() == 2 && imFragment.roomInfo.roomType == RoomType.EXCLUSIVE
                    if ((layoutManager?.findFirstVisibleItemPosition() ?: 0) <= 4
                            && !userForbidLoad
                            && imFragment.msgList.size > 0) {
                        loadMessage()
                    }
                }
            })

        }
    var identify: String = ""
        set(value) {
            if (value.isNotEmpty()) {
                field = value
                initChatProvider()
                imFragment.msgList.clear()
                loadMessage()
            }
        }
    var isLoadingMessageList = false
    var conversation: TIMConversation
        get() = TIMManager.getInstance().getConversation(TIMConversationType.Group, identify)
        set(_) {}

    init {
        bindEventLifecycle(imFragment)
    }


    var timMsgList = arrayListOf<TIMMessage>()
    var retryError = 0
    var hasMore = true

    fun initChatProvider() {
        timMsgList.clear()
        hasMore = true
        isLoadingMessageList = false
        loadMessage()
    }


    /**
     * 加载消息
     * @param lastMsg 最后加载出来的消息
     */
    fun loadMessage() {
        if (!isLoadingMessageList && identify.isNotEmpty() && hasMore) {
            isLoadingMessageList = true
            conversation.getMessage(100, timMsgList.lastOrNull(), object : TIMValueCallBack<List<TIMMessage>?> {
                override fun onSuccess(msgs: List<TIMMessage>?) {
                    timMsgList.addAll(msgs.orEmpty())
                    retryError = 0
                    conversation.mapToConversation().readAllMessage()
                    isLoadingMessageList = false

                    hasMore = msgs.orEmpty().isNotEmpty() && msgs.orEmpty().last().checkTimeOut()
                    runAsyncCallBack(action = {
                        msgs.filterMsgList().filter { it.timMessage.checkTimeOut() }
                    }, uiBack = {
                        showMessage(it.orEmpty(), false)
                    })
                }

                override fun onError(code: Int, msg: String?) {
                    isLoadingMessageList = false
                    showMessage(emptyList(), false)
                    if (code == 10007) {
                        retryError++
                        if (retryError < 3) loadMessage()
                    }
                }

            })
        }
    }

    /**
     * 检查消息发送时间是否在指定时间内
     */
    private fun TIMMessage.checkTimeOut(): Boolean {
        //过滤指定时间内的消息
        var lastShowTime = tabbarConfig.timMsgPullSec
        if (lastShowTime <= 0) lastShowTime = System.currentTimeMillis() / 1000
        return System.currentTimeMillis() / 1000 - this.timestamp() < lastShowTime
    }

    override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
        timMsgList.addAll(0, msgs.orEmpty())
        /** 筛选当前聊天的最新消息 **/
        val chatList = msgs.filterMsgList()
        if (chatList.isNotEmpty()) {
            showMessage(chatList, true)
        }
        msgs.orEmpty().forEach { conversation.mapToConversation().readMessages(it) }
        return super.onNewMessages(msgs)
    }

    override fun onLoginSuccess() {
        super.onLoginSuccess()
        this.loginSuccess()
    }

    private fun List<TIMMessage>?.filterMsgList() = orEmpty().asSequence()
            .filter { it.conversation?.type == TIMConversationType.Group }
            .filter { it.conversation?.peer == identify || it.sender == imLoginId() }
            .filter { it.status() != TIMMessageStatus.HasDeleted && it.status() != TIMMessageStatus.HasRevoked }
            .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
            .filter { supportMsgType.any { type -> type == it.customMsgType } }
            .filter { (it.customInfo?.data as? ChickCustomData)?.filterVisibleMsg() ?: true }
            .toList()//过滤未支持的消息

    /**
     * 过滤可见性消息
     */
    private fun ChickCustomData.filterVisibleMsg(): Boolean {
        return if (whitelist.isNotEmpty()) {
            whitelist.contains(JohnUser.getSharedUser().userID)
        } else {
            !blacklist.contains(JohnUser.getSharedUser().userID)
        }
    }
}