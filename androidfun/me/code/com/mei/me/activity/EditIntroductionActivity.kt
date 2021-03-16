package com.mei.me.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.mei.orc.ActivityLauncher
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomBarActivity
import kotlinx.android.synthetic.main.im_activity_edit_instroduction.*
import launcher.Boom
import launcher.MulField

const val RESULT_EIDT_INFO = "RESULT_EIDT_INFO"

/**
 * 信息编辑
 */
class EditIntroductionActivity : MeiCustomBarActivity() {
    @MulField
    @Boom
    var paramOne: String = ""

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.im_activity_edit_instroduction)
        ActivityLauncher.bind(this)
        et_input.setText(paramOne)
        title = "编辑个人简介"
        tv_personal_lable_change.text = "${et_input.text.length}/800"
        et_input.addTextChangedListener {
            tv_personal_lable_change.text = "${it.toString().length}/800"
        }
        et_input.setSelection(et_input.text.length)

    }

    override fun onClickLeft(view: View) {
        saveData()
    }

    override fun onBackPressed() {
        saveData()
    }

    /**
     * 保存数据
     */
    private fun saveData() {
        if (et_input.text.trim().isEmpty()) {
            UIToast.toast("个人简介不能为空")
        } else {
            if (et_input.text.toString() != paramOne) {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(RESULT_EIDT_INFO, et_input.text.toString())
                })
                UIToast.toast("编辑已保存")
            }
            finish()
        }
    }
}