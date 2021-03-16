package com.mei.login.ext

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Created by hang on 2019/6/11.
 */

fun EditText.formatPhoneNo(back: (phone: String, index: Int) -> Unit) {
    addTextChangedListener(object : FormatPhoneWatcher(object : OnFormatPhoneCallback {
        override fun onFormatPhone(phone: String, index: Int) {
            back(phone, index)
        }
    }) {})
}

fun EditText.formatPhoneNo(formatTextWatcher: FormatPhoneWatcher) {
    addTextChangedListener(formatTextWatcher)
}

fun EditText.removeFormatPhoneNo(formatTextWatcher: FormatPhoneWatcher) {
    removeTextChangedListener(formatTextWatcher)
}

open class FormatPhoneWatcher(private val callback: OnFormatPhoneCallback?) : TextWatcher {


    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //手机号格式化xxx xxxx xxxx
        if (s == null || s.isEmpty()) return
        val sb = StringBuilder()
        for (i in s.indices) {
            if (i == 0 && s[0] == ' ' || i != 3 && i != 8 && s[i] == ' ') {
                continue
            } else {
                sb.append(s[i])
                if ((sb.length == 4 || sb.length == 9) && sb[sb.length - 1] != ' ') {
                    sb.insert(sb.length - 1, ' ')
                }
            }
        }
        if (sb.toString() != s.toString()) {
            var index = start + 1
            if (index < sb.length) {
                if (sb[start] == ' ') {
                    if (before == 0) {
                        index++
                    } else {
                        index--
                    }
                } else {
                    if (before == 1) {
                        index--
                    }
                }
                if (callback != null) {
                    if (index > sb.length) index = sb.length
                    callback.onFormatPhone(sb.toString(), index)
                }
            }
        }
    }

    interface OnFormatPhoneCallback {
        fun onFormatPhone(phone: String, index: Int)
    }
}