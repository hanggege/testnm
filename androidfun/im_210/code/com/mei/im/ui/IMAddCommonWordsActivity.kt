package com.mei.im.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.mei.im.ADD_WORDS_KEY
import com.mei.orc.Cxt
import com.mei.orc.ext.addTextChangedListener
import com.mei.orc.ui.toast.UIToast
import com.mei.widget.actionbar.defaultRightTextView
import com.mei.wood.R
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.network.chat.CommandPhraseAddRequest
import com.net.network.chat.CommandPhraseModifyRequest
import kotlinx.android.synthetic.main.im_activity_add_common_words.*
import launcher.Boom
import launcher.MulField

/**
 * 添加常用语
 */
class IMAddCommonWordsActivity : MeiCustomBarActivity() {
    @MulField
    @Boom
    var paramPostion: Int = -1//输入框内容

    @MulField
    @Boom
    var wordList = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IMAddCommonWordsActivityLauncher.bind(this)
        setContentView(R.layout.im_activity_add_common_words)
        showBottomLine(false)
        initView()
    }

    /**
     * 初始化UI
     */
    private fun initView() {
        title = if (paramPostion >= 0) {
            Cxt.getStr(R.string.edit_command_phrase)
        } else {
            Cxt.getStr(R.string.add_command_phrase)

        }
        setCustomView(defaultRightTextView(getString(R.string.complete), Cxt.getColor(R.color.color_333333)), 1)
        meiTitleBar.rightContainer.setOnClickListener {
            if (paramPostion >= 0 && wordList.isNotEmpty()) {
                //判断编辑内容是否跟传过来的内容一致
                if (et_input.text.toString().trim().isEmpty()) {
                    UIToast.toast("没有填写内容")
                    return@setOnClickListener
                }
                if (wordList[paramPostion] != et_input.text.toString()) {
                    updatePhraseList()
                } else {
                    finish()
                }
            } else {
                //新增页面
                if (et_input.text.toString().trim().isEmpty()) {
                    UIToast.toast("没有填写内容")
                } else {
                    addCommonWords(et_input.text.toString().trim())
                }
            }

        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        et_input.requestFocus()
        if (paramPostion >= 0 && wordList.size > paramPostion) {
            et_input.setText(wordList[paramPostion])
        }


        et_input.setSelection(et_input.text.length)
        tv_count_words.text = et_input.text.length.toString()
        et_input.addTextChangedListener {
            tv_count_words.text = it.length.toString()
        }
    }

    // 修改常用于
    fun updatePhraseList() {
        showLoading(true)
        wordList[paramPostion] = et_input.text.toString()
        apiSpiceMgr.executeKt(CommandPhraseModifyRequest(wordList),
                success = {
                    if (it.isSuccess) {
                        UIToast.toast("修改完成")
                        setResult(Activity.RESULT_OK, Intent().apply {
                            putStringArrayListExtra(ADD_WORDS_KEY, wordList)

                        })
                        finish()
                    }
                }, finish = {
            showLoading(false)
        })
    }

    //添加常用语言
    private fun addCommonWords(words: String) {
        apiSpiceMgr.executeKt(CommandPhraseAddRequest(words), success = {
            if (it.data != null && it.data.chatPhrases.isNotEmpty()) {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putStringArrayListExtra(ADD_WORDS_KEY, it.data.chatPhrases)
                })
                UIToast.toast("添加成功")
            } else {
                UIToast.toast(it?.msg)
            }
            finish()
        }, failure = {
            UIToast.toast(it?.currMessage)
            finish()
        })
    }

    /**
     * 左边返回按钮事件
     */
    override fun onClickLeft(view: View) {
        if (paramPostion >= 0) {
            //输入框不能为空
            if (et_input.text.toString().isEmpty()) {
                //内容为空
                UIToast.toast("没有填写内容")
                return
            }
            //编辑内容跟传的参数是否相等，相等直接退出返回
            if (wordList[paramPostion] != et_input.text.toString()) {
                //不相等就把修改的值返回回去
                NormalDialogLauncher.newInstance().showDialog(this, "修改的内容未保存，是否确定退出", back = {
                    when (it) {
                        DISSMISS_DO_OK -> {
                            finish()
                        }
                    }
                })
            } else {
                finish()
            }

        } else {
            //新增页面
            //判断输入文本是否为空
            if (et_input.text.toString().trim().isEmpty()) {
                finish()
                return
            }
            //不为弹窗提示保存
            NormalDialogLauncher.newInstance().showDialog(this, "修改的内容未保存，是否确定退出", back = {
                when (it) {
                    DISSMISS_DO_OK -> finish()
                }
            })
        }
    }
}
