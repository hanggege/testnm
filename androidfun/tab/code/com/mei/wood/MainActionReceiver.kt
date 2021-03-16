package com.mei.wood

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.mei.base.ui.nav.Nav
import com.mei.login.ext.showForbiddenDialog
import com.mei.orc.common.CommonConstant
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.ui.TabMainActivity
import com.mei.wood.ui.changeLogin
import com.mei.wood.util.AppManager
import org.json.JSONObject

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/9/1.
 */


/**
 * 这些只弹窗  不弹toast
 */
fun Int.needToast(): Boolean {
    return this != CommonConstant.Config.BASE_CODE_T_SESSION &&
            this != CommonConstant.Config.BASE_CODE_SESSION_PAST &&
            this != CommonConstant.Config.BASE_CODE_SESSION_EXPIREED &&
            this != CommonConstant.Config.BASE_CODE_BAD_SESSION &&
            this != CommonConstant.Config.LOGIN_BASE_CODE_BAD_SESSION
}

class MainActionReceiver(private val mainActivity: TabMainActivity) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                CommonConstant.Action.LOGIN() -> {
                    mainActivity.changeLogin(true)
                }
                CommonConstant.Action.LOGOUT() -> {
                    mainActivity.changeLogin(false)
                }
                CommonConstant.Action.SESSION_EXPIRED() -> {
                    mainActivity.runOnUiThread {
                        handleSessionErrorRtn(
                                intent.getIntExtra(CommonConstant.DataKey.session_rtn_code, 0),
                                json = intent.getStringExtra(CommonConstant.DataKey.session_rtn_json)
                                        ?: ""
                        )
                    }
                }
            }
        }
    }

    private var canShow = true

    /**
     * 处理session相关的错误码
     */
    private fun handleSessionErrorRtn(errorRtn: Int, json: String = "") {
        try {
            Nav.clearInfo()
            val activity = if (AppManager.getInstance().currentActivity() != null) AppManager.getInstance().currentActivity() else mainActivity
            if (errorRtn == CommonConstant.Config.BASE_CODE_T_SESSION) {// 账号被踢
                /**账号被踢后，把当前显示在app页面的弹框去掉*/
                (activity as? FragmentActivity)?.supportFragmentManager?.fragments?.forEach { dialog ->
                    (dialog as? MeiSupportDialogFragment)?.dismiss()
                }
                val tip1103 = "你的账号在其他设备上登录，如果这不是你的操作，你的账号可能已泄露。"
                if (activity is FragmentActivity && activity.supportFragmentManager.findFragmentByTag("logout") == null && canShow) {
                    canShow = false
                    NormalDialogLauncher.newInstance().showDialog(activity, DialogData(tip1103, "", canceledOnTouchOutside = false, backCanceled = false, title = "安全提示", key = "logout"), okBack = {
                        //                        Nav.exitToMain()
                        canShow = true
                        //让用户重新登录
                        Nav.logout(activity, false)
                    })
                    return
                }
            } else if (errorRtn == CommonConstant.Config.BASE_CODE_SESSION_PAST || errorRtn == CommonConstant.Config
                            .BASE_CODE_SESSION_EXPIREED ||
                    errorRtn == CommonConstant.Config.BASE_CODE_NO_SESSION) {
                // session过期
                val tip1100 = "你的登录状态过期，请重新登录"
                if (activity is FragmentActivity && activity.supportFragmentManager.findFragmentByTag("logout") == null)
                    NormalDialogLauncher.newInstance().showDialog(activity, DialogData(tip1100, "", canceledOnTouchOutside = false, backCanceled = false, title = "安全提示", key = "logout"), okBack = {
                        Nav.exitToMain()
                    })
                return
            } else if (errorRtn == CommonConstant.Config.BASE_CODE_BAD_SESSION) {
                // 账号被封
                var tip20202 = try {
                    JSONObject(json).getString("msg") ?: ""
                } catch (e: Exception) {
                    ""
                }
                if (tip20202.isEmpty()) {
                    tip20202 = "系统检测到账号异常，为了你的账号安全，系统暂时停用了你的账号，详情请联系官方客服。"
                }
                activity.showForbiddenDialog(tip20202) {
                    Nav.exitToMain()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}