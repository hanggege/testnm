package com.mei.base.weight.richtext.listener

import android.text.Editable

/**
 *
 * @author Created by Ling on 2019-07-08
 * 文本变动回调
 */
interface RichTextWatcher {

    fun onTextChanged(richTxt: String)

    fun afterTextChanged(s: Editable)

    fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
}