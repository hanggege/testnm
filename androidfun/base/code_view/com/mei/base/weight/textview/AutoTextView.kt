package com.mei.base.weight.textview

import android.content.Context
import android.text.DynamicLayout
import android.text.Layout
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/14
 */
class AutoTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatTextView(context, attrs) {

    private var mEmptyWidth = 150//空白文本宽度

    fun setEndWidth(width: Int): AutoTextView {
        mEmptyWidth = width
        isVisible
        return this
    }

    /**
     * [DynamicLayout.Builder]中部分方法要API28以上才能生效，故没改
     */
    override fun setText(text: CharSequence, type: BufferType) {
        @Suppress("DEPRECATION")
        val dLayout = DynamicLayout(text, paint, measuredWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, false)
        if (dLayout.lineCount >= 2 && measuredWidth > 0) {
            val availableTextWidth = maxLines * (measuredWidth - paddingLeft - paddingRight) - mEmptyWidth
            val txtResult = TextUtils.ellipsize(text, paint, availableTextWidth.toFloat(), TextUtils.TruncateAt.END).toString()
            super.setText(txtResult, type)
        } else
            super.setText(text, type)
    }

}
