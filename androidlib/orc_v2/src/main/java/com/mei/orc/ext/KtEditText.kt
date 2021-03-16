package com.mei.orc.ext

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/12/19
 */

fun EditText.addTextChangedListener(afterTextChanged: (s: String) -> Unit = {}) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged(s?.toString().orEmpty())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}