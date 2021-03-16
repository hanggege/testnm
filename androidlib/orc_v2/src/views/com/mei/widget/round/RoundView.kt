package com.mei.widget.round

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import com.mei.orc.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/7
 *
 *
 * @attr ref android.R.styleable#back_color 填充色
 * @attr ref android.R.styleable#stroke_color  边框颜色
 * @attr ref android.R.styleable#stroke_width   边框宽度
 * @attr ref android.R.styleable#stroke_radius  角度
 * @attr ref android.R.styleable#is_circle      是否是圆
 */
class RoundView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RelativeLayout(context, attrs, defStyleAttr) {

    /** 背景画笔 **/
    private val paint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL } }

    /** 边框画笔 **/
    private val strokePaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE } }

    var bgColor: ColorStateList = ColorStateList.valueOf(Color.TRANSPARENT)
        set(value) {
            field = value
            invalidate()
        }

    fun setBackColor(@ColorInt color: Int) {
        bgColor = ColorStateList.valueOf(color)
    }

    var strokeColor: ColorStateList = ColorStateList.valueOf(Color.TRANSPARENT)
        set(value) {
            field = value
            invalidate()
        }

    fun setStrokeColor(@ColorInt color: Int) {
        strokeColor = ColorStateList.valueOf(color)
    }

    var strokeWidth: Float = 0f
        set(value) {
            field = value
            strokePaint.strokeWidth = value
            invalidate()
        }
    var radius: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    var isCircle: Boolean = false
        set(value) {
            field = value
            invalidate()
        }


    init {
        setWillNotDraw(false)
        @SuppressLint("CustomViewStyleable")
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.round_style)
        bgColor = typedArray.getColorStateList(R.styleable.round_style_back_color)
                ?: ColorStateList.valueOf(Color.TRANSPARENT)
        strokeColor = typedArray.getColorStateList(R.styleable.round_style_stroke_color)
                ?: ColorStateList.valueOf(Color.TRANSPARENT)
        strokeWidth = typedArray.getDimension(R.styleable.round_style_stroke_width, 0f)
        radius = typedArray.getDimension(R.styleable.round_style_stroke_radius, 0f)
        isCircle = typedArray.getBoolean(R.styleable.round_style_is_circle, false)
        typedArray.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        paint.color = bgColor.getColorForState(drawableState, Color.TRANSPARENT)
        strokePaint.color = strokeColor.getColorForState(drawableState, Color.TRANSPARENT)
        if (isCircle) {
            val drawWidth = measuredWidth - paddingLeft - paddingRight
            val drawHeight = measuredHeight - paddingTop - paddingBottom
            val r = (Math.min(drawWidth, drawHeight) - strokeWidth * 2) / 2
            canvas.drawCircle(drawWidth / 2f + paddingLeft, drawHeight / 2f + paddingTop, r, paint)
            canvas.drawCircle(drawWidth / 2f + paddingLeft, drawHeight / 2f + paddingTop, r + strokeWidth / 2, strokePaint)
        } else {
            val rectF = RectF(strokeWidth / 2 + paddingLeft, strokeWidth / 2 + paddingTop,
                    measuredWidth - strokeWidth / 2 - paddingRight, measuredHeight - strokeWidth / 2 - paddingBottom)
            canvas.drawRoundRect(rectF, radius, radius, paint)
            canvas.drawRoundRect(rectF, radius, radius, strokePaint)

        }
        super.onDraw(canvas)
    }
}