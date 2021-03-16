package com.joker.im.message

import com.joker.im.Message
import com.tencent.imsdk.TIMGroupSystemElem
import com.tencent.imsdk.TIMGroupSystemElemType
import com.tencent.imsdk.TIMMessage

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-20
 */
class GroupSystemMessage(msg: TIMMessage) : Message(msg) {

    var groupSystemType: TIMGroupSystemElemType
        get() = if (timMessage.elementCount > 0) {
            (timMessage.getElement(0) as? TIMGroupSystemElem)?.subtype
                    ?: TIMGroupSystemElemType.INVALID
        } else TIMGroupSystemElemType.INVALID
        set(_) {}

    var groupId: String
        get() = if (timMessage.elementCount > 0) {
            (timMessage.getElement(0) as? TIMGroupSystemElem)?.groupId.orEmpty()
        } else ""
        set(_) {}

    override fun getSummary(): String = ""

    override fun getCopySummary(): String = ""

}