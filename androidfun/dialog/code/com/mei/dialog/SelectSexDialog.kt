package com.mei.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.FragmentActivity
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.wood.R
import kotlinx.android.synthetic.main.view_select_sex.*
import kotlinx.android.synthetic.main.view_upload_header.dialog_sel_cancel

/**
 * author : Song Jian
 * date   : 2020/2/18
 * desc   : 性别选择对话框
 */
fun FragmentActivity.showSelectSexDialog(callBack: (type: Int, extra: String?) -> Unit = { _, _ -> }) {
    SelectSexDialog().apply {
        this.callBack = callBack
    }.show(supportFragmentManager, "SelectSexDialog")
}


class SelectSexDialog : BottomDialogFragment() {
    var callBack: (type: Int, extra: String?) -> Unit = { _, _ -> }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_select_sex, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //男
        dialog_sel_male.setOnClickListener {
            dismissAllowingStateLoss()
            callBack(1, "男")
        }
        //女
        dialog_sel_female.setOnClickListener {
            callBack(0, "女")
            dismissAllowingStateLoss()
        }
        dialog_sel_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

}