package com.mei.vivo

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.mei.im.push.BuildConfig
import com.mei.im.push.saveOfflinePushToken
import com.mei.im.push.thirdToken
import com.vivo.push.PushClient
import com.vivo.push.model.UPSNotificationMessage
import com.vivo.push.sdk.OpenClientPushMessageReceiver
import com.vivo.push.ups.VUpsManager

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/5/7
 */

fun Context.registeredVivo() {
    /** 1.集成SDK **/
    PushClient.getInstance(applicationContext).initialize()
    /** 2.启动推送 **/
    PushClient.getInstance(applicationContext).turnOffPush { state ->
        if (state == 0) {
            /** 3.注册push **/
            VUpsManager.getInstance().registerToken(applicationContext,
                    BuildConfig.VIVO_APP_ID, BuildConfig.VIVO_APP_KEY, BuildConfig.VIVO_APP_SECRET)
            { result ->
                if (result.returnCode == 0) {
                    thirdToken = result.token.orEmpty()
                    saveOfflinePushToken(thirdToken)
                }
                Log.e("pushVi", "registerToken: ${result.returnCode} $thirdToken")
            }
        }
        Log.e("pushVi", "turnOffPush: $state")
    }

}

class VIVOPushMessageReceiverImpl : OpenClientPushMessageReceiver() {

    /**
     * 当通知被点击时回调此方法
     * @param context 应用上下文
     * @param msg 通知详情，详细信息见API接入文档
     */
    override fun onNotificationMessageClicked(p0: Context?, msg: UPSNotificationMessage?) {
        Log.e("pushViPushMessage", "onNotificationMessageClicked: ${Gson().toJson(msg)} ")
    }

    /**
     * 当首次turnOnPush成功或regId发生改变时，回调此方法
     * 如需获取regId，请使用PushClient.getInstance(context).getRegId()
     * @param context 应用上下文
     * @param regId 注册id
     */
    override fun onReceiveRegId(p0: Context?, regId: String?) {
        Log.e("pushViPushMessage", "onReceiveRegId: $regId")
        if (regId.orEmpty().isNotEmpty()) {
            thirdToken = regId.orEmpty()
            saveOfflinePushToken(thirdToken)
        }
    }
}