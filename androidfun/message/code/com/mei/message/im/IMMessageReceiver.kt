package com.mei.message.im

import com.joker.im.imLoginId
import com.joker.im.provider.ChatMessageProvider
import com.joker.im.provider.ChatMessageView
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMMessage

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/2/28
 */
class IMMessageReceiver(
        identify: String,
        type: TIMConversationType = TIMConversationType.C2C,
        chatView: ChatMessageView? = null)
    : ChatMessageProvider(identify, type, chatView) {

    override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
        /** 筛选当前聊天的最新消息 **/
        val chatList = msgs.orEmpty().filter {
            (it.conversation?.peer == identify || it.sender == imLoginId()) && (it.conversation?.type
                    ?: type) == type
        }
        if (chatList.isNotEmpty()) {
            showMessageList(chatList)
            chatView?.showMessage(chatList)
        }
        return false
    }

}