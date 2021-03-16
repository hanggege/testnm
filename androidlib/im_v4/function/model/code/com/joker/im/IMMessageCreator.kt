package com.joker.im

import com.joker.im.custom.CustomInfo
import com.joker.im.custom.CustomType
import com.joker.im.custom.NoneData
import com.joker.im.message.*
import com.tencent.imsdk.TIMElemType
import com.tencent.imsdk.TIMMessage

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-18
 */

fun TIMMessage?.mapToMeiMessage(): Message? {
    var msg: Message? = null
    this?.apply {
        if (elementCount > 0) {
            msg = when (getElement(0).type) {
                TIMElemType.Text -> TextMessage(this)
                TIMElemType.Image -> ImageMessage(this)
                TIMElemType.Sound -> VoiceMessage(this)
                TIMElemType.Video -> VideoMessage(this)
                TIMElemType.File -> FileMessage(this)
                TIMElemType.GroupTips -> GroupTipsMessage(this)
                TIMElemType.Custom -> {
                    val customMsg = CustomMessage(this)
                    /** 如果是不支持的消息类型 **/
                    if (!customMsg.isSupportMsg()) {
                        customMsg.formatUnKnowMessage()
                    }
                    customMsg
                }
                TIMElemType.GroupSystem -> GroupSystemMessage(this)
                else -> {
                    /** 如果是不支持的消息类型 **/
                    CustomMessage(this).apply {
                        formatUnKnowMessage()
                    }
                }
            }
        }
    }
    return msg
}

private fun CustomMessage.formatUnKnowMessage() {
    customInfo = CustomInfo(CustomType.invalid.name, NoneData())
}