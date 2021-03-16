package com.mei.chat.ui.adapter

import android.view.ViewGroup
import android.widget.Space
import com.joker.im.Message
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.message.CustomMessage
import com.joker.im.message.ImageMessage
import com.joker.im.message.TextMessage
import com.joker.im.message.VoiceMessage
import com.mei.chat.ui.adapter.item.IMBaseMessageHolder
import com.mei.chat.ui.adapter.item.IMEmptyHolder
import com.mei.chat.ui.adapter.item.IMSystemNotifyHolder
import com.mei.chat.ui.adapter.item.receive.*
import com.mei.chat.ui.adapter.item.receive.custom.IMReceiveCourseCardMessageHolder
import com.mei.chat.ui.adapter.item.receive.custom.IMReceiveCustomTextMessageHolder
import com.mei.chat.ui.adapter.item.receive.custom.IMReceiveGiftMessageHolder
import com.mei.chat.ui.adapter.item.receive.custom.IMReceiveServiceCardMessageHolder
import com.mei.chat.ui.adapter.item.send.*
import com.mei.chat.ui.adapter.item.send.custom.IMSendCourseCardMessageHolder
import com.mei.chat.ui.adapter.item.send.custom.IMSendCustomTextMessageHolder
import com.mei.chat.ui.adapter.item.send.custom.IMSendGiftMessageHolder
import com.mei.chat.ui.adapter.item.send.custom.IMSendServiceCardMessageHolder
import com.mei.orc.ext.layoutInflaterKt
import com.mei.wood.R


/**
 * 佛祖保佑         永无BUG

 * Created by joker on 2016/12/29.
 */

private const val MESSAGE_TYPE_RECV_TXT = 0
private const val MESSAGE_TYPE_SEND_TXT = 1
private const val MESSAGE_TYPE_SEND_IMAGE = 2
private const val MESSAGE_TYPE_RECV_IMAGE = 3
private const val MESSAGE_TYPE_SEND_VOICE = 6
private const val MESSAGE_TYPE_RECV_VOICE = 7
private const val MESSAGE_TYPE_SEND_CUSTOM_TEXT = 8
private const val MESSAGE_TYPE_RECEIVE_CUSTOM_TEXT = 9
private const val MESSAGE_TYPE_SYSYTEM_NOTIFY = 10
private const val MESSAGE_TYPE_SEND_GIFT = 11
private const val MESSAGE_TYPE_RECEIVE_GIFT = 12
private const val MESSAGE_TYPE_RECEIVE_CALL_STATUS = 13
private const val MESSAGE_TYPE_SEND_CALL_STATUS = 14
private const val MESSAGE_TYPE_SEND_SERVCE_CARD = 15
private const val MESSAGE_TYPE_RECEIVE_SERVCE_CARD = 16
private const val MESSAGE_TYPE_SEND_COURSE_CARD = 20
private const val MESSAGE_TYPE_RECEIVE_COURSE_CARD = 21
private const val MESSAGE_TYPE_COUPON = 22
private const val MESSAGE_TYPE_RECEIVE_GENERAL_CARD = 23
private const val MESSAGE_TYPE_SEND_GENERAL_CARD = 24
private const val MESSAGE_TYPE_RECEIVE_GIVING_COURSE_CARD = 25
private const val MESSAGE_TYPE_SEND_GIVING_COURSE_CARD = 26

//接收未知
const val MESSAGE_TYPE_RECV_NOT_SUPPORT_THIS_VERSION = 18

//发送未知
const val MESSAGE_TYPE_SEND_NOT_SUPPORT_THIS_VERSION = 19

const val MESSAGE_TYPE_EMPTY = 110


fun getMessageType(message: Message?): Int {
    return if (message == null || message.isDeleted()) MESSAGE_TYPE_EMPTY
    else when (message) {
        is TextMessage -> if (message.isSelf) MESSAGE_TYPE_SEND_TXT else MESSAGE_TYPE_RECV_TXT
        is ImageMessage -> if (message.isSelf) MESSAGE_TYPE_SEND_IMAGE else MESSAGE_TYPE_RECV_IMAGE
        is VoiceMessage -> if (message.isSelf) MESSAGE_TYPE_SEND_VOICE else MESSAGE_TYPE_RECV_VOICE
//        message is VideoMessage -> if (message.isSelf) MESSAGE_TYPE_SEND_VIDEO else MESSAGE_TYPE_RECV_VIDEO
//        message.isRecalled() -> MESSAGE_TYPE_RECALL
        is CustomMessage -> {
            when {
                message.customMsgType == CustomType.invalid -> if (message.isSelf) MESSAGE_TYPE_SEND_NOT_SUPPORT_THIS_VERSION else MESSAGE_TYPE_RECV_NOT_SUPPORT_THIS_VERSION
                message.customMsgType == CustomType.system_notify -> MESSAGE_TYPE_SYSYTEM_NOTIFY
                message.customMsgType == CustomType.exclusive_system_notify -> MESSAGE_TYPE_SYSYTEM_NOTIFY
                message.customMsgType == CustomType.send_gift -> if (message.isSelf) MESSAGE_TYPE_SEND_GIFT else MESSAGE_TYPE_RECEIVE_GIFT
                message.customMsgType == CustomType.call_status_changed -> if (message.isSelf) MESSAGE_TYPE_SEND_CALL_STATUS else MESSAGE_TYPE_RECEIVE_CALL_STATUS
                message.customMsgType == CustomType.special_service_card -> if (message.isSelf) MESSAGE_TYPE_SEND_SERVCE_CARD else MESSAGE_TYPE_RECEIVE_SERVCE_CARD
                message.customMsgType == CustomType.course_card -> if (message.isSelf) MESSAGE_TYPE_SEND_COURSE_CARD else MESSAGE_TYPE_RECEIVE_COURSE_CARD
                message.customMsgType == CustomType.coupon -> MESSAGE_TYPE_COUPON
                message.customMsgType == CustomType.general_card -> if (message.isSelf) MESSAGE_TYPE_SEND_GENERAL_CARD else MESSAGE_TYPE_RECEIVE_GENERAL_CARD
                message.customMsgType == CustomType.general_card_popup -> if (message.isSelf) MESSAGE_TYPE_SEND_GIVING_COURSE_CARD else MESSAGE_TYPE_RECEIVE_GIVING_COURSE_CARD
                message.customInfo?.data is ChickCustomData -> if (message.isSelf) MESSAGE_TYPE_SEND_CUSTOM_TEXT else MESSAGE_TYPE_RECEIVE_CUSTOM_TEXT
                else -> {
                    if (message.isSelf) MESSAGE_TYPE_SEND_NOT_SUPPORT_THIS_VERSION else MESSAGE_TYPE_RECV_NOT_SUPPORT_THIS_VERSION
                }
            }
        }
        else -> MESSAGE_TYPE_RECV_NOT_SUPPORT_THIS_VERSION
    }

}

