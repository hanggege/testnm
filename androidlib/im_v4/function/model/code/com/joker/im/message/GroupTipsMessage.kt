package com.joker.im.message

import com.joker.im.Message
import com.tencent.imsdk.TIMGroupTipsElem
import com.tencent.imsdk.TIMGroupTipsType
import com.tencent.imsdk.TIMMessage

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-18
 */
class GroupTipsMessage(msg: TIMMessage) : Message(msg) {

    var opUser: String
        get() = timElement?.opUser.orEmpty()
        set(_) {}
    var tipsType: TIMGroupTipsType
        get() = timElement?.tipsType ?: TIMGroupTipsType.Invalid
        set(_) {}

    var timElement: TIMGroupTipsElem?
        get() = if (timMessage.elementCount > 0) (timMessage.getElement(0) as? TIMGroupTipsElem) else null
        set(_) {}

    override fun getCopySummary(): String = ""

    override fun getSummary(): String {
        val stringBuilder = StringBuilder()
        if (timMessage.elementCount > 0) {
            (timMessage.getElement(0) as? TIMGroupTipsElem)?.apply {
                stringBuilder.append(changedUserInfo.orEmpty()
                        .mapNotNull { it.value?.nickName }
                        .filter { it.isNotEmpty() }
                        .joinToString(separator = " "))
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(" ").append(
                            when (this.tipsType) {
                                TIMGroupTipsType.Join -> "加入了群"
                                TIMGroupTipsType.Kick, TIMGroupTipsType.Quit -> "退出了群"
                                else -> ""
                            }
                    )
                }
            }
        }
        return stringBuilder.toString()
    }
}