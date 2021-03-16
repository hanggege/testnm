package com.mei.huawei

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.mei.im.push.runAsyncCallBack
import com.mei.im.push.saveOfflinePushToken
import com.mei.im.push.thirdToken
import com.tencent.imsdk.utils.IMFunc

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/30
 */
/**
 * 推送版本检查，版本过低时自动引导升级
 */
fun Activity.checkHuaweiPushKit() {
    if (thirdToken.isEmpty() && IMFunc.isBrandHuawei()) registeredHuawei()
}

internal fun Context.registeredHuawei() {
    runAsyncCallBack(action = {
        try {
            val appId = AGConnectServicesConfig.fromContext(this).getString("client/app_id")
            val pushtoken = HmsInstanceId.getInstance(this).getToken(appId, "HCM")
            pushtoken.orEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }) {
        thirdToken = it.orEmpty()
        saveOfflinePushToken(it.orEmpty())
        Log.e("HuaweiPushService", " token : $it ");
    }
}

internal class HuaweiPushService : HmsMessageService() {

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.e("HuaweiPushService", "onNewToken:$token")
        thirdToken = token.orEmpty()
        saveOfflinePushToken(token.orEmpty())
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        remoteMessage?.let {
            val json = Gson().toJson(it)
            Log.e("HuaweiPushService", "$json: ");
        }
    }


    override fun onMessageSent(msg: String?) {
        super.onMessageSent(msg)
    }

    override fun onSendError(p0: String?, p1: Exception?) {
        super.onSendError(p0, p1)
    }
}