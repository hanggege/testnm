package com.mei.widget.redpoint

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.doOnPreDraw
import com.mei.orc.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/3
 *
 * 消息小红点
 * 宽度可自适应或者写死  宽度进行计算所得，xml无法写死，padding只适用于Y轴
 * @attr ref android.R.styleable#back_color 小红点背景
 * @attr ref android.R.styleable#stroke_color  边框颜色
 * @attr ref android.R.styleable#stroke_width   边框宽度
 */
@SuppressLint("CustomViewStyleable")
class RedPointView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        AppCompatTextView(context, attrs, defStyleAttr) {


    private val defaultColor = Color.parseColor("#FF5949")

    var bgColor = defaultColor
        set(value) {
            field = value
            refreshBackground()
        }

    var strokeColor = Color.TRANSPARENT
        set(value) {
            field = value
            refreshBackground()
        }
    var strokeWidth = 0f
        set(value) {
            field = value
            refreshBackground()
        }

    init {
        gravity = Gravity.CENTER
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
        includeFontPadding = false
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.round_style)
        bgColor = typedArray.getColor(R.styleable.round_style_back_color, defaultColor)
        strokeColor = typedArray.getColor(R.styleable.round_style_stroke_color, Color.TRANSPARENT)
        strokeWidth = typedArray.getDimension(R.styleable.round_style_stroke_width, 0f)
        typedArray.recycle()
        doOnPreDraw { refreshWidth() }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        refreshWidth()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        refreshWidth()
        refreshBackground()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        refreshWidth(text)
    }


    private fun refreshWidth(text: CharSequence? = getText()) {
        val w = when (val len = (text ?: "").length) {
            0 -> 0
            1 -> measuredHeight
            else -> measuredHeight + (textSize * (len - 1) * 3 / 5).toInt()
        }
        if (w != width) {
            width = w
            invalidate()
        }
    }

    private fun refreshBackground() {
        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(bgColor)
            setStroke(strokeWidth.toInt(), strokeColor)
            cornerRadius = measuredHeight / 2f
        }
    }


}