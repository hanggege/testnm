package com.mei.shanyan

import android.content.Context
import com.chuanglan.shanyan_sdk.OneKeyLoginManager
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig
import com.mei.orc.util.click.isNotOnDoubleClick
import com.mei.orc.util.json.getStringValue
import com.mei.wood.BuildConfig

/**
 *  Created by zzw on 2020-01-19
 *  Des:
 */


var isInit = false


/**
 * 初始化
 */
fun Context.initSY(back: (isOk: Boolean) -> Unit = {}) {
    OneKeyLoginManager.getInstance().setDebug(BuildConfig.IS_TEST)
    if (isInit) {
        back(true)
        return
    }
    //闪验SDK配置debug开关 （必须放在初始化之前，开启后可打印闪验SDK更加详细日志信息）
    OneKeyLoginManager.getInstance().setDebug(BuildConfig.IS_TEST)

    //闪验SDK初始化（建议放在Application的onCreate方法中执行）
    OneKeyLoginManager.getInstance().init(applicationContext, getSYAppId()) { code, result ->
        //闪验SDK初始化结果回调
        shanyanLog(msg = "初始化： code==$code   result==$result")
        isInit = code == 1022
        back(code == 1022)
        if (isInit) realCheckPhoneInfo()
    }

}


/**
 * 检测是否能够获取手机卡信息
 */
fun realCheckPhoneInfo(back: (isOk: Boolean) -> Unit = { }) {
    try {//闪验SDK预取号（可缩短拉起授权页时间）
        OneKeyLoginManager.getInstance().getPhoneInfo { code, result ->
            //预取号回调
            shanyanLog(msg = "预取号： code==$code   result==$result")
            back(code == 1022)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

/**
 * 授权
 * @param back code:1成功  0 返回失败  -1 失败
 * @
 */
fun openLoginAuth(config: ShanYanUIConfig, openFailure: (code: Int) -> Unit, openSuccess: () -> Unit,
                  callBack: (code: Int, result: String) -> Unit) {
    OneKeyLoginManager.getInstance().setAuthThemeConfig(config)
    OneKeyLoginManager.getInstance().openLoginAuth(false, { code, result ->
        if (code == 1000) {
            openSuccess()
            //拉起授权页成功
            shanyanLog(msg = "拉起授权页成功： _code==$code   _result==$result")
        } else {
            openFailure(code)
            //拉起授权页失败
            shanyanLog(msg = "拉起授权页失败： _code==$code   _result==$result")
        }
    }, { code, result ->
        OneKeyLoginManager.getInstance().setLoadingVisibility(false)
        when (code) {
            1011 -> {
                shanyanLog(msg = "用户点击授权页返回： _code==$code   _result==$result")
                callBack(0, "用户点击授权页返回")
            }
            1000 -> {
                shanyanLog(msg = "用户点击登录获取token成功： _code==$code   _result==$result")
                val token = result.orEmpty().getStringValue("token", "")
                if (token.isNotEmpty()) {
                    isNotOnDoubleClick(2000, "apply_local_phone_no") {
                        callBack(1, token)
                    }
                } else callBack(-1, "返回数据无效")
            }
            else -> {
                shanyanLog(msg = "用户点击登录获取token失败： _code==$code   _result==$result")
                callBack(-1, "用户点击登录获取token失败")
            }
        }
    })
}