@Suppress("unused")
fun IMMessageListAdapter.createHolder(parent: ViewGroup, viewType: Int): IMBaseMessageHolder {
    with(parent.context) {
        return when (viewType) {
            MESSAGE_TYPE_SEND_TXT -> IMSendTextMessageHolder(layoutInflaterKt(R.layout.im_row_send_text_item, parent))
            MESSAGE_TYPE_RECV_TXT -> IMReceiveTextMessageHolder(layoutInflaterKt(R.layout.im_row_receive_text_item, parent))
            MESSAGE_TYPE_SEND_IMAGE -> IMSendPictureMessageHolder(layoutInflaterKt(R.layout.im_row_send_picture_item, parent))
            MESSAGE_TYPE_RECV_IMAGE -> IMReceivePictureMessageHolder(layoutInflaterKt(R.layout.im_row_receive_picture_item, parent))
            MESSAGE_TYPE_SEND_VOICE -> IMSendVoiceMessageHolder(layoutInflaterKt(R.layout.im_row_send_voice_item, parent))
            MESSAGE_TYPE_RECV_VOICE -> IMReceiveVoiceMessageHolder(layoutInflaterKt(R.layout.im_row_receive_voice_item, parent))
            MESSAGE_TYPE_SEND_NOT_SUPPORT_THIS_VERSION -> IMSendUnKnowMessageHolder(layoutInflaterKt(R.layout.im_row_send_text_item, parent))
            MESSAGE_TYPE_RECV_NOT_SUPPORT_THIS_VERSION -> IMReceiveUnKnowMessageHolder(layoutInflaterKt(R.layout.im_row_receive_text_item, parent))
            MESSAGE_TYPE_SEND_CUSTOM_TEXT -> IMSendCustomTextMessageHolder(layoutInflaterKt(R.layout.im_row_send_text_item, parent))
            MESSAGE_TYPE_SEND_GIFT -> IMSendGiftMessageHolder(layoutInflaterKt(R.layout.im_row_send_gift_item, parent))
            MESSAGE_TYPE_RECEIVE_GIFT -> IMReceiveGiftMessageHolder(layoutInflaterKt(R.layout.im_row_receive_gift_item, parent))
            MESSAGE_TYPE_RECEIVE_CUSTOM_TEXT -> IMReceiveCustomTextMessageHolder(layoutInflaterKt(R.layout.im_row_receive_text_item, parent))
            MESSAGE_TYPE_SYSYTEM_NOTIFY -> IMSystemNotifyHolder(layoutInflaterKt(R.layout.im_row_system_notify_item, parent))
            MESSAGE_TYPE_SEND_CALL_STATUS -> IMSendCallStatusHolder(layoutInflaterKt(R.layout.im_row_send_call_status_item, parent))
            MESSAGE_TYPE_RECEIVE_CALL_STATUS -> IMReceiveCallStatusHolder(layoutInflaterKt(R.layout.im_row_receive_call_status_item, parent))
            MESSAGE_TYPE_SEND_SERVCE_CARD -> IMSendServiceCardMessageHolder(layoutInflaterKt(R.layout.im_row_send_service_card_item, parent))
            MESSAGE_TYPE_RECEIVE_SERVCE_CARD -> IMReceiveServiceCardMessageHolder(layoutInflaterKt(R.layout.im_row_receive_service_card_item, parent))
            MESSAGE_TYPE_SEND_COURSE_CARD -> IMSendCourseCardMessageHolder(layoutInflaterKt(R.layout.im_row_send_course_card_item, parent))
            MESSAGE_TYPE_RECEIVE_COURSE_CARD -> IMReceiveCourseCardMessageHolder(layoutInflaterKt(R.layout.im_row_receive_course_card_item, parent))
            MESSAGE_TYPE_COUPON -> IMReceiveCouponMessageHolder(layoutInflaterKt(R.layout.im_row_receive_coupon_msg_item, parent))
            MESSAGE_TYPE_SEND_GIVING_COURSE_CARD,
            MESSAGE_TYPE_SEND_GENERAL_CARD -> IMSendGeneralCardHolder(layoutInflaterKt(R.layout.im_row_send_general_card, parent))
            MESSAGE_TYPE_RECEIVE_GIVING_COURSE_CARD,
            MESSAGE_TYPE_RECEIVE_GENERAL_CARD -> IMReceiveGeneralCardHolder(layoutInflaterKt(R.layout.im_row_receive_general_card, parent))

            else -> IMEmptyHolder(Space(this))
        }
    }
}



