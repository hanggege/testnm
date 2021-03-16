package com.mei.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.chuanglan.shanyan_sdk.OneKeyLoginManager
import com.mei.GrowingUtil
import com.mei.base.network.holder.SpiceHolder
import com.mei.im.quickLoginIM
import com.mei.init.requestTabBar
import com.mei.init.spiceHolder
import com.mei.login.ext.showForbiddenDialog
import com.mei.login.ui.LoginByPhoneActivity
import com.mei.login.ui.WechatFirstLoginActivity
import com.mei.login.view.provideShanYanConfig
import com.mei.orc.common.CommonConstant
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.clearDoubleCheck
import com.mei.orc.util.click.isOnDoubleClick
import com.mei.orc.util.save.putValue
import com.mei.orc.util.startactivity.startFragmentForResult2
import com.mei.shanyan.openLoginAuth
import com.mei.userinfocomplement.toInfoComplement
import com.mei.wood.extensions.executeKt
import com.mei.wood.rx.MeiUiFrame
import com.mei.wood.util.AppManager
import com.net.MeiUser
import com.net.model.chick.login.LoginWithOauthResult
import com.net.model.chick.tab.LoginType
import com.net.model.chick.tab.tabbarConfig
import com.net.network.chick.login.UseOwnNumberLoginRequest
import com.net.network.chick.user.MyInfoRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/6/16
 */


const val LOGIN_STATE_KEY = "login_state_key"//登录成功结果
const val LOGIN_REGISTER_KEY = "login_register_key"//登录成功是否是注册

/**
 * 微信去登录
 */
fun Context.toLoginByWeChat(back: (Boolean, isReg: Boolean) -> Unit = { _, _ -> }): Boolean {
    return if (isOnDoubleClick(1500, "LoginTag")) {
        false
    } else {
        (this as? Activity)?.startFragmentForResult2(Intent(this, WechatFirstLoginActivity::class.java), 115) { code, intent ->
            if (code == 115) {
                back(intent.getLoginState().first, intent.getLoginState().second)
            }
        }
        true
    }
}

/**
 * 调起手机号去登录
 */

fun Context.toLoginByPhoneNo(tag: String = "LoginTag", back: (Boolean, isReg: Boolean) -> Unit = { _, _ -> }): Boolean {
    return if (isOnDoubleClick(1500, tag)) {
        false
    } else {
        (this as? Activity)?.startFragmentForResult2(Intent(this, LoginByPhoneActivity::class.java), 117) { code, intent ->
            if (code == 117) {
                back(intent.getLoginState().first, intent.getLoginState().second)
            }
        }
        true
    }
}

/**
 * 调起闪验去登录
 * 这里一定要注意作用域的问题，一不小心就找不到回调了的
 */
