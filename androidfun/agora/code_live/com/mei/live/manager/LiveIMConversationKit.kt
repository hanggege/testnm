package com.mei.live.manager

import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.message.CustomMessage
import com.joker.im.newMessages
import com.tencent.imsdk.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-11
 */

private val conversationList = arrayListOf<TIMConversation>()

fun liveConversation(id: String, type: TIMConversationType = TIMConversationType.Group): TIMConversation {
    var conversation = conversationList.firstOrNull { it.peer == id }
    return if (conversation == null) {
        conversation = TIMManager.getInstance().getConversation(type, id)
        conversationList.add(conversation)
        conversation
    } else conversation
}

/** 发送消息 **/
fun liveSendCustomMsg2(
        id: String,
        type: CustomType,
        chatType: TIMConversationType = TIMConversationType.Group,
        applyData: ChickCustomData.() -> Unit = {},
        data: ChickCustomData = ChickCustomData(),
        back: (code: Int) -> Unit = { _ -> }
) {
    val msg = CustomMessage(ChickCustomData.Result(type, data.apply(applyData)))
    liveConversation(id, chatType).sendMessage(msg.timMessage, object : TIMValueCallBack<TIMMessage> {
        override fun onSuccess(p0: TIMMessage?) {
            back(0)
            p0?.let { newMessages(arrayListOf(it)) }
        }

        override fun onError(p0: Int, p1: String?) {
            back(p0)
        }

    })
}

/** 发送消息 **/
fun liveSendCustomMsg(
        id: String,
        type: CustomType,
        conversationType: TIMConversationType = TIMConversationType.Group,
        applyData: ChickCustomData.() -> Unit = {},
        back: (code: Int) -> Unit = { _ -> }
) = liveSendCustomMsg2(id, type, conversationType, applyData, back = back)

/**
 * 禁言用户
 */
fun forbidSendMsg(groupId: String, userId: String, time: Long = 10000, back: (code: Int, msg: String) -> Unit = { _, _ -> }) {
    TIMGroupManager.getInstance().modifyMemberInfo(TIMGroupManager.ModifyMemberInfoParam(groupId, userId).apply {
        silence = time
    }, object : TIMCallBack {
        override fun onSuccess() {
            back(0, "")
        }

        override fun onError(p0: Int, p1: String?) {
            back(p0, p1.orEmpty())
        }

    })
}