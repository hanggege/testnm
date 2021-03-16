package com.mei.login

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.mei.base.ui.nav.Nav
import com.mei.deepLinkMap
import com.mei.init.spiceHolder
import com.mei.intimate.TouristsActivity
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.save.getValue
import com.mei.reportDeepLink
import com.mei.wood.extensions.executeKt
import com.net.model.chick.tab.LoginType
import com.net.model.chick.tab.clearTabItem
import com.net.model.chick.tab.tabbarConfig
import com.net.network.report.FirstLoginRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/6/16
 */
/**
 * 检查是否登录,且回调成功结果
 */
@JvmOverloads
fun Context?.checkLogin(back: (Boolean) -> Unit = {}) {
    if (JohnUser.getSharedUser().hasLogin()) {
        back(true)
    } else {
        (this as? FragmentActivity)?.toLogin { succ, _ -> back(succ) }
    }
}

/**
 * 检查是否登录，如果未登录只去登录 不回调结果
 */
fun Context?.checkLogin(): Boolean {
    if (!JohnUser.getSharedUser().hasLogin()) {
        toLogin()
        return false
    }
    return true
}


fun Context?.quickLogin(loginBack: (Boolean) -> Unit = { }) {
    toLogin { success, _ -> loginBack(success) }
}

/**
 * @param back 返回登录结果
 */
@JvmOverloads
fun Context?.toLogin(loginType: LoginType = tabbarConfig.loginConfig?.first
        ?: LoginType.login_type_wsp,
                     back: (Boolean, isReg: Boolean) -> Unit = { _, _ -> }): Boolean {
    val loginBack: (Boolean, isReg: Boolean) -> Unit = { succ, isReg ->
        if (succ) {
            clearTabItem()
            reportDeepLink { deepLinkMap.clear() }
        }
        isLoginByRegistered = isReg
        back(succ, isReg)
        if (isReg) {
            spiceHolder().apiSpiceMgr.executeKt(FirstLoginRequest())
        }
        (this as? TouristsActivity)?.apply {
            if (succ) {
                Nav.toMain(this)
                finish()
            }
        }
    }

    return when (loginType) {
        LoginType.login_type_swp -> this?.toLoginByShanYan(loginBack) ?: false
        LoginType.login_type_p -> this?.toLoginByPhoneNo(back = loginBack) ?: false
        LoginType.login_type_wsp,
        LoginType.login_type_wp -> this?.toLoginByWeChat(loginBack) ?: false
    }
}


/**
 * ----------------------------------  第一次登陆  ----------------------------------
 */
const val FIRST_LOGIN_KEY = "FIRST_LOGIN_KEY"
fun checkFirstLogin(): Boolean = FIRST_LOGIN_KEY.getValue(true) ?: true