package com.mei.chat.ui.adapter.item.send

import android.view.View
import androidx.core.view.isVisible
import com.joker.im.Message
import com.joker.im.message.ImageMessage
import com.joker.im.newMessages
import com.mei.chat.ui.adapter.item.IMBaseMessageHolder
import com.mei.im.ui.fragment.checkAuth
import com.mei.widget.holder.quickAdapter
import com.mei.wood.R
import com.tencent.imsdk.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 * 发送的消息基类
 */

/** 支付失败，无法进行消息发送处理 **/
const val IM_CUSTOM_IS_PAY_FAIL = "im_custom_is_pay_fail"

abstract class IMSendBaseMessageHolder(itemView: View) : IMBaseMessageHolder(itemView) {

    override fun refreshBase(msg: Message) {
        super.refreshBase(msg)
        val sendingView = itemView.findViewById<View>(R.id.im_message_sending)
        val retryView = itemView.findViewById<View>(R.id.im_message_retry)
        sendingView?.isVisible = msg.state == TIMMessageStatus.Sending
        retryView?.apply {
            isClickable = true
            val isPayFail = msg.getCustomValue(IM_CUSTOM_IS_PAY_FAIL, false)
            isVisible = msg.state == TIMMessageStatus.SendFail || isPayFail
            setOnClickListener {
                sendingView.isVisible = true
                retryView.isVisible = false
                isClickable = false
                if (isPayFail) {
                    checkAuth(msg.peer, success = {
                        msg.putCustomValue(IM_CUSTOM_IS_PAY_FAIL, false)
                        val newMsg = if (msg is ImageMessage) {
                            /**使用copyFrom 时 ImageMessage 的path出现空，导致接收端显示不出图片*/
                            ImageMessage(if (msg.timMessage.elementCount > 0) {
                                (msg.timMessage.getElement(0) as? TIMImageElem)?.path.orEmpty()
                            } else {
                                ""
                            }, true).timMessage
                        } else {
                            TIMMessage().apply {
                                copyFrom(msg.timMessage)
                            }
                        }

                        sendMsgInBackground(msg.timMessage.conversation, newMsg) {
                            newMessages(arrayListOf(newMsg))
                            isClickable = true
                            msg.removeMessage()
                            quickAdapter?.notifyDataSetChanged()
                        }
                    }, failure = { _, _ ->
                        isClickable = true
                        quickAdapter?.notifyItemChanged(bindingAdapterPosition)
                    })
                } else sendMsgInBackground(msg.timMessage.conversation, msg.timMessage) {
                    isClickable = true
                }
            }
        }

    }

    /**
     * 发送消息
     */
    private fun sendMsgInBackground(conversation: TIMConversation, msg: TIMMessage, finish: () -> Unit) {
        conversation.sendMessage(msg, object : TIMValueCallBack<TIMMessage> {
            override fun onSuccess(p0: TIMMessage?) {
                quickAdapter?.notifyItemChanged(bindingAdapterPosition)
                finish()
            }

            override fun onError(p0: Int, p1: String?) {
                quickAdapter?.notifyItemChanged(bindingAdapterPosition)
                finish()
            }

        })
    }

}