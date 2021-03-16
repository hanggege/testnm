package com.mei

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.core.text.toSpannable
import androidx.fragment.app.FragmentActivity
import com.mei.base.ui.nav.Nav
import com.mei.orc.util.save.getValue
import com.mei.orc.util.save.putValue
import com.mei.wood.R
import com.mei.wood.dialog.DialogBack
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.buildLink
import com.net.model.chick.tab.tabbarConfig
import kotlinx.android.synthetic.main.dialog_privacy.view.*

/**
 *  Created by zzw on 2019-12-19
 *  Des:
 */


const val PRIVACY_KEY = "PRIVACY_KEY"


//检测是否点击过了
fun checkPrivacy(): Boolean = PRIVACY_KEY.getValue(false) ?: false

@SuppressLint("InflateParams", "SetTextI18n")
fun FragmentActivity.showPrivacyDialog(back: (Boolean) -> Unit = {}) {
    if (checkPrivacy() || tabbarConfig.show_privacy != 1) {//服务器配置不显示隐私弹框
        back(true)
        return
    }
    var isClick = false
    val dialog = NormalDialogLauncher.newInstance()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_privacy, null)
    view.apply {
        privacy_content1.text = "欢迎使用知心APP！在您使用知心产品或服务前，请您仔细阅读并充分理解《知心服务及隐私协议》；" +
                "当您点击“同意”并开始使用产品服务时，即表示您已阅读并同意条款内容，如果您不同意，将可能导致无法继续使用我们的产品及服务。"
        val s = "点击查看《知心服务及隐私协议》"
        privacy_content2.text = s.toSpannable().apply {
            buildLink("《知心服务及隐私协议》", Color.parseColor("#0073DC")) {
                Nav.toWebActivity(this@showPrivacyDialog, AmanLink.NetUrl.privacy_page, "服务及隐私协议")
            }
        }
        privacy_content2.movementMethod = LinkMovementMethod.getInstance()
        privacy_content2.highlightColor = Color.TRANSPARENT //设置点击后的颜色为透明

        privacy_bt.setOnClickListener {
            PRIVACY_KEY.putValue(true)
            isClick = true
            dialog.dismissAllowingStateLoss()
            back(true)
        }

        cancel_bt.setOnClickListener {
            isClick = true
            dialog.dismissAllowingStateLoss()
            back(false)
        }

    }


    dialog.showDialog(activity = this, data = DialogData(canceledOnTouchOutside = false, backCanceled = true, customView = view), back = object : DialogBack {
        override fun handle(state: Int) {
            if (!isClick) {
                back(false)
            }
        }
    })

}