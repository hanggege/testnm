package com.mei.widget.menupoint

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
 * app:menu_point_color="@android:color/holo_red_light" 点的颜色
 * app:menu_point_width="5dp" 点的大小 宽度
 * app:menu_type="horizontal"  方向
 * app:menu_number="3" 数量
 */
class MenuPointView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        BackGroundView(context, attrs, defStyleAttr) {
    private val defaultColor = Color.parseColor("#9b9b9b")

    private val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            strokeWidth = pointWidth
        }
    }

    var pointColor: ColorStateList = ColorStateList.valueOf(defaultColor)
        set(value) {
            field = value
            invalidate()
        }

    fun setPointColor(@ColorInt color: Int) {
        pointColor = ColorStateList.valueOf(color)
    }

    var pointWidth: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, resources.displayMetrics)
        set(value) {
            field = value
            paint.strokeWidth = value
            invalidate()
        }

    /**点数量**/
    var pointNumber: Int = 3
        set(value) {
            field = value
            invalidate()
        }

    /**点的方向  0 横向 1 竖向**/
    var direction: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.MenuPointView)
        pointColor = typeArray.getColorStateList(R.styleable.MenuPointView_menu_point_color)
                ?: ColorStateList.valueOf(defaultColor)
        pointWidth = typeArray.getDimension(R.styleable.MenuPointView_menu_point_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, resources.displayMetrics))
        pointNumber = typeArray.getInt(R.styleable.MenuPointView_menu_number, 3)
        direction = typeArray.getInt(R.styleable.MenuPointView_menu_type, 0)
        typeArray.recycle()
    }

    override fun onDrawChild(canvas: Canvas, w: Int, h: Int) {
        paint.color = pointColor.getColorForState(drawableState, defaultColor)
        if (pointNumber == 0) return
        if (pointNumber == 1) {
            canvas.drawCircle(paddingLeft + w / 2f, paddingTop + h / 2f, pointWidth / 2, paint)
            return
        }
        when (direction) {
            0 -> {
                val xSpace = (w - pointNumber * pointWidth) / (pointNumber - 1)
                for (i in 0 until pointNumber) {
                    canvas.drawCircle(paddingLeft + pointWidth / 2f + i * (xSpace + pointWidth), paddingTop + h / 2f, pointWidth / 2, paint)
                }
            }
            1 -> {
                val ySpace = (h - pointNumber * pointWidth) / (pointNumber - 1)
                for (i in 0 until pointNumber) {
                    canvas.drawCircle(paddingTop + w / 2f, paddingTop + pointWidth / 2f + i * (ySpace + pointWidth), pointWidth / 2, paint)
                }
            }
            else -> {
            }
        }
    }

}