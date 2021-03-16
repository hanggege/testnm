package com.mei.im.helper

import android.text.TextUtils
import com.joker.im.Message
import com.joker.im.imLoginId
import com.mei.orc.common.CommonConstant
import com.mei.orc.unit.TimeUnit
import com.tencent.imsdk.TIMMessageStatus
import kotlin.math.abs

/**
 * 佛祖保佑         永无BUG

 * Created by joker on 2016/12/29.
 */


/**
 * 是否能撤回
 */
fun isRecallableMsg(message: Message?, isGroupManger: Boolean): Boolean {
    try {
        return message != null
                && checkPermissions(message, isGroupManger)
                && message.state == TIMMessageStatus.SendSucc
    } catch (e: Exception) {
        return false
    }

}

fun checkPermissions(message: Message, groupManger: Boolean): Boolean =
        (message.isSelf && (abs(System.currentTimeMillis() / TimeUnit.SECOND - message.timMessage.timestamp()) < TimeUnit.MINUTE * 2 / TimeUnit.SECOND))
                || groupManger || TextUtils.equals(CommonConstant.IMOfficialWood.ID_XIAOLU_OFFICIAL, imLoginId())

