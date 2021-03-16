package com.mei.widget.multiline

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
 * app:multi_line_color="@android:color/holo_red_light"  线的颜色
 * app:multi_line_width="5dp"  线的宽度
 * app:multi_h_number="1"   横线的数量
 * app:multi_v_number="1"   竖线的数量
 */
class MultiLineView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        BackGroundView(context, attrs, defStyleAttr) {
    private val defaultColor = Color.parseColor("#8B8B8B")
    private val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = lineWidth
        }
    }

    var lineColor: ColorStateList = ColorStateList.valueOf(defaultColor)
        set(value) {
            field = value
            invalidate()
        }

    fun setLineColor(@ColorInt color: Int) {
        lineColor = ColorStateList.valueOf(color)
    }

    var lineWidth: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, resources.displayMetrics)
        set(value) {
            field = value
            paint.strokeWidth = value
            invalidate()
        }

    /**横线数量**/
    var horizontalLineNumber: Int = 1
        set(value) {
            field = value
            invalidate()
        }

    /** 竖线数量 **/
    var verticalLineNumber: Int = 1
        set(value) {
            field = value
            invalidate()
        }

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.MultiLineView)
        lineColor = typeArray.getColorStateList(R.styleable.ChoiceView_choice_color)
                ?: ColorStateList.valueOf(defaultColor)
        lineWidth = typeArray.getDimension(R.styleable.MultiLineView_multi_line_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, resources.displayMetrics))
        horizontalLineNumber = typeArray.getInt(R.styleable.MultiLineView_multi_h_number, 1)
        verticalLineNumber = typeArray.getInt(R.styleable.MultiLineView_multi_v_number, 1)
        typeArray.recycle()
    }

    override fun onDrawChild(canvas: Canvas, w: Int, h: Int) {
        paint.color = lineColor.getColorForState(drawableState, defaultColor)

        /**画横线**/
        val ySpace = (h - lineWidth * horizontalLineNumber) / (horizontalLineNumber + 1)
        if (horizontalLineNumber == 1) {
            canvas.drawLine(paddingLeft.toFloat(), paddingTop + h / 2f, (paddingLeft + w).toFloat(), paddingTop + h / 2f, paint)
        } else {
            for (i in 1..horizontalLineNumber) {
                val startY = paddingTop + i * (ySpace + lineWidth) - lineWidth / 2
                canvas.drawLine(paddingLeft.toFloat(), startY, (paddingLeft + w).toFloat(), startY, paint)
            }
        }

        /** 画竖线 **/
        val xSpace = (w - lineWidth * verticalLineNumber) / (verticalLineNumber + 1)
        if (verticalLineNumber == 1) {
            canvas.drawLine(paddingLeft + w / 2f, paddingTop.toFloat(), paddingLeft + w / 2f, (paddingTop + h).toFloat(), paint)
        } else {
            for (i in 1..verticalLineNumber) {
                val startX = paddingLeft + i * (xSpace + lineWidth) - lineWidth / 2
                canvas.drawLine(startX, paddingTop.toFloat(), startX, (paddingTop + h).toFloat(), paint)
            }
        }

    }
}