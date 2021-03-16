package com.mei.me.activity

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar
import com.mei.base.common.BIND_PHONE_ACTION
import com.mei.login.ext.joinCountryCode
import com.mei.login.view.VerificationAction
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.http.exception.RxThrowable
import com.mei.orc.http.listener.RequestListener
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.keyboard.hideKeyBoard
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.net.MeiUser
import com.net.model.chick.user.ChickUserInfo
import com.net.network.chick.user.UpdatePhoneRequest
import kotlinx.android.synthetic.main.bind_phone_code_layout.*
import launcher.Boom

/**
 * author : Song Jian
 * date   : 2020/2/20
 * desc   : 绑定手机 验证码输入
 */


class BindPhoneCodeActivity : MeiCustomActivity() {

    @Boom
    var phoneNo = ""

    @Boom
    var countryCode = ""

    override fun customStatusBar(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityLauncher.bind(this)
        setContentView(R.layout.bind_phone_code_layout)
        if (phoneNo.isEmpty() || countryCode.isEmpty()) {
            finish()
            return
        }
        ImmersionBar.with(this).apply {
            statusBarDarkFont(true)
            statusBarColorInt(Color.WHITE)
            transparentStatusBar()
            statusBarView(status_bar_view)
        }.init()

        login_title_close.setOnClickListener { finish() }
        login_verify_code.setOnVerificationCodeChangedListener(object : VerificationAction.OnVerificationCodeChangedListener {
            override fun onVerCodeChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onInputCompleted(s: CharSequence?) {
                showLoading(true)
                apiSpiceMgr.executeToastKt(UpdatePhoneRequest(phoneNo.joinCountryCode(countryCode), login_verify_code.text.toString(),0), success = {
                    if (it.isSuccess) {
                        //成功也要提示

                        UIToast.toast(it.msg)
                        it.data?.let { data ->
                            //重置session_id
                            JohnUser.getSharedUser().sessionID = data.session_id
                            MeiUser.resetUser(apiSpiceMgr, object :RequestListener<ChickUserInfo.Response>{
                                override fun onRequestFailure(retrofitThrowable: RxThrowable?) {

                                }

                                override fun onRequestSuccess(result: ChickUserInfo.Response) {
                                    postAction(BIND_PHONE_ACTION)
                                }

                            })
                        }
                        hideKeyBoard()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }, finish = {
                    showLoading(false)
                })
            }

        })
        login_verify_code.postDelayed({
            login_verify_code?.requestFocus()
            login_verify_code?.showKeyBoard(this)
        }, 1000)
    }
}

