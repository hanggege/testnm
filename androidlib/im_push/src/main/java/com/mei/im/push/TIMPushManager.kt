package com.mei.im.push

import android.content.Context
import android.util.Log
import com.mei.huawei.registeredHuawei
import com.mei.oppo.registeredOPPO
import com.mei.vivo.registeredVivo
import com.meizu.cloud.pushsdk.PushManager
import com.tencent.imsdk.TIMCallBack
import com.tencent.imsdk.TIMManager
import com.tencent.imsdk.TIMOfflinePushToken
import com.tencent.imsdk.session.SessionWrapper
import com.tencent.imsdk.utils.IMFunc
import com.xiaomi.mipush.sdk.MiPushClient

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/5/7
 */

internal var thirdToken = ""

fun Context.registeredOfflinePush() {
    if (SessionWrapper.isMainProcess(applicationContext)) {
        when {
            IMFunc.isBrandHuawei() -> registeredHuawei()
            IMFunc.isBrandXiaoMi() -> MiPushClient.registerPush(this, BuildConfig.XIAOMI_APP_ID, BuildConfig.XIAOMI_APP_KEY)
            IMFunc.isBrandVivo() -> registeredVivo()
            IMFunc.isBrandMeizu() -> PushManager.register(this, BuildConfig.MZ_PUSH_APPID, BuildConfig.MZ_PUSH_APPKEY)
            IMFunc.isBrandOppo() -> registeredOPPO()
        }
        checkIMLoginState()
    }
}

internal fun saveOfflinePushToken(token: String = thirdToken) {
    val buzId = parseThirdBuzId()
    if (buzId > 0 && token.isNotEmpty())
        TIMManager.getInstance().setOfflinePushToken(TIMOfflinePushToken(buzId, token), object : TIMCallBack {
            override fun onSuccess() {
                Log.e("saveOfflinePushToken", "onSuccess $token")
            }

            override fun onError(code: Int, error: String?) {
                Log.e("saveOfflinePushToken", "onError: $code  $error   $token")
            }
        })
}


