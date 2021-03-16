package com.joker.im.message

import com.joker.im.Message
import com.tencent.imsdk.TIMMessage
import com.tencent.imsdk.TIMTextElem

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-13
 */
class TextMessage : Message {

    constructor(msg: TIMMessage) : super(msg)

    constructor(txt: String) : super(TIMMessage().apply {
        addElement(TIMTextElem().apply { text = txt })
    })

    override fun getSummary(): String {
        val result = StringBuilder()
        if (timMessage.elementCount > 0) {
            for (index in 0 until timMessage.elementCount.toInt()) {
                (timMessage.getElement(index) as? TIMTextElem)?.let {
                    result.append(it.text)
                }
            }
        }
        return result.toString()
    }

    override fun getCopySummary(): String = getSummary()
}