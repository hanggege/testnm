package com.joker.im.listener

import com.tencent.imsdk.*
import com.tencent.imsdk.ext.message.TIMMessageLocator
import com.tencent.imsdk.ext.message.TIMMessageReceipt
import com.tencent.imsdk.ext.message.TIMMessageReceiptListener
import com.tencent.imsdk.ext.message.TIMMessageRevokedListener
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-13
 * IM的所有事件总监听,这里做消息分发到指定业务
 */


open class IMAllEventManager :
/** 以下为[TIMManager] 的回调**/
        TIMMessageListener,
        /** 以下为[TIMUserConfig] 的回调**/
        TIMMessageRevokedListener,
        TIMMessageReceiptListener,
        TIMUserStatusListener,
        TIMConnListener,
        TIMRefreshListener,
        TIMGroupEventListener {

    fun init() {
    }

    val viewSet = CopyOnWriteArraySet<IMAllEventManager>()

    @Synchronized
    private fun looperView(action: (IMAllEventManager) -> Unit) {
        viewSet.forEach { action(it) }
    }

    @Synchronized
    fun addEventListener(listener: IMAllEventManager) {
        viewSet.add(listener)
    }

    @Synchronized
    fun removeEventListener(listener: IMAllEventManager) {
        viewSet.remove(listener)
    }


    /****************************以下为事件回调，上面的业务层是不需要改动************************************/

    /****************************消息撤回[TIMMessageRevokedListener]************************************/

    /**
     * 设置消息撤回通知监听器
     * @param locator 消息撤回通知监听器
     * @since 3.1.0
     */
    override fun onMessageRevoked(locator: TIMMessageLocator?) {
        looperView { it.onMessageRevoked(locator) }
    }

    /**
     * 消息已读回执监听器
     */
    override fun onRecvReceipt(receipts: MutableList<TIMMessageReceipt>?) {
        looperView { it.onRecvReceipt(receipts) }
    }

    /****************************[TIMMessageListener]************************************/
    /**
     * 收到新消息回调
     * @param msgs 收到的新消息
     * @return 正常情况下，如果注册了多个listener, IM SDK会顺序回调到所有的listener。当碰到listener的回调返回true的时候，将终止继续回调后续的listener。
     */
    override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
        looperView { it.onNewMessages(msgs) }
        return false
    }

    /**
     * 刷新指定消息
     */
    open fun onRefreshMessage(msg: TIMMessage?) {
        looperView { it.onRefreshMessage(msg) }
    }

    /****************************用户状态变更事件监听器[TIMUserStatusListener]************************************/
    /**
     * 用户票据过期
     */
    override fun onUserSigExpired() {
        looperView { it.onUserSigExpired() }
    }

    /**
     * 被踢下线时回调
     */
    override fun onForceOffline() {
        looperView { it.onForceOffline() }
    }

    /****************************连接状态事件监听器[TIMConnListener]************************************/
    /**
     * 连接建立
     */
    override fun onConnected() {
        looperView { it.onConnected() }
    }

    /**
     * WIFI需要验证
     * @param name wifi名称
     */
    override fun onWifiNeedAuth(name: String?) {
        looperView { it.onWifiNeedAuth(name) }
    }

    /**
     * 连接断开
     *
     * @param code 错误码
     * @param desc 错误描述
     */
    override fun onDisconnected(code: Int, msg: String?) {
        looperView { it.onDisconnected(code, msg) }
    }


    /****************************会话刷新监听器 [TIMRefreshListener]************************************/
    /**
     * 部分会话刷新（包括多终端已读上报同步）
     * @param conversations 需要刷新的会话列表
     */
    override fun onRefreshConversation(conversations: MutableList<TIMConversation>?) {
        looperView { it.onRefreshConversation(conversations) }
    }

    /**
     * 数据刷新通知回调（如未读计数，会话列表等）
     */
    override fun onRefresh() {
        looperView { it.onRefresh() }
    }

    /****************************群组事件监听器[TIMGroupEventListener]************************************/
    override fun onGroupTipsEvent(elem: TIMGroupTipsElem?) {
        looperView { it.onGroupTipsEvent(elem) }
    }


    /****************************[IMLoginProvider]************************************/
    open fun onLoginSuccess() {
        looperView { it.onLoginSuccess() }
    }
}