package com.mei.base.weight.span

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

import com.mei.orc.Cxt
import com.mei.wood.R

/**
 * Created by 杨强彪 on 2016/12/21.
 *
 * @描述： 不带下划线的超链
 */

class NoUnderlineSpan : ClickableSpan() {

    override fun onClick(widget: View) {

    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = Cxt.getColor(R.color.colorPrimary)
        ds.isUnderlineText = false
    }
}
