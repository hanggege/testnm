package com.mei.red_packet.view

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.ext.px2dip
import com.mei.orc.ext.screenWidth
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil

/**
 *
 * @author Created by lenna on 2020/7/24
 * 运用闪验绑定手机号定制
 */
private class ShanYanBindPhoneView(context: Context, val bindBack: (Boolean) -> Unit) : FrameLayout(context) {
    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutInflaterKtToParentAttach(R.layout.shanyan_bind_view)
    }
}

fun Context.provideShanYanBindPhoneConfig(back: (Boolean) -> Unit): ShanYanUIConfig {
    val customView = ShanYanBindPhoneView(this, back)
    val backBitmap = ContextCompat.getDrawable(this@provideShanYanBindPhoneConfig, R.drawable.icon_close_white_bg)
    val bindBitmap = ContextCompat.getDrawable(this@provideShanYanBindPhoneConfig, R.drawable.bg_bind_phone_btn_normal)
    val bgBitmap = ContextCompat.getDrawable(this@provideShanYanBindPhoneConfig, R.drawable.bg_bind_phone)
    return ShanYanUIConfig.Builder().apply {
        setDialogTheme(true, px2dip(screenWidth).toInt(), 285, 0, 0, true) //弹框样式
        setDialogDimAmount(0.6f)
                .setAuthBGImgPath(bgBitmap)


        //顶部标题栏
        setNavColor(Color.WHITE)
                .setNavText("")
                .setNavTextColor(Color.BLACK)
                .setNavReturnImgPath(backBitmap)
                .setNavReturnBtnWidth(12)
                .setNavReturnBtnHeight(12)
                .setNavReturnBtnOffsetRightX(20)
        //logo
        setLogoHidden(true)


        //slogan
        setSloganHidden(true).setShanYanSloganHidden(true)
        //号码栏
        setNumberColor(Color.WHITE)
                .setNumFieldOffsetBottomY(142)
                .setNumberSize(22)
                .setNumberBold(true)


        //领取按钮
        setLogBtnText("一键领取")
                .setLogBtnTextColor(Color.parseColor("#904717"))
                .setLogBtnTextSize(16)
                .setLogBtnImgPath(bindBitmap)
                .setLogBtnHeight(50)
                .setLogBtnWidth(px2dip(screenWidth).toInt() - 40)
                .setLogBtnOffsetBottomY(60)
                .setLogBtnTextBold(true)
        // 底部协议
        setCheckBoxHidden(true)
                .setPrivacyText("绑定即已阅读并同意", "和", "、", "、", "")
                .setPrivacyOffsetBottomY(22)
                .setPrivacyOffsetX(26)
                .setPrivacyGravityHorizontalCenter(true)
                .setAppPrivacyOne("知心服务及隐私协议", MeiUtil.appendGeneraUrl(AmanLink.NetUrl.chat_artifact_agreement))
                .setAppPrivacyColor(Color.parseColor("#7DFFFFFF"), Color.parseColor("#E3FFFFFF"))  //参数1：基础文字颜色  参数2 :  协议文字颜色

        addCustomView(customView, false, true, null)
    }.build()
}