package com.joker.im

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.joker.im.provider.imUserProfile
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMMessage
import com.tencent.imsdk.TIMMessageOfflinePushSettings
import com.tencent.imsdk.TIMMessageStatus
import kotlin.math.abs


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-13
 */

/** 音频已听 **/
const val MESSAGE_CUSTOM_IS_LISTENED = 1001

/** 文件下载状态 **/
const val MESSAGE_CUSTOM_FILE_LOADING = 1013
const val MESSAGE_CUSTOM_FILE_SUCCESS = 1014
const val MESSAGE_CUSTOM_FILE_FAIL = 1015

abstract class Message(val timMessage: TIMMessage) {

    init {
        if (timMessage.isSelf && timMessage.status() == TIMMessageStatus.Sending) {
            pushOffline("")
        }
    }

    /** 获取当前发送人ID **/
    var sender: String = timMessage.sender
        set(value) {
            field = value
            timMessage.sender = value
        }

    /** 判断是否是自己发的 **/
    val isSelf = timMessage.isSelf

    /** 获取状态 **/
    var state: TIMMessageStatus
        get() = timMessage.status()
        set(_) {}

    /** 对话类型 **/
    val chatType: TIMConversationType = timMessage.conversation?.type ?: TIMConversationType.C2C
    val peer: String = timMessage.conversation?.peer ?: ""

    /** 是否需要显示时间 **/
    var hasTime: Boolean = false

    /**
     * 是否需要显示时间设置  , 5 分钟后显示下一条时间
     * @param preMsg 上一条消息
     */
    fun checkHasTime(preMsg: TIMMessage?) {
        hasTime = if (preMsg != null) abs(preMsg.timestamp() - timMessage.timestamp()) > 300 else true
    }

    /** 是否是撤回消息 **/
    fun isRecalled(): Boolean = state == TIMMessageStatus.HasRevoked

    /** 是否是已删除的消息 **/
    fun isDeleted() = state == TIMMessageStatus.HasDeleted

    /**
     * 删除消息
     */
    fun removeMessage() = timMessage.remove()

    /**
     * 获取所有自定义数据
     */
    fun getCustomValue(): HashMap<String, Any> {
        return try {
            Gson().fromJson(timMessage.customStr.orEmpty(), object : TypeToken<HashMap<String, Any>>() {}.type)
        } catch (e: Exception) {
            HashMap()
        }
    }

    /**
     * 通过key获取指定数据
     */
    inline fun <reified T : Any> getCustomValue(key: String, defaultValue: T): T {
        val value = getCustomValue()[key]
        return if (value is T) value else defaultValue
    }

    /**
     * 添加新的自定义数据
     */
    fun putCustomValue(key: String, value: Any) {
        getCustomValue().apply {
            put(key, value)
            timMessage.customStr = Gson().toJson(this).orEmpty()
        }
    }

    /**
     * 删除指定key的自定义数据
     */
    fun removeCustomValue(key: String) {
        getCustomValue().apply {
            remove(key)
            timMessage.customStr = Gson().toJson(this).orEmpty()
        }
    }

    /**
     * 离线推送消息
     */
    fun pushOffline(content: String) {
        var nickName = imUserProfile?.getNickName(timMessage.sender).orEmpty()
        if (nickName.isEmpty()) nickName = timMessage.senderNickname.orEmpty()
        val settings = TIMMessageOfflinePushSettings().apply {
            isEnabled = true
            title = nickName
            descr = getSummary()
            androidSettings = TIMMessageOfflinePushSettings.AndroidSettings().apply {
                oppoChannelID = "mei_goat_oppo"
            }
            iosSettings = TIMMessageOfflinePushSettings.IOSSettings().apply {
                title = nickName
                descr = getSummary()
                isBadgeEnabled = true
            }
            ext = content.toByteArray()
        }
        timMessage.offlinePushSettings = settings
    }

    /**
     * 获取消息摘要
     */
    abstract fun getSummary(): String

    /**
     * 可以copy的摘要
     */
    abstract fun getCopySummary(): String
}