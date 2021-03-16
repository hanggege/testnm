package com.joker.im.provider

import com.joker.im.*
import com.joker.im.listener.IMAllEventManager
import com.joker.im.message.*
import com.tencent.imsdk.*
import com.tencent.imsdk.ext.message.TIMMessageDraft
import com.tencent.imsdk.ext.message.TIMMessageLocator
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-14
 */
interface ChatMessageView {
    fun showMessage(msgs: List<TIMMessage>, isNewMsg: Boolean = true)

    /** 刷新消息，如果为空则全部刷新  [replace] 如果[msg]不为空是否进行消息替换 **/
    fun refreshMessage(msg: TIMMessage? = null, replace: Boolean = false) {}

    fun showError(code: Int, msg: String) {}
}


const val LOAD_MESSAGE_NUM = 30
const val FILE_MAX_LIMIT = 1024 * 1024 * 28

open class ChatMessageProvider(
        val identify: String,
        val type: TIMConversationType = TIMConversationType.C2C,
        val chatView: ChatMessageView? = null
) : IMAllEventManager() {
    val conversation = timConversation(identify, type)
    val appConversation = conversation.mapToConversation()

    var sendMsgAgo: (msg: Message) -> Unit = {}
    var isLoadingMessageList = false
    var lastMsg: TIMMessage? = null
    var hasMore = true


    /****************************公共使用的方法************************************/
    init {
        bindEventLifecycle(chatView)
        initChatProvider()
    }

    fun initChatProvider() {
        lastMsg = null
        hasMore = true
        loadMessage()
    }

    /**
     * 加载消息
     */
    fun loadMessage() {
        /**腾讯IM 4.8版本会出现使用lastMsg去拉取历史消息时会出现重复的消息，当我们再使用lastMsg去获取时无限的循环*/
        if (!isLoadingMessageList && hasMore) {
            isLoadingMessageList = true
            conversation.getMessage(LOAD_MESSAGE_NUM, lastMsg, object : TIMValueCallBack<List<TIMMessage>?> {
                override fun onSuccess(msgs: List<TIMMessage>?) {
                    if (msgs.orEmpty().isNotEmpty()) lastMsg = msgs?.lastOrNull()
                    hasMore = msgs.orEmpty().isNotEmpty()
                    appConversation.readAllMessage()
                    isLoadingMessageList = false
                    showMessageList(msgs.orEmpty())
                    chatView?.showMessage(msgs.orEmpty(), false)
                }

                override fun onError(code: Int, msg: String?) {
                    isLoadingMessageList = false
                    chatView?.showMessage(emptyList(), false)
                }

            })
        }
    }

    open fun showMessageList(msgs: List<TIMMessage>) {

    }

    fun revokeMessage(msg: TIMMessage) {
        conversation.revokeMessage(msg, object : TIMCallBack {
            override fun onSuccess() {
                chatView?.refreshMessage(msg)
            }

            override fun onError(code: Int, msg: String?) {
                chatView?.showError(code, "撤消失败：${msg.orEmpty()}")
            }

        })
    }

    /** 清除消息 **/
    fun clearAllMessage(success: (Boolean) -> Unit = {}) {
        conversation.deleteLocalMessage(object : TIMCallBack {
            override fun onSuccess() {
                success(true)
            }

            override fun onError(p0: Int, p1: String?) {
                success(false)
            }

        })
    }

    /** 草稿 **/
    fun saveDraft(draft: String) {
        conversation.draft = null
        if (draft.isNotEmpty()) {
            conversation.draft = TIMMessageDraft().apply {
                addElem(TIMTextElem().apply { text = draft })
            }
        }
    }

    fun getDraft(): String {
        return if (conversation.hasDraft()) {
            (conversation.draft.elems.firstOrNull() as? TIMTextElem)?.text ?: ""
        } else ""
    }

    /****************************发送消息************************************/
    fun sendText(msg: String, success: () -> Unit = {}, error: (code: Int, msg: String?) -> Unit = { _, _ -> }) {
        if (msg.isNotEmpty()) {
            sendMessage(TextMessage(msg), success, error)
        }
    }

    fun sendPicture(filePath: String, isOri: Boolean = true, success: () -> Unit = {}, error: (code: Int, msg: String?) -> Unit = { _, _ -> }) {
        val file = File(filePath)
        if (file.exists() && file.length() > 0) {
            if (file.length() > FILE_MAX_LIMIT) chatView?.showError(-1, "图片过大，发送失败")
            else sendMessage(ImageMessage(filePath, isOri), success, error)
        }
    }

    fun sendVoice(voicePath: String, duration: Long, success: () -> Unit = {}, error: (code: Int, msg: String?) -> Unit = { _, _ -> }) {
        val file = File(voicePath)
        if (file.exists() && file.length() > 0) {
            sendMessage(VoiceMessage(voicePath, duration), success, error)
        }
    }

    fun sendVideo(videoPath: String, duration: Long, success: () -> Unit = {}, error: (code: Int, msg: String?) -> Unit = { _, _ -> }) {
        val file = File(videoPath)
        if (file.exists() && file.length() > 0) {
            sendMessage(VideoMessage(videoPath, duration), success, error)
        }
    }

    fun sendFile(filePath: String, success: () -> Unit = {}, error: (code: Int, msg: String?) -> Unit = { _, _ -> }) {
        val file = File(filePath)
        if (file.exists() && file.length() > 0) {
            sendMessage(FileMessage(filePath), success, error)
        }
    }


    /** 发送消息 **/
    fun sendMessage(message: Message, success: () -> Unit = {}, error: (code: Int, msg: String?) -> Unit = { _, _ -> }) {
        //通知其他地方 ,触发回调 有新消息了，虽然是自己发送的
        newMessages(arrayListOf(message.timMessage))
//        chatView?.showMessage(arrayListOf(message.timMessage))
        sendMsgAgo(message)

        conversation.sendMessage(message.timMessage, object : TIMValueCallBack<TIMMessage> {
            override fun onSuccess(msg: TIMMessage?) {
                success()
                onRefreshMessage(message.timMessage)
            }

            override fun onError(code: Int, msg: String?) {
                error(code, msg)
                onRefreshMessage(message.timMessage)
            }
        })
    }

    /****************************[IMAllEventManager]************************************/
    override fun onMessageRevoked(locator: TIMMessageLocator?) {
        super.onMessageRevoked(locator)
        locator?.let {
            conversation.findMessages(arrayListOf(locator), object : TIMValueCallBack<List<TIMMessage>> {
                override fun onSuccess(msgs: List<TIMMessage>?) {
                    chatView?.refreshMessage(msgs.orEmpty().firstOrNull(), true)
                }

                override fun onError(p0: Int, p1: String?) {
                }

            })
        }
    }

    override fun onRefreshMessage(msg: TIMMessage?) {
        super.onRefreshMessage(msg)
        chatView?.refreshMessage(msg)
    }

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

        appConversation.readAllMessage()
        return super.onNewMessages(msgs)
    }


}