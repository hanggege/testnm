package com.mei.im.presenters

import com.joker.im.custom.CustomInfo
import com.joker.im.message.CustomMessage
import com.joker.im.provider.ChatMessageProvider
import com.joker.im.provider.ChatMessageView
import com.tencent.imsdk.TIMConversationType

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-18
 */

class IMChatMessageBusiness(
        identify: String,
        type: TIMConversationType = TIMConversationType.C2C,
        chatView: ChatMessageView? = null)
    : ChatMessageProvider(identify, type, chatView) {

    fun <T : CustomInfo<*>> sendCustomMessage(customInfo: T) {
        val msg = CustomMessage(customInfo)
        if (msg.isSupportMsg()) {
            sendMessage(msg)
        }
    }

}