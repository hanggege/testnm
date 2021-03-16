package com.mei.im.ui.dialog

import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.mei.orc.ext.layoutInflaterKt
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialog
import com.mei.wood.dialog.NormalDialogLauncher

/**
 *
 * @author Created by lenna on 2020/10/27
 */

/**
 * 一个按钮普通弹框
 */
fun FragmentActivity.showOneBtnCommonDialog(content: String = ""
                                            , btnText: String = "知道了"
                                            , canceledOnTouchOutside: Boolean = true
                                            , okBack: () -> Unit = {}): NormalDialog {
    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.view_shielding_dialog)
    contentView.findViewById<TextView>(R.id.shield_hint_dialog_content).apply {
        text = content
        isVisible = content.isNotEmpty()
    }

    contentView.findViewById<TextView>(R.id.shield_hint_dialog_confirm_btn).apply {
        text = btnText
        setOnClickListener {
            dialog.dismiss()
            okBack()
        }
    }
    dialog.showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = canceledOnTouchOutside, backCanceled = false), okBack = {})
    return dialog
}