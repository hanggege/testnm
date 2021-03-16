package com.mei.wood.span

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/20
 */
class ClickSpan(@ColorInt val color: Int, val click: (view: View) -> Unit) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.color = color
        ds.isUnderlineText = false
    }

    override fun onClick(widget: View) {
        click(widget)
    }


}