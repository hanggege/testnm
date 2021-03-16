package com.mei.oppo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.heytap.mcssdk.PushManager
import com.heytap.mcssdk.callback.PushCallback
import com.heytap.mcssdk.mode.SubscribeResult
import com.mei.im.push.BuildConfig
import com.mei.im.push.saveOfflinePushToken
import com.mei.im.push.thirdToken

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/5/8
 */

internal fun Context.registeredOPPO() {
    if (PushManager.isSupportPush(this)) {
        createNotificationChannel()
        PushManager.getInstance().register(this, BuildConfig.OPPO_PUSH_APPKEY, BuildConfig.OPPO_PUSH_SECRET, OPPOPushImpl())
    } else Log.e("opPushImpl", "registeredOPPO：不支持推送")
}

internal fun Context.createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name: CharSequence = "mei_goat_oppo"
        val description = "mei_goat_oppo"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("mei_goat_oppo", name, importance)
        channel.description = description
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager: NotificationManager? = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}

internal class OPPOPushImpl : PushCallback {

    override fun onRegister(code: Int, regId: String?) {
        Log.e("opPushImpl", "onRegister: $code  $regId ")
        thirdToken = regId.orEmpty()
        saveOfflinePushToken(thirdToken)
    }

    override fun onGetNotificationStatus(code: Int, status: Int) {
        if (code == 0 && status == 0) {
            Log.e("opPushImpl", "onGetNotificationStatus: 通知状态正常 ")
        } else {
            Log.e("opPushImpl", "onGetNotificationStatus: 通知状态错误：$code  $status ")
        }
    }

    override fun onUnRegister(responseCode: Int) {
        Log.i("opPushImpl", "onUnRegister responseCode: $responseCode")
    }

    override fun onSetPushTime(responseCode: Int, s: String) {
        Log.i("opPushImpl", "onSetPushTime responseCode: $responseCode s: $s")
    }

    override fun onGetPushStatus(responseCode: Int, status: Int) {
        Log.i("opPushImpl", "onGetPushStatus responseCode: $responseCode status: $status")
    }

    override fun onGetAliases(responseCode: Int, alias: List<SubscribeResult?>) {
        Log.i("opPushImpl", "onGetAliases responseCode: $responseCode alias: $alias")
    }

    override fun onSetAliases(responseCode: Int, alias: List<SubscribeResult?>) {
        Log.i("opPushImpl", "onSetAliases responseCode: $responseCode alias: $alias")
    }

    override fun onUnsetAliases(responseCode: Int, alias: List<SubscribeResult?>) {
        Log.i("opPushImpl", "onUnsetAliases responseCode: $responseCode alias: $alias")
    }

    override fun onSetUserAccounts(responseCode: Int, accounts: List<SubscribeResult?>) {
        Log.i("opPushImpl", "onSetUserAccounts responseCode: $responseCode accounts: $accounts")
    }

    override fun onUnsetUserAccounts(responseCode: Int, accounts: List<SubscribeResult?>) {
        Log.i("opPushImpl", "onUnsetUserAccounts responseCode: $responseCode accounts: $accounts")
    }

    override fun onGetUserAccounts(responseCode: Int, accounts: List<SubscribeResult?>) {
        Log.i("opPushImpl", "onGetUserAccounts responseCode: $responseCode accounts: $accounts")
    }

    override fun onSetTags(responseCode: Int, tags: List<SubscribeResult?>) {
        Log.i("opPushImpl", "onSetTags responseCode: $responseCode tags: $tags")
    }

    override fun onUnsetTags(responseCode: Int, tags: List<SubscribeResult?>) {
        Log.i("opPushImpl", "onUnsetTags responseCode:  tags: $tags")
    }

    override fun onGetTags(responseCode: Int, tags: List<SubscribeResult?>) {
        Log.i("opPushImpl", "onGetTags responseCode: $responseCode tags: $tags")
    }


}