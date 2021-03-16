package com.mei.login.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig
import com.joker.TdManager
import com.joker.TdType
import com.joker.connect.TdCallBack
import com.joker.model.BackResult
import com.joker.model.ErrResult
import com.mei.login.checkFirstLogin
import com.mei.login.checkLoginUserInfo
import com.mei.login.ext.queryRegistered
import com.mei.login.finishShanYanAuth
import com.mei.login.toLoginByPhoneNo
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.ext.px2dip
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.ext.screenWidth
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.clearDoubleCheck
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.AppManager
import com.mei.wood.util.MeiUtil
import com.net.model.chick.tab.LoginType
import com.net.model.chick.tab.tabbarConfig
import kotlinx.android.synthetic.main.shanyan_login_layout.view.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/6/16
 */

fun Context.provideShanYanConfig(back: (Boolean, isReg: Boolean) -> Unit): ShanYanUIConfig {
    val maskView = ShanYanLoginView(this, back)
    val backBitmap = ContextCompat.getDrawable(this@provideShanYanConfig, R.drawable.icon_close_with_bg_white)
    val loginBitmap = ContextCompat.getDrawable(this@provideShanYanConfig, R.drawable.bg_login_btn_normal)
    return ShanYanUIConfig.Builder().apply {
        //背景色
        setAuthBGImgPath(ColorDrawable(Color.WHITE))
        //导航栏
        setNavText("")
                .setNavColor(Color.WHITE)
                .setNavReturnImgPath(backBitmap)
        if (checkFirstLogin() && LoginType.login_type_swp == tabbarConfig.loginConfig?.first && !tabbarConfig.guestEnable) {
            //第一次登录 不显示返回按钮
            setNavReturnImgHidden(true)
        }
        //中间logo影藏 自定义logo
        setLogoHidden(true)

        //号码栏
        setNumberColor(Color.parseColor("#333333"))
                .setNumberSize(20)
                .setNumberBold(true)
                .setNumFieldOffsetBottomY(256)

        //slogan
        setSloganHidden(true).setShanYanSloganHidden(true)

        //登录按钮
        setLogBtnText("本机号码一键登录")
                .setLogBtnTextColor(Color.parseColor("#ffffff"))
                .setLogBtnTextSize(16)
                .setLogBtnHeight(50)
                .setLogBtnImgPath(loginBitmap)
                .setLogBtnOffsetBottomY(181)
                .setLogBtnWidth(px2dip(screenWidth).toInt() - 40)
        // 底部协议
        setCheckBoxHidden(true)
                .setPrivacyText("登录即同意", "和", "、", "、", "")
                .setPrivacyOffsetBottomY(20)
                .setPrivacyOffsetX(26)
                .setPrivacyGravityHorizontalCenter(true)
                .setAppPrivacyOne("知心服务及隐私协议", MeiUtil.appendGeneraUrl(AmanLink.NetUrl.chat_artifact_agreement))
                .setAppPrivacyColor(Color.parseColor("#B5B5B5"), Color.parseColor("#797979"))  //参数1：基础文字颜色  参数2 :  协议文字颜色

        addCustomView(maskView, false, false, null)

    }.build()
}

@SuppressLint("ViewConstructor")
private class ShanYanLoginView(context: Context, val loginBack: (Boolean, isReg: Boolean) -> Unit) : FrameLayout(context) {

    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutInflaterKtToParentAttach(R.layout.shanyan_login_layout)
        login_by_phone.setOnClickNotDoubleListener(tag = "oneKeyPhoneLogin", duration = 2000) {
            clearDoubleCheck("LoginTag")
            AppManager.getInstance().currentActivity().toLoginByPhoneNo { success, isReg ->
                if (success) {
                    loginBack(success, isReg)
                    finishShanYanAuth()
                }

            }
        }
        login_by_wechat.setOnClickNotDoubleListener(tag = "oneKeyLogin", duration = 2000) {
            weChatLogin()
        }
    }


    private fun weChatLogin() {
        with(AppManager.getInstance().currentActivity()) {
            //微信登录
            TdManager.performLogin(this, TdType.weixin, object : TdCallBack {
                override fun onSuccess(success: BackResult) {
                    login_by_phone.isEnabled = false
                    login_by_wechat.isEnabled = false
                    queryRegistered(success, success = { loginInfo ->
                        checkLoginUserInfo(loginInfo) { success ->
                            if (success) {
                                loginBack(success, if (success) loginInfo?.isReg == 1 else false)
                                finishShanYanAuth()
                            } else {
                                login_by_phone.isEnabled = true
                                login_by_wechat.isEnabled = true
                                UIToast.toast(this@with, "完善信息失败，请重试")
                            }
                        }
                    }, failure = { _, msg ->
                        runDelayedOnUiThread(800) {
                            try {
                                login_by_phone.isEnabled = true
                                login_by_wechat.isEnabled = true
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (!JohnUser.getSharedUser().hasLogin() && !msg.contains("code been used")) {
                            UIToast.toast(this@with, if (msg.isNotEmpty()) msg else "登录失败，请重试")
                        }
                    })
                }

                override fun onFailure(errResult: ErrResult) {
                    UIToast.toast(errResult.msg)
                }
            })
        }

    }
}