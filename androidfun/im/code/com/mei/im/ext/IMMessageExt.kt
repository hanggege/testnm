package com.mei.im.ext

import com.joker.im.Message
import com.joker.im.message.CustomMessage
import com.joker.im.message.ImageMessage
import com.joker.im.message.TextMessage
import com.joker.im.message.VoiceMessage


/**
 *  Created by zzw on 2020-01-09
 *  Des:
 */


/**
 * 支持im的展示的消息类型
 */
fun Message.isImSupportUIMsg(): Boolean {
    return if (this is TextMessage || this is ImageMessage || this is VoiceMessage) {
        true
    } else (this is CustomMessage)
}


/**
 * 获取提示
 */
fun Message.getRealSummary(): String {
    return if (this is CustomMessage) {
        this.getSummary().replace("#", "")
    } else {
        this.getSummary().replace("#", "")
    }
}

/**
 * 是否是指令id
 */
fun Int?.isCmdId(): Boolean = this != null && this in 111..115



