package com.mei.userinfocomplement

import android.app.Activity
import android.content.Context
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.startactivity.startFragmentForResult2

/**
 *  Created by zzw on 2019-12-03
 *  Des:
 */


const val INFO_COMPLEMENT_STATE_KEY = "INFO_COMPLEMENT_STATE_KEY"

/**
 * 完善信息
 */
fun Context.toInfoComplement(loginId: Int = JohnUser.getSharedUser().userID,
                             sessionId: String = JohnUser.getSharedUser().sessionID,
                             back: (Boolean) -> Unit = {}) {
    (this as? Activity)?.startFragmentForResult2(UserInfoComplementActivityLauncher.getIntentFrom(this, loginId, sessionId), 110)
    { code, intent ->
        if (code == 110) {
            val status = intent?.getBooleanExtra(INFO_COMPLEMENT_STATE_KEY, false) ?: false
            back(status)
        }
    }
}


fun Int?.checkCompleteRtn(): Boolean = when (this) {
    1103, 1147, 1102, 1148, 1149, 1101, 1100, 20202 -> true
    else -> false
}

//SESSION_CHECK_ERROR(1406, "登录态验证未通过"),
//SESSION_KICKOUT(1103, "您的账号在其他设备上登录,已被T下线"),
//SESSION_ID_ERROR(1147, "SESSION_ID不正确"),
//SESSION_NULL(1102, "SESSION_ID参数无效"),
//LOGIN_USER_ID_ERROR(1148, "LOGIN_USER_ID不正确"),
//ADMIN_ID_ERROR(1149, "ADMIN_ID不正确"),
//SESSION_EXPIRED(1101, "您的账号登录状态过期，请重新登录"),
//LOGONG_USER_ID_ILLEGAL(1100, "LOGIN_USER_ID无效"),
//VALA_PARA_ILLEGAL(1151, "验证参数非法"),
//USER_DISABLE_LOGIN(20202, "用户被禁止登陆");