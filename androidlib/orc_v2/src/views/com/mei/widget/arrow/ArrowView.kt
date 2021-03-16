package com.mei.widget.arrow

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.ColorInt
import com.mei.orc.R
import com.mei.widget.background.BackGroundView

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/14
 *
 *
 * app:arrow_direction="left" //箭头方向  2上 3下 0左 1右
 * app:arrow_full="true" // 箭头是否铺满
 * app:arrow_line_color="@android:color/holo_red_light" //剪头颜色
 * app:arrow_width="8dp" //线条宽度
 *
 * 箭头控件，包含上下左右方向 ，内容 是否填充等
 *
 */
class ArrowView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        BackGroundView(context, attrs, defStyleAttr) {

    private val defaultColor = ColorStateList.valueOf(Color.parseColor("#9b9b9b"))

    private val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = arrowWidth
            strokeCap = Paint.Cap.ROUND
        }
    }
    var arrowColor: ColorStateList = defaultColor
        set(value) {
            field = value
            invalidate()
        }

    fun setArrowColor(@ColorInt color: Int) {
        arrowColor = ColorStateList.valueOf(color)
    }

    var arrowWidth: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, resources.displayMetrics)
        set(value) {
            field = value
            paint.strokeWidth = value
            invalidate()
        }

    /**箭头方向  2上 3下 0左 1右**/
    var arrowDirection: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var arrowFull: Boolean = false
        set(value) {
            field = value
            paint.style = if (value) Paint.Style.FILL else Paint.Style.STROKE
            invalidate()
        }

    init {

        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ArrowView)
        arrowColor = typeArray.getColorStateList(R.styleable.ArrowView_arrow_line_color)
                ?: defaultColor
        arrowWidth = typeArray.getDimension(R.styleable.ArrowView_arrow_width,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, resources.displayMetrics))
        arrowDirection = typeArray.getInt(R.styleable.ArrowView_arrow_direction, 0)
        arrowFull = typeArray.getBoolean(R.styleable.ArrowView_arrow_full, false)
        typeArray.recycle()
    }

    override fun onDrawChild(canvas: Canvas, w: Int, h: Int) {
        paint.color = arrowColor.getColorForState(drawableState, Color.parseColor("#9b9b9b"))
        /**画箭头**/
        val offset = if (arrowFull) 0f else arrowWidth

        val drawWidth = w - offset
        val drawHeight = h - offset

        val startX = paddingLeft.toFloat() + offset / 2
        val startY = paddingTop.toFloat() + offset / 2
        val centerY = startY + drawHeight / 2f
        val centerX = startX + drawWidth / 2f

        val path = when (arrowDirection) {
            3 -> {//箭头向下
                Path().apply {
                    moveTo(startX, startY)
                    lineTo(centerX, startY + drawHeight)
                    lineTo(startX + drawWidth, startY)
                }
            }
            2 -> {//箭头向上
                Path().apply {
                    moveTo(startX, startY + drawHeight)
                    lineTo(centerX, startY)
                    lineTo(startX + drawWidth, startY + drawHeight)
                }
            }
            1 -> {//箭头向右
                Path().apply {
                    moveTo(startX, startY)
                    lineTo(startX + drawWidth, centerY)
                    lineTo(startX, startY + drawHeight)
                }
            }
            else -> {//箭头向左
                Path().apply {
                    moveTo(startX + drawWidth, startY)
                    lineTo(startX, centerY)
                    lineTo(startX + drawWidth, startY + drawHeight)
                }
            }
        }
        canvas.drawPath(path, paint)
    }
}