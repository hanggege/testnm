package com.mei.login.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.view.isVisible
import com.gyf.immersionbar.ImmersionBar
import com.mei.live.action.IgnoreSystemAction
import com.mei.login.LOGIN_REGISTER_KEY
import com.mei.login.LOGIN_STATE_KEY
import com.mei.login.checkFirstLogin
import com.mei.login.checkLoginUserInfo
import com.mei.login.ext.formatPhoneNo
import com.mei.login.ext.joinCountryCode
import com.mei.login.ext.requestMobileCode
import com.mei.login.ext.showForbiddenDialog
import com.mei.orc.callback.Callback
import com.mei.orc.common.CommonConstant
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.encrypt.DigestUtils
import com.mei.orc.util.keyboard.showKeyBoardDelay
import com.mei.orc.util.save.getStringMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.player.view.IgnorePlayerBar
import com.mei.wood.BuildConfig
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.showCountryCode
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.chick.login.LoginWithOauthResult
import com.net.model.chick.tab.LoginType
import com.net.model.chick.tab.tabbarConfig
import com.net.network.chick.login.GetCheckSumRequest
import com.net.network.chick.login.UseMobileCodeLoginRequest
import com.net.network.chick.login.UserAccountPasswordLoginRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login_by_phone.*
import java.util.concurrent.TimeUnit

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/6/18
 */
class LoginByPhoneActivity : MeiCustomActivity(), IgnorePlayerBar, IgnoreSystemAction {

    private var timeAccount = -1

    override fun customStatusBar(): Boolean = true

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_by_phone)
        initView()
        ImmersionBar.with(this).apply {
            statusBarDarkFont(true)
            statusBarColorInt(Color.WHITE)
            transparentStatusBar()
            fitsSystemWindows(true)
        }.init()

        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribeBy {
                    if (timeAccount > 0) {
                        login_get_code.isEnabled = false
                        login_get_code.text = "${timeAccount}s"
                        timeAccount--
                    } else {
                        login_get_code.isEnabled = true
                        login_get_code.text = "获取验证码"
                    }
                }

    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun initView() {
        login_title_close.apply {
            isVisible = !(checkFirstLogin() && LoginType.login_type_p == tabbarConfig.loginConfig?.first && !tabbarConfig.guestEnable)
            setOnClickListener { onBackPressed() }
        }
        login_phone_country.setOnClickListener { showCountryCode(Callback { code -> login_phone_country_tv.text = code }) }
        et_phone.formatPhoneNo { phone, index ->
            et_phone.setText(phone)
            et_phone.setSelection(index)
        }
        login_get_code.setOnClickListener {
            login_get_code.isEnabled = false
            val phoneNo = et_phone.text?.toString().orEmpty().replace(" ", "")
            if (phoneNo.isEmpty()) {
                UIToast.toast(this, "请输入手机号")
            } else {
                requestMobileCode(login_phone_country_tv.text?.toString().orEmpty(), phoneNo, success = {
                    // 获取验证码成功，进入60s倒计时
                    timeAccount = 60
                    login_verification_code.requestFocus()
                    login_verification_code.showKeyBoardDelay()
                }, failure = { _, msg ->
                    // 获取验证码失败
                    UIToast.toast(msg)
                    login_get_code.isEnabled = true
                })
            }
        }
        if (BuildConfig.DEBUG) {
            et_phone.setText("last_login_key".getStringMMKV())
        }
        other_login_by_phone.setOnClickNotDoubleListener(1500) {
            checkAccount()
        }
        et_phone.showKeyBoardDelay()
    }

    /**
     * 检查用户类别，准备登录
     */
    private fun checkAccount() {
        val phoneNo = et_phone.text?.toString().orEmpty().replace(" ", "")
        val phoneCode = login_verification_code.text?.toString().orEmpty().replace(" ", "")
        when {
            phoneNo.isEmpty() -> UIToast.toast(this, "请输入手机号")
            phoneCode.isEmpty() -> UIToast.toast(this, "请输入验证码")
            else -> {
                "last_login_key".putMMKV(phoneNo)
                showLoading(true)
                other_login_by_phone.isClickable = false
                //检测是否是特殊账号
                apiSpiceMgr.executeKt(GetCheckSumRequest(phoneNo), success = {
                    when {
                        it.isSuccess -> {
                            if (it.data?.ifSpecial == true && it.data?.checksum.orEmpty().length > 4) {
                                //测试账号登录
                                loginByPWD(it.data?.checksum.orEmpty())
                            } else {
                                //手机验证码登录
                                loginByMobileCode()
                            }
                        }
                        it.rtn == CommonConstant.Config.LOGIN_BASE_CODE_BAD_SESSION -> {
                            showForbiddenDialog(it.errMsg)
                        }
                        it.rtn == CommonConstant.Config.BASE_CODE_BAD_SESSION -> {
                            //MainActionReceiver做了拦截 这里不处理
                        }
                        else -> {
                            UIToast.toast(this, it.errMsg)
                        }
                    }
                }, failure = {
                    if (!JohnUser.getSharedUser().hasLogin()) {
                        UIToast.toast(this, "登录失败，请重试")
                    }
                }, finish = {
                    other_login_by_phone.isClickable = true
                    showLoading(false)
                })
            }
        }
    }

    /**
     * 手机验证码登录
     */
    private fun loginByMobileCode() {
        other_login_by_phone.isClickable = false
        showLoading(true)
        val phoneNo = et_phone.text?.toString().orEmpty().replace(" ", "")
        val phoneCode = login_verification_code.text?.toString().orEmpty().replace(" ", "")
        apiSpiceMgr.executeKt(UseMobileCodeLoginRequest(phoneNo.joinCountryCode(login_phone_country_tv.text?.toString().orEmpty()), phoneCode), success = {
            if (it.isSuccess) {
                checkUserInfoComplete(it.data)
            } else {
                UIToast.toast(this, it.errMsg)
            }
        }, failure = {
            if (!JohnUser.getSharedUser().hasLogin()) {
                UIToast.toast(this, "登录失败，请重试")
            }
        }, finish = {
            other_login_by_phone.isClickable = true
            showLoading(false)
        })
    }

    private fun loginByPWD(checkSum: String) {
        other_login_by_phone.isClickable = false
        showLoading(true)
        val phoneNo = et_phone.text?.toString().orEmpty().replace(" ", "")
        val password = login_verification_code.text?.toString().orEmpty().replace(" ", "")
        val md5Code = DigestUtils.md5Hex(checkSum + DigestUtils.md5Hex(checkSum.substring(0, 4) + DigestUtils.md5Hex("(\$*ngr^@%$password")))
        apiSpiceMgr.executeKt(UserAccountPasswordLoginRequest(phoneNo, md5Code, checkSum), success = {
            if (it.isSuccess) {
                checkUserInfoComplete(it.data)
            } else {
                UIToast.toast(this, it.errMsg)
            }
        }, failure = {
            if (!JohnUser.getSharedUser().hasLogin()) {
                UIToast.toast(this, "登录失败，请重试")
            }
        }, finish = {
            other_login_by_phone.isClickable = true
            showLoading(false)
        })
    }


    /**
     * 检查用户信息是否完善
     */
    private fun checkUserInfoComplete(oauth: LoginWithOauthResult) {
        checkLoginUserInfo(oauth) { success ->
            if (success) {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(LOGIN_STATE_KEY, success)
                    putExtra(LOGIN_REGISTER_KEY, if (success) oauth.isReg == 1 else false)
                })
                finish()
            } else {
                UIToast.toast(this, "完善信息失败，请重试")
            }

        }
    }


}