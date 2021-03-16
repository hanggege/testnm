package com.mei.im

import com.mei.im.ext.IMPushNotify

/**
 *  Created by zzw on 2019-07-11
 *  Des:
 */


const val PUSH_MESSAGE_IDENTIFY = "push_message_identify"
const val PUSH_MESSAGE_IS_GROUP = "push_message_is_group"
const val PUSH_TIM_REPORT_DATA = "push_tim_report_data"

private val impl: IMPushNotify by lazy {
    IMPushNotify()
}

/**
 * 删除指定id的通知
 */
fun resetNotifyById(pushId: String) {
    impl.resetNotifyById(pushId)
}

/**
 * 删除所有的消息通知
 */
fun resetNotifyAll() {
    impl.resetNotifyAll()
}
