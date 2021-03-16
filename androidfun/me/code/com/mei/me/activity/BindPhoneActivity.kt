package com.mei.me.activity

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import com.gyf.immersionbar.ImmersionBar
import com.mei.login.ext.formatPhoneNo
import com.mei.login.ext.requestMobileCode
import com.mei.orc.callback.Callback
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.keyboard.showKeyBoardDelay
import com.mei.orc.util.startactivity.startFragmentForResult
import com.mei.wood.R
import com.mei.wood.extensions.showCountryCode
import com.mei.wood.ui.MeiCustomActivity
import kotlinx.android.synthetic.main.activity_bind_phone.*

/**
 * author : Song Jian
 * date   : 2020/1/18
 * desc   : 绑定手机号
 */

class BindPhoneActivity : MeiCustomActivity() {

    override fun customStatusBar(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_phone)
        ImmersionBar.with(this).apply {
            statusBarDarkFont(true)
            statusBarColorInt(Color.WHITE)
            transparentStatusBar()
            statusBarView(status_bar_view)
        }.init()
        initListener()
    }


    private fun initListener() {
        bind_phone_title_close.setOnClickListener { onBackPressed() }
        login_phone_country.setOnClickListener { showCountryCode(Callback { code -> login_phone_country_tv.text = code }) }
        //进入页面拉起键盘
        et_phone.requestFocus()
        et_phone.showKeyBoardDelay()
        et_phone.formatPhoneNo { phone, index ->
            et_phone.setText(phone)
            et_phone.setSelection(index)
        }
        bind_phone_get_code.setOnClickListener {
            val phone = et_phone.text.trim().toString().replace(" ", "")
            if (TextUtils.isEmpty(phone)) {
                UIToast.toast("请输入手机号")
                return@setOnClickListener
            }
            bind_phone_get_code.isEnabled = false
            requestMobileCode(login_phone_country_tv.text?.toString().orEmpty(), phone, success = {
                bind_phone_get_code.isEnabled = true
                gotoCodePage()
            }, failure = { _, msg ->
                UIToast.toast(msg)
                bind_phone_get_code.isEnabled = true
            })
        }
    }

    private fun gotoCodePage() {
        val phone = et_phone.text.trim().toString().replace(" ", "")
        val countryCode = login_phone_country_tv.text?.toString().orEmpty()
        startFragmentForResult(BindPhoneCodeActivityLauncher.getIntentFrom(this, phone, countryCode), 120)
        { code, _ ->
            if (code == 120) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

}