private var shanYanStarting = false
fun Context.toLoginByShanYan(back: (Boolean, isReg: Boolean) -> Unit = { _, _ -> }, openResult: (Boolean) -> Unit = {}): Boolean {
    // 闪验打开中，禁止多次触发
    if (shanYanStarting) return false
    return if (isOnDoubleClick(1500, "LoginTag")) {
        false
    } else {
        (this as? MeiUiFrame)?.showLoading(true)
        shanYanStarting = true
        openLoginAuth(provideShanYanConfig(back), openFailure = {
            openResult(false)
            shanYanStarting = false
            (this as? MeiUiFrame)?.showLoading(false)
            finishShanYanAuth()
            clearDoubleCheck("LoginTag")
            if (tabbarConfig.loginConfig?.first == LoginType.login_type_swp) toLoginByWeChat(back)
            else toLoginByPhoneNo(back = back)
        }, openSuccess = {
            openResult(true)
            shanYanStarting = false
            (this as? MeiUiFrame)?.showLoading(false)
        }) { code, result ->
            when (code) {
                0 -> back(false, false)//返回键
                -1 -> {//数据有问题
                    finishShanYanAuth()
                    UIToast.toast("数据返回错误，请用其他方式登陆")
                    clearDoubleCheck("LoginTag")
                    if (tabbarConfig.loginConfig?.first == LoginType.login_type_swp) toLoginByWeChat(back)
                    else toLoginByPhoneNo(back = back)
                }
                1 -> {
                    OneKeyLoginManager.getInstance().setLoadingVisibility(true)
                    val apiClient = (this as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
                    apiClient.executeKt(UseOwnNumberLoginRequest(result), success = {
                        when {
                            it.isSuccess -> {
                                // 这里想在ShanYanOneKeyActivity马上获取回调进行页面关闭
                                AppManager.getInstance().currentActivity().checkLoginUserInfo(it.data) { success ->
                                    if (success) {
                                        back(success, it.data?.isReg == 1)
                                        finishShanYanAuth()
                                    } else {
                                        UIToast.toast(this, "完善信息失败，请重试")
                                    }
                                }
                            }
                            it.rtn == CommonConstant.Config.LOGIN_BASE_CODE_BAD_SESSION || it.rtn == CommonConstant.Config.BASE_CODE_BAD_SESSION -> {
                                AppManager.getInstance().currentActivity().showForbiddenDialog(it.errMsg, cancelBack = {
                                    back(false, false)
                                    finishShanYanAuth()
                                }) {
                                    back(false, false)
                                    finishShanYanAuth()
                                }
                            }
                            else -> {
                                /** 本地接口出错，可进行重试 **/
                                UIToast.toast(this, it.errMsg)
                            }
                        }
                    }, failure = {
                        UIToast.toast(this, it?.currMessage ?: "登录失败，网络出错")
                    }, finish = {
                        OneKeyLoginManager.getInstance().setLoadingVisibility(false)
                    })
                }
            }
        }
        true
    }
}

fun finishShanYanAuth() {
    OneKeyLoginManager.getInstance().finishAuthActivity()
    OneKeyLoginManager.getInstance().removeAllListener()
}

private fun Intent?.getLoginState() = Pair(this?.getBooleanExtra(LOGIN_STATE_KEY, false)
        ?: false, this?.getBooleanExtra(LOGIN_REGISTER_KEY, false) ?: false)

/**
 * 检查用户信息完成度，
 * 如果未完善资料则登录未完成，继续完善用户资料
 */
fun Context.checkLoginUserInfo(loginInfo: LoginWithOauthResult?, checkBack: (Boolean) -> Unit) {
    if (loginInfo != null) {
        // 注册登录时上报一次，选择完资料再上报一次
        spiceHolder().apiSpiceMgr.executeKt(MyInfoRequest(loginInfo.login_user_id, loginInfo.session_id), success = { result ->
            result.data?.info?.let { info ->
                info.abVer = tabbarConfig.abVer
                //AB 测版本
                GrowingUtil.uploadLoginInfo(info)
            }
        })
    }
    val back: (Boolean) -> Unit = {
        if (it) {
            /** 终于登录成功了 **/
            FIRST_LOGIN_KEY.putValue(false)
            JohnUser.getSharedUser().setLoginInfo(loginInfo)
            MeiUser.resetUser(spiceHolder().apiSpiceMgr, null)
            quickLoginIM()
            // 注册登录时上报一次，选择完资料再上报一次
            spiceHolder().apiSpiceMgr.executeKt(MyInfoRequest(), success = { result ->
                result.data?.info?.let { info ->
                    requestTabBar { tabBar ->
                        info.abVer = tabBar?.abVer
                        GrowingUtil.uploadLoginInfo(info)
                    }
                }
            })
        }
        checkBack(it)
    }
    when {
        loginInfo == null -> back(false) // 用户信息为空
        loginInfo.ifPerfect -> back(true)
        else -> {
            /** 没有完善用户资料则去完善资料 **/
            toInfoComplement(loginInfo.login_user_id, loginInfo.session_id) { success ->
                back(success)
            }
            /**生成id就上报一次*/
            GrowingUtil.track("registration_successful")
        }
    }
}