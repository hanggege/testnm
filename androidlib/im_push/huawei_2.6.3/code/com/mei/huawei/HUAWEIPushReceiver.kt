package com.mei.huawei

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.huawei.android.hms.agent.HMSAgent
import com.huawei.hms.support.api.push.PushReceiver
import com.mei.im.push.saveOfflinePushToken
import com.mei.im.push.thirdToken

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/5/9
 */

internal fun Application.registeredHuawei() {
    HMSAgent.init(this)
    registerActivityLifecycleCallbacks(PushActivityLifecycleCall {
        if (it::class.java.simpleName.equals("TabMainActivity", ignoreCase = true)) {
            HMSAgent.connect(it) { rst ->
                Log.e("HuaweiPushService", "huawei push HMS connect end: $rst")
            }
            HMSAgent.Push.getToken { code ->
                Log.e("HuaweiPushService", "huawei push get token result code: $code")
            }
        }
    })
}

internal class HUAWEIPushReceiver : PushReceiver() {

    override fun onEvent(context: Context?, event: Event?, extras: Bundle?) {
        super.onEvent(context, event, extras)
        extras?.keySet().orEmpty().forEach {
            Log.e("HuaweiPushService", "$it  :  ${extras?.get(it)}")
        }
    }

    override fun onPushMsg(context: Context?, msg: ByteArray?, token: String?) {
        super.onPushMsg(context, msg, token)
        Log.e("HuaweiPushService", "onPushMsg: ${String(msg ?: ByteArray(0))}")
    }

    override fun onToken(context: Context?, token: String?) {
        super.onToken(context, token)
        Log.e("HuaweiPushService", "onNewToken:$token")
        thirdToken = token.orEmpty()
        saveOfflinePushToken(thirdToken)
    }
}

internal class PushActivityLifecycleCall(val onCreate: (Activity) -> Unit) : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        onCreate(activity)
    }

    override fun onActivityResumed(activity: Activity) {
    }

}



