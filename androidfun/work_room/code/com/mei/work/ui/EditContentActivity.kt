package com.mei.work.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mei.orc.ActivityLauncher
import com.mei.orc.ext.addTextChangedListener
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomBarActivity
import kotlinx.android.synthetic.main.activity_edit_content.*
import launcher.Boom
import launcher.MulField

/**
 *
 * @author Created by lenna on 2020/7/23
 * 编辑内容公共页面并返回编辑内容
 */
const val RESULT_EDIT_CONTENT = "RESULT_EDIT_CONTENT"

class EditContentActivity : MeiCustomBarActivity() {
    @MulField
    @Boom
    var content: String = ""

    @MulField
    @Boom
    var mTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_content)
        ActivityLauncher.bind(this)
        title = "编辑$mTitle"
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        content_et.setText(content)
        content_et.hint = "请输入$mTitle"
        count_content_length_tv.text = "${content_et.text.length}/800"
        content_et.addTextChangedListener {
            count_content_length_tv.text = "${it.length}/800"
        }
        content_et.setSelection(content_et.text.length)
    }

    override fun onClickLeft(view: View) {
        saveContent()
    }

    override fun onBackPressed() {
        saveContent()
    }

    private fun saveContent() {
        if (content_et.text.trim().isEmpty()) {
            UIToast.toast("${mTitle}不能为空")
        } else {
            if (content_et.text.toString() != content) {
                val intent = Intent().apply {
                    putExtra(RESULT_EDIT_CONTENT, content_et.text.toString())
                }
                setResult(Activity.RESULT_OK, intent)
                UIToast.toast("编辑已保存")
            }
            finish()
        }
    }

}