package com.mei.mi

import android.content.Context
import android.util.Log
import com.mei.im.push.saveOfflinePushToken
import com.mei.im.push.thirdToken
import com.xiaomi.mipush.sdk.ErrorCode
import com.xiaomi.mipush.sdk.MiPushClient
import com.xiaomi.mipush.sdk.MiPushCommandMessage
import com.xiaomi.mipush.sdk.PushMessageReceiver

class XiaomiMsgReceiver : PushMessageReceiver() {
    private val TAG = "XiaomiMsgReceiver"

    override fun onReceiveRegisterResult(context: Context, miPushCommandMessage: MiPushCommandMessage) {
        Log.e(TAG, "onReceiveRegisterResult is called. $miPushCommandMessage")
        val command = miPushCommandMessage.command
        val arguments = miPushCommandMessage.commandArguments
        val cmdArg1 = if (arguments != null && arguments.size > 0) arguments[0] else null
        Log.e(TAG, "cmd:$command | arg:$cmdArg1 | result:$miPushCommandMessage.resultCode | reason:$miPushCommandMessage.reason")
        if (MiPushClient.COMMAND_REGISTER == command) {
            if (miPushCommandMessage.resultCode == ErrorCode.SUCCESS.toLong()) {
                thirdToken = cmdArg1.orEmpty()
            }
        }
        Log.e(TAG, "regId: $thirdToken")
        saveOfflinePushToken(thirdToken)
    }

}