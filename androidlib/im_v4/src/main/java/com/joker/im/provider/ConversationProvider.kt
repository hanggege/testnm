package com.joker.im.provider

import com.joker.im.bindEventLifecycle
import com.joker.im.imLoginId
import com.joker.im.listener.IMAllEventManager
import com.tencent.imsdk.*
import com.tencent.imsdk.ext.message.TIMMessageLocator

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-13
 */

interface ConversationView {
    fun initListView(conversations: List<TIMConversation>)
    fun updateMessage(msgs: List<TIMMessage>) {}
    fun removeConversation(peer: String) {}
}

open class ConversationProvider(val converView: ConversationView? = null) : IMAllEventManager() {

    init {
        bindEventLifecycle(converView)
    }

    override fun onMessageRevoked(locator: TIMMessageLocator?) {
        super.onMessageRevoked(locator)
        if (locator != null) {
            loadConversation()
        }
    }

    override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
        converView?.updateMessage(msgs.orEmpty())
        return super.onNewMessages(msgs)
    }

    override fun onRefresh() {
        loadConversation()
    }

    @Suppress("DEPRECATION")
    override fun onGroupTipsEvent(elem: TIMGroupTipsElem?) {
        when (elem?.tipsType) {
            TIMGroupTipsType.Quit, TIMGroupTipsType.Kick, TIMGroupTipsType.DelGroup -> converView?.removeConversation(
                    elem.groupId
            )
            else -> {
            }
        }
    }

    override fun onForceOffline() {
        super.onForceOffline()
        converView?.initListView(emptyList())
    }

    /****************************公共使用的方法************************************/

    fun loadConversation() {
        /** 过滤自己和自己的聊天 **/
        converView?.initListView(TIMManager.getInstance().conversationList.filter { it.peer != imLoginId() })
    }


}

/**
 *创建对话
 */
fun timConversation(id: String, type: TIMConversationType = TIMConversationType.C2C) =
        TIMManager.getInstance().getConversation(type, id)