package com.mei.meizu

import android.content.Context
import android.util.Log
import com.mei.im.push.saveOfflinePushToken
import com.mei.im.push.thirdToken
import com.meizu.cloud.pushsdk.MzPushMessageReceiver
import com.meizu.cloud.pushsdk.handler.MzPushMessage
import com.meizu.cloud.pushsdk.platform.message.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/5/8
 */
class MEIZUPushReceiver : MzPushMessageReceiver() {

    override fun onRegister(p0: Context?, p1: String?) {
        Log.e("MZPushReceiver", "onRegister: $p1")
    }

    override fun onSubTagsStatus(p0: Context?, status: SubTagsStatus?) {
        Log.e("MZPushReceiver", "onSubTagsStatus: ${status?.pushId}")
    }

    override fun onRegisterStatus(p0: Context?, registerStatus: RegisterStatus?) {
        Log.e("MZPushReceiver", "onRegisterStatus: ${registerStatus?.pushId} ")
        thirdToken = registerStatus?.pushId.orEmpty()
        saveOfflinePushToken(thirdToken)
    }

    override fun onUnRegisterStatus(p0: Context?, status: UnRegisterStatus?) {
        Log.e("MZPushReceiver", "onUnRegisterStatus: ${status?.code}  ${status?.message}")
    }

    override fun onSubAliasStatus(p0: Context?, status: SubAliasStatus?) {
        Log.e("MZPushReceiver", "onSubAliasStatus: ${status?.pushId}")
    }

    override fun onUnRegister(p0: Context?, p1: Boolean) {
        Log.e("MZPushReceiver", "onUnRegister: $p1")
    }

    override fun onPushStatus(p0: Context?, status: PushSwitchStatus?) {
        Log.e("MZPushReceiver", "onPushStatus: ${status?.pushId}")
    }

    /**
     * 用户点击通知栏
     */
    override fun onNotificationClicked(p0: Context?, mzPushMessage: MzPushMessage?) {
        super.onNotificationClicked(p0, mzPushMessage)
        Log.e("MZPushReceiver", "onNotificationClicked: ${mzPushMessage?.selfDefineContentString} ");
    }
}