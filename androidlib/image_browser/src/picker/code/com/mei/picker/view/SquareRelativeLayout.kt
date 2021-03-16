package com.mei.picker.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-19
 */
class SquareRelativeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}