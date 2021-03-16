package com.joker.im

import android.util.Log
import com.tencent.imsdk.*
import java.net.URLDecoder

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-13
 */

fun TIMConversation.mapToConversation() = Conversation(this)

open class Conversation(val timConversation: TIMConversation) : Comparable<Conversation> {
    val peer: String = timConversation.peer
    val type: TIMConversationType = timConversation.type

    fun getLastMessageTime(): Long {
        return timConversation.lastMsg?.timestamp() ?: 0
    }

    fun readAllMessage(success: () -> Unit = {}, failure: ((Int, String) -> Unit) = { _, _ -> }) {
        timConversation.setReadMessage(null, object : TIMCallBack {
            override fun onSuccess() {
                success()
            }

            override fun onError(code: Int, msg: String?) {
                failure(code, msg.orEmpty())
                Log.e("Conversation", "readAllMessage $peer: 失败$code $msg ");
            }

        })
    }

    fun readMessages(msg: TIMMessage, success: () -> Unit = {}, failure: ((Int, String) -> Unit) = { _, _ -> }) {
        timConversation.setReadMessage(msg, object : TIMCallBack {
            override fun onSuccess() {
                success()
            }

            override fun onError(code: Int, msg: String?) {
                failure(code, msg.orEmpty())
                Log.e("Conversation", "readMessages $peer: 设置已读出错 $code: $msg ");
            }

        })
    }

    /**
     * 获取最后一条消息摘要
     */
    fun getLastMessageSummary(): String {
        val msg = timConversation.lastMsg
        return when {
            timConversation.hasDraft() -> {
                "[草稿]${(timConversation.draft.elems.firstOrNull() as? TIMTextElem)?.text ?: ""}"
            }
            msg?.mapToMeiMessage()?.isRecalled() == true -> {
                "撤回了一条消息"
            }
            else -> {
                getDecodeSummary(msg?.mapToMeiMessage()?.getSummary().orEmpty())
            }
        }
    }


    override fun hashCode(): Int = peer.hashCode() * 31 + type.hashCode()


    override fun equals(other: Any?): Boolean {
        return if (other is Conversation) other.peer == this.peer && other.timConversation.type == this.timConversation.type
        else false
    }

    override fun compareTo(other: Conversation): Int {
        val timeGap = other.getLastMessageTime() - getLastMessageTime()
        return when {
            timeGap > 0 -> 1
            timeGap < 0 -> -1
            else -> 0
        }
    }

    private fun getDecodeSummary(content: String): String {
        return try {
            URLDecoder.decode(content, "UTF-8")
        } catch (e: Exception) {
            e.printStackTrace()
            content
        }
    }

}
