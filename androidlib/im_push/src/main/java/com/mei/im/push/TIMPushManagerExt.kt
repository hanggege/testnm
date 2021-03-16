package com.mei.im.push

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import com.joker.im.listener.IMAllEventManager
import com.joker.im.registered
import com.tencent.imsdk.utils.IMFunc

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/5/7
 */

internal fun checkIMLoginState() {
    object : IMAllEventManager() {
        override fun onLoginSuccess() {
            super.onLoginSuccess()
            saveOfflinePushToken(thirdToken)
        }
    }.registered()
}

internal fun parseThirdBuzId() = when {
    IMFunc.isBrandXiaoMi() -> BuildConfig.XM_PUSH_BUZID
    IMFunc.isBrandHuawei() -> BuildConfig.HW_PUSH_BUZID
    IMFunc.isBrandMeizu() -> BuildConfig.MZ_PUSH_BUZID
    IMFunc.isBrandVivo() -> BuildConfig.VIVO_PUSH_BUZID
    IMFunc.isBrandOppo() -> BuildConfig.OPPO_PUSH_BUZID
    else -> 0L
}

internal fun <T> runAsyncCallBack(action: () -> T?, uiBack: (T?) -> Unit) {
    val asyncTask = object : AsyncTask<Unit, Unit, T?>() {

        override fun doInBackground(vararg params: Unit?): T? {
            return action()
        }

        override fun onPostExecute(result: T?) {
            super.onPostExecute(result)
            uiBack(result)

        }
    }
    asyncTask.execute()
}

internal fun runDelayedOnUiThread(delayMillis: Long, action: () -> Unit) = Handler(Looper.getMainLooper()).postDelayed(Runnable(action), delayMillis)