package com.mei.login.ext

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.keyboard.showKeyBoardDelay
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.network.chick.login.GetImgCodeTokenRequest
import com.net.network.chick.login.SendMobileCodeRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/6/18
 *
 * 验证码弹框通用处理
 * 4. 10402 -> 服务器要求走图片验证码流程
 * 5. 10401 -> 图片验证码校验文本输入错误
 * 6. 10403 -> 图片验证码校验失败，token失效。
 */


fun MeiCustomBarActivity.requestMobileCode(countryCode: String, phoneNo: String,
                                           success: () -> Unit,
                                           failure: (code: Int, msg: String) -> Unit = { _, _ -> }) {
    showLoading(true)
    apiSpiceMgr.executeKt(SendMobileCodeRequest(phoneNo.joinCountryCode(countryCode), ""), success = {
        when {
            it.isSuccess -> {
                UIToast.toast(this, "验证码已发送，请查收")
                success()
            }
            it.rtn == 10402 -> {//服务器要求走图片验证码流程
                showImageCodeDialog(countryCode, phoneNo, it.data.url.orEmpty(), it.data.token.orEmpty(), success, failure)
            }
            else -> {
                failure(it.rtn, it.errMsg)
            }
        }
    }, failure = {
        val msg = it?.currMessage.orEmpty()
        failure(-1, if (msg.isEmpty()) "网络出错,请重试" else msg)
    }, finish = { showLoading(false) })
}


/**
 * 验证码弹框
 */
private fun MeiCustomBarActivity.showImageCodeDialog(countryCode: String, phoneNo: String,
                                                     imgUrl: String, token: String,
                                                     success: () -> Unit,
                                                     failure: (code: Int, msg: String) -> Unit) {
    var isRequestDismiss = false
    var currImageUrl = imgUrl

    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var currToken = token
    val contentView = layoutInflaterKt(R.layout.dialog_image_code)
    val dialog = NormalDialogLauncher.newInstance().showDialog(this, DialogData(customView = contentView), back = {
        if (!isRequestDismiss) failure(-2, "取消")
    })
    val codeImage = contentView.findViewById<ImageView>(R.id.code_img)
    val codeError = contentView.findViewById<TextView>(R.id.code_err)
    val codeEdit = contentView.findViewById<EditText>(R.id.code_edit)
    contentView.findViewById<View>(R.id.code_root).setOnClickListener {
        apiSpiceMgr.executeToastKt(GetImgCodeTokenRequest(phoneNo.joinCountryCode(countryCode)), success = {
            if (it.data != null && !dialog.isDetached) {
                currImageUrl = it.data.url
                currToken = it.data.token
                loadErrBack(currImageUrl, codeImage) { succ -> codeError.isVisible = !succ }
            }
        })
    }
    loadErrBack(currImageUrl, codeImage) { succ -> codeError.isVisible = !succ }
    codeEdit.showKeyBoardDelay()
    codeEdit.addTextChangedListener { e ->
        val inputCode = e?.toString().orEmpty().trim()
        if (inputCode.length >= 4) {
            showLoading(true)
            apiSpiceMgr.executeKt(SendMobileCodeRequest(phoneNo.joinCountryCode(countryCode), inputCode), success = {
                when {
                    it.isSuccess -> {
                        isRequestDismiss = true
                        UIToast.toast(this, "验证码已发送，请查收")
                        dialog.dismissAllowingStateLoss()
                        success()
                    }
                    it.rtn == 10303 -> {// 手机号码被占用
                        isRequestDismiss = true
                        dialog.dismissAllowingStateLoss()
                        failure(it.rtn, it.errMsg)
                    }
                    else -> {
                        codeEdit.setText("")
                        loadErrBack(currImageUrl, codeImage) { succ -> codeError.isVisible = !succ }
                    }
                }
            }, failure = {
                val msg = it?.currMessage.orEmpty()
                failure(-1, if (msg.isEmpty()) "网络出错,请重试" else msg)
                UIToast.toast(this, msg)
            }, finish = { showLoading(false) })
        }

    }

}

