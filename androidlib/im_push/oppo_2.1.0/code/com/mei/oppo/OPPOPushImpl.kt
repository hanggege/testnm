package com.mei.oppo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.heytap.msp.push.HeytapPushManager
import com.heytap.msp.push.callback.ICallBackResultService
import com.mei.im.push.BuildConfig
import com.mei.im.push.saveOfflinePushToken
import com.mei.im.push.thirdToken

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/5/8
 */

internal fun Context.registeredOPPO() {
    HeytapPushManager.init(this, true)
    if (HeytapPushManager.isSupportPush()) {
        createNotificationChannel()
        HeytapPushManager.register(this, BuildConfig.OPPO_PUSH_APPKEY, BuildConfig.OPPO_PUSH_SECRET, OPPOPushImpl())
    } else Log.e("opPushImpl", "registeredOPPO：不支持推送")
}

internal fun Context.createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name: CharSequence = "消息"
        val description = "消息通知"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("mei_goat_oppo", name, importance)
        channel.description = description
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager: NotificationManager? = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}

internal class OPPOPushImpl : ICallBackResultService {
    override fun onGetPushStatus(code: Int, status: Int) {
    }

    override fun onSetPushTime(code: Int, msg: String?) {
    }

    override fun onGetNotificationStatus(code: Int, status: Int) {
        if (code == 0 && status == 0) {
            Log.e("opPushImpl", "onGetNotificationStatus: 通知状态正常 ")
        } else {
            Log.e("opPushImpl", "onGetNotificationStatus: 通知状态错误：$code  $status ")
        }
    }

    override fun onUnRegister(code: Int) {
    }

    override fun onRegister(code: Int, regId: String?) {
        Log.e("opPushImpl", "onRegister: $code  $regId ")
        thirdToken = regId.orEmpty()
        saveOfflinePushToken(thirdToken)
    }

}