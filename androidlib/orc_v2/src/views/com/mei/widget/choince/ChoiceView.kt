package com.mei.widget.choince

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
 * app:choice_color="@android:color/holo_red_light" 颜色
 * app:choice_type="cross"  类型  none无 tick对勾 cross 叉叉 plus 加号
 * app:choice_width="5dp"
 *
 */
class ChoiceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        BackGroundView(context, attrs, defStyleAttr) {
    private val defaultColor = Color.parseColor("#8B8B8B")
    val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
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

    /**1 对勾 2 叉叉 3 加号**/
    var type: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ChoiceView)
        lineColor = typeArray.getColorStateList(R.styleable.ChoiceView_choice_color)
                ?: ColorStateList.valueOf(defaultColor)
        lineWidth = typeArray.getDimension(R.styleable.ChoiceView_choice_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, resources.displayMetrics))
        type = typeArray.getInt(R.styleable.ChoiceView_choice_type, 0)
        typeArray.recycle()
    }

    override fun onDrawChild(canvas: Canvas, w: Int, h: Int) {
        paint.color = lineColor.getColorForState(drawableState, defaultColor)

        var startX = paddingLeft + lineWidth / 2
        var startY = paddingTop + lineWidth / 2
        var drawWidth = w - lineWidth
        var drawHeight = h - lineWidth

        when (type) {
            1 -> {//对勾
                canvas.drawPath(Path().apply {
                    moveTo(startX, startY + drawHeight / 2f)
                    lineTo(startX + drawWidth / 3f, startY + drawHeight * 5 / 6f)
                    lineTo(startX + drawWidth, startY + drawHeight / 6f)
                }, paint)
            }
            2 -> {//叉叉
                startX += drawWidth / 7
                startY += drawHeight / 7
                drawWidth -= drawWidth * 2 / 7
                drawHeight -= drawHeight * 2 / 7

                canvas.drawLine(startX, startY, startX + drawWidth, startY + drawHeight, paint)
                canvas.drawLine(startX + drawWidth, startY, startX, startY + drawHeight, paint)
            }
            3 -> {
                canvas.drawLine(startX + drawWidth / 2, startY, startX + drawWidth / 2, startY + drawHeight, paint)
                canvas.drawLine(startX, startY + drawHeight / 2, startX + drawWidth, startY + drawHeight / 2, paint)
            }
            else -> {
            }
        }
    }
}