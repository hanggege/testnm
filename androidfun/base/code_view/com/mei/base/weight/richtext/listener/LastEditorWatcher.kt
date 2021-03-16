package com.mei.base.weight.richtext.listener

import android.widget.EditText


/**
 *
 * @author Created by Ling on 2019-07-08
 * 当前获取焦点的文本编辑器改变的时候回调
 */
interface LastEditorWatcher {
    fun onLastEditor(editText: EditText)
}