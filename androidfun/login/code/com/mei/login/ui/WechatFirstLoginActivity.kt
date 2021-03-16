package com.mei.login.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.core.view.updatePadding
import com.joker.TdManager
import com.joker.TdType
import com.joker.connect.TdCallBack
import com.joker.model.BackResult
import com.joker.model.ErrResult
import com.mei.base.ui.nav.Nav
import com.mei.login.*
import com.mei.login.ext.queryRegistered
import com.mei.orc.common.CommonConstant
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.ext.lightMode
import com.mei.orc.ext.transparentStatusBar
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.clearDoubleCheck
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.player.view.IgnorePlayerBar
import com.mei.wood.R
import com.mei.wood.dialog.checkAppUpgrade
import com.mei.wood.ext.AmanLink
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.chick.tab.LoginType
import com.net.model.chick.tab.tabbarConfig
import kotlinx.android.synthetic.main.activity_wechat_first_login.*

/**
 * author : Song Jian
 * date   : 2020/2/12
 * desc   : 微信登录优先
 */

class WechatFirstLoginActivity : MeiCustomActivity(), IgnorePlayerBar {

    override fun customStatusBar(): Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wechat_first_login)
        transparentStatusBar()
        lightMode()

        root_layout.updatePadding(top = getStatusBarHeight())
        login_title_close.apply {
            isInvisible = checkFirstLogin() && !tabbarConfig.guestEnable
            setOnClickNotDoubleListener(2000) {
                onBackPressed()
            }
        }

        login_agreement.setOnClickListener {
            Nav.toAmanLink(this, AmanLink.NetUrl.chat_artifact_agreement)
        }


        other_login_by_wx.setOnClickNotDoubleListener(2000, "wechatLogin") {
            clearDoubleCheck("LoginTag")
            //微信登录
            TdManager.performLogin(this, TdType.weixin, object : TdCallBack {
                override fun onSuccess(success: BackResult) {
                    other_login_by_wx.isClickable = false
                    other_login_by_phone.isClickable = false
                    queryRegistered(success, success = { loginInfo ->
                        checkLoginUserInfo(loginInfo) { success ->
                            if (success) {
                                setResult(Activity.RESULT_OK, Intent().apply {
                                    putExtra(LOGIN_STATE_KEY, success)
                                    putExtra(LOGIN_REGISTER_KEY, if (success) loginInfo?.isReg == 1 else false)
                                })
                                finish()
                            } else {
                                other_login_by_wx.isClickable = true
                                other_login_by_phone.isClickable = true
                                UIToast.toast(this@WechatFirstLoginActivity, "完善信息失败，请重试")
                            }
                        }
                    }, failure = { code, msg ->
                        other_login_by_wx.isClickable = true
                        other_login_by_phone.isClickable = true
                        if (!JohnUser.getSharedUser().hasLogin()
                                && !msg.contains("code been used")
                                && code != CommonConstant.Config.LOGIN_BASE_CODE_BAD_SESSION) {
                            UIToast.toast(this@WechatFirstLoginActivity, if (msg.isNotEmpty()) msg else "登录失败，请重试")
                        }
                    })
                }

                override fun onFailure(errResult: ErrResult) {
                    UIToast.toast(errResult.msg)
                }
            })
        }

        other_login_by_phone.setOnClickNotDoubleListener(1000, "wechatLogin") {
            clearDoubleCheck("LoginTag")
            val back: (Boolean, Boolean) -> Unit = { loginSucess, isReg ->
                other_login_by_phone.isClickable = true
                if (loginSucess) {
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra(LOGIN_STATE_KEY, loginSucess)
                        putExtra(LOGIN_REGISTER_KEY, if (loginSucess) isReg else false)
                    })
                    finish()
                }
            }
            val openSuccess = if (tabbarConfig.loginConfig?.first == LoginType.login_type_wsp) {
                toLoginByShanYan(back) {
                    other_login_by_phone.isClickable = true
                }
            } else {
                toLoginByPhoneNo(back = back)
            }
            if (openSuccess) other_login_by_phone.isClickable = false
        }
    }


    override fun onResume() {
        super.onResume()
        checkAppUpgrade()
    }

    override fun onBackPressed() {
        if (!checkFirstLogin() || tabbarConfig.guestEnable) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}

