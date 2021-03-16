package com.mei.base.weight.richtext.delegate

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.mei.orc.ext.dip
import com.mei.wood.R

/**
 *
 * @author Created by Ling on 2019-07-05
 * 富文本的xml配置
 */
class RichTextDelegate(var view: View, var context: Context, var attrs: AttributeSet?, defStyleAttr: Int) {

    var hint: CharSequence? = ""
    var mHintColor: ColorStateList? = null
    var mTextColor: ColorStateList? = null
    var mTextSize = context.dip(16)
    var mMarginLeft = context.dip(15)
    var mMarginRight = context.dip(15)
    var mTextMarginTop = context.dip(15)
    var mTextMarginBottom = context.dip(20)
    var mImageMarginTop = context.dip(10)
    var mImageMarginBottom = context.dip(15)
    var mLineSpacingMultiplier = 1.2f

    init {
        obtainAttributes(context, attrs, defStyleAttr)
    }

    private fun obtainAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.RichTextEditor, defStyleAttr, 0)
            hint = a.getText(R.styleable.RichTextEditor_rich_text_hint)
            mHintColor = a.getColorStateList(R.styleable.RichTextEditor_rich_text_hint_color)
            if (mHintColor == null) {
                mHintColor = ColorStateList.valueOf(Color.parseColor("#C8C8C8"))
            }
            mTextColor = a.getColorStateList(R.styleable.RichTextEditor_rich_text_color_normal)
            if (mTextColor == null) {
                mTextColor = ColorStateList.valueOf(Color.parseColor("#333333"))
            }
            mTextSize = a.getDimensionPixelSize(R.styleable.RichTextEditor_rich_text_size_normal, mTextSize)
            mMarginLeft = a.getDimensionPixelSize(R.styleable.RichTextEditor_rich_text_margin_left, mMarginLeft)
            mMarginRight = a.getDimensionPixelSize(R.styleable.RichTextEditor_rich_text_margin_right, mMarginRight)
            mTextMarginTop = a.getDimensionPixelSize(R.styleable.RichTextEditor_rich_text_margin_top, mTextMarginTop)
            mTextMarginBottom = a.getDimensionPixelSize(R.styleable.RichTextEditor_rich_text_margin_bottom, mTextMarginBottom)
            mImageMarginTop = a.getDimensionPixelSize(R.styleable.RichTextEditor_rich_image_margin_bottom, mImageMarginTop)
            mImageMarginBottom = a.getDimensionPixelSize(R.styleable.RichTextEditor_rich_image_margin_bottom, mImageMarginBottom)
            mLineSpacingMultiplier = a.getFloat(R.styleable.RichTextEditor_rich_text_line_spacing, mLineSpacingMultiplier)
            a.recycle()
        }
    }
}