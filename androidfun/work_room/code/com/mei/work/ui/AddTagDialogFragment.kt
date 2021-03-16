package com.mei.work.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.mei.me.ext.setEditTextInhibitInputSpeChat
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.ext.addTextChangedListener
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.keyboard.showKeyBoardDelay
import com.mei.wood.R
import kotlinx.android.synthetic.main.dialog_work_room_lable_add.*
import launcher.MakeResult

/**
 *
 * @author Created by lenna on 2020/7/23
 * 添加标签dialog
 */
@MakeResult(includeStartForResult = false)
class AddTagDialog : MeiSupportDialogFragment() {
    var back: (tagStr: String) -> Unit={}
    override fun onSetInflaterLayout(): Int = R.layout.dialog_work_room_lable_add

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setEditTextInhibitInputSpeChat(tag_et, 8)
        tag_et?.showKeyBoardDelay()
        tag_et?.isFocusable = true
        tag_et?.isFocusableInTouchMode = true
        tag_et?.requestFocus()
        tag_change_tv?.text = "${tag_et.text.length}/8"
        choice_view.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tag_et?.addTextChangedListener {
            tag_change_tv.text = "${it.length}/8"
        }

        dialog_add_tag_tv.setOnClickNotDoubleListener {
            back(tag_et.text.toString())
            dismissAllowingStateLoss()

        }
    }

}
fun FragmentActivity.showAddTagDialog(callBack: (tagStr:String) -> Unit = {}): AddTagDialog? {
    val dialog=AddTagDialogLauncher.newInstance().apply {
        back=callBack
    }
    dialog.show(supportFragmentManager,"AddTagDialog")
    return dialog
}