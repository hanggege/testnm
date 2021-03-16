package com.joker.im.ext

import com.joker.im.listener.IMAllEventManager
import com.tencent.imsdk.TIMConversation
import com.tencent.imsdk.TIMManager
import com.tencent.imsdk.TIMMessage
import com.tencent.imsdk.TIMUserConfig

/**
 *  Created by zzw on 2019-07-11
 *  Des:
 */


internal class IMAllEventManagerExt {

    fun registered(listener: IMAllEventManager) {
        imAllEvent.addEventListener(listener)
    }

    fun unregistered(listener: IMAllEventManager) {
        imAllEvent.removeEventListener(listener)
    }

    fun onRefresh() {
        imAllEvent.onRefresh()
    }

    fun onRefreshConversation(conversations: List<TIMConversation>) {
        imAllEvent.onRefreshConversation(conversations.toMutableList())
    }

    fun onNewMessages(messages: List<TIMMessage>) {
        if (messages.isNotEmpty()) {
            imAllEvent.onNewMessages(messages.toMutableList())
        }
    }

    fun onRefreshMessage(messages: List<TIMMessage>) {
        if (messages.isNotEmpty()) messages.forEach { imAllEvent.onRefreshMessage(it) }
    }
}

internal val imAllEvent: IMAllEventManager by lazy {
    IMAllEventManager().apply {
        val userConfig = TIMUserConfig()
        userConfig.userStatusListener = this
        userConfig.connectionListener = this
        userConfig.refreshListener = this
        userConfig.groupEventListener = this
        userConfig.messageRevokedListener = this
        userConfig.messageReceiptListener = this
        userConfig.disableAutoReport(false)
        //开启消息已读回执 启用已读回执，启用后在已读上报时会发送回执给对方，只对单聊会话有效
        // 在 与iOS的兼容有问题，
//        userConfig.enableReadReceipt(true)
        TIMManager.getInstance().userConfig = userConfig
        TIMManager.getInstance().addMessageListener(this)
    }
}