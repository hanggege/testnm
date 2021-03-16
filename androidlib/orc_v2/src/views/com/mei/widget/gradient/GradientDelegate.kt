package com.mei.widget.gradient

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.mei.orc.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/8/5
 *
app:gd_LB_radius="50dp" // 左下角圆角
app:gd_RB_radius="20dp" //右下角圆角
app:gd_LT_radius="5dp" // 左上角圆角
app:gd_RT_radius="10dp" //右上角圆角
app:gd_radius="20dp" // 全局圆角
app:gd_start_color="@android:color/holo_blue_light" //开始渐变颜色
app:gd_center_color="@android:color/holo_red_light" //中间渐变颜色
app:gd_end_color="@android:color/holo_green_light"// 结束颜色
app:gd_direction="leftToRight" //渐变方向

app:gd_stroke_start_color="@android:color/background_dark" //边框开始颜色
app:gd_stroke_center_color="@android:color/holo_orange_dark" //边框 中间颜色
app:gd_stroke_end_color="@android:color/darker_gray" //边框结束颜色
app:gd_stroke_direction="topToBottom"//边框颜色渐变方向 ,没有设置的话取上面的gd_direction
app:gd_stroke_width="10dp" //边框宽度
 */
class GradientDelegate(val context: Context, val view: View, val attrs: AttributeSet?) {
    @ColorInt
    var startColor: Int = -1

    @ColorInt
    var centerColor: Int = -1

    @ColorInt
    var endColor: Int = -1

    /** 0: 从左到右，1：从上到下，2：从左上到右下 **/
    var direction: Int = 0
    var radius: Float = 0f
    var leftTopRadius: Float = 0f
    var leftBottomRadius: Float = 0f
    var rightTopRadius: Float = 0f
    var rightBottomRadius: Float = 0f

    /** 边框 **/
    /** 边框渐变色的方向 0: 从左到右，1：从上到下，2：从左上到右下 **/
    var strokeDirection: Int = 0

    @ColorInt
    var strokeStartColor: Int = -1

    @ColorInt
    var strokeCenterColor: Int = -1

    @ColorInt
    var strokeEndColor: Int = -1
    var strokeWidth: Float = 0f

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.GradientTextView)
        startColor = typeArray.getColor(R.styleable.GradientTextView_gd_start_color, -1)
        centerColor = typeArray.getColor(R.styleable.GradientTextView_gd_center_color, -1)
        endColor = typeArray.getColor(R.styleable.GradientTextView_gd_end_color, -1)
        direction = typeArray.getInt(R.styleable.GradientTextView_gd_direction, 0)
        radius = typeArray.getDimension(R.styleable.GradientTextView_gd_radius, 0f)
        leftTopRadius = typeArray.getDimension(R.styleable.GradientTextView_gd_LT_radius, 0f)
        leftBottomRadius = typeArray.getDimension(R.styleable.GradientTextView_gd_LB_radius, 0f)
        rightTopRadius = typeArray.getDimension(R.styleable.GradientTextView_gd_RT_radius, 0f)
        rightBottomRadius = typeArray.getDimension(R.styleable.GradientTextView_gd_RB_radius, 0f)

        // 边框的方向没有渐变的话默认用上面的
        strokeDirection = typeArray.getInt(R.styleable.GradientTextView_gd_stroke_direction, direction)
        strokeWidth = typeArray.getDimension(R.styleable.GradientTextView_gd_stroke_width, 0f)
        strokeStartColor = typeArray.getColor(R.styleable.GradientTextView_gd_stroke_start_color, -1)
        strokeCenterColor = typeArray.getColor(R.styleable.GradientTextView_gd_stroke_center_color, -1)
        strokeEndColor = typeArray.getColor(R.styleable.GradientTextView_gd_stroke_end_color, -1)
        typeArray.recycle()

        if (view.background == null) view.setBackgroundColor(Color.TRANSPARENT)
    }

    fun refresh() {
        view.background = drawableProvider()
    }

    private fun drawableProvider(): Drawable {
        val colors = arrayOf(startColor, centerColor, endColor).filter { it != -1 }
        val radii = if (radius > 0) arrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        else arrayOf(leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius)

        val foregroundDrawable = GradientDrawable().apply {
            cornerRadii = radii.toFloatArray()
            shape = GradientDrawable.RECTANGLE
            orientation = when (direction) {
                0 -> GradientDrawable.Orientation.LEFT_RIGHT
                1 -> GradientDrawable.Orientation.TOP_BOTTOM
                2 -> GradientDrawable.Orientation.TL_BR
                else -> GradientDrawable.Orientation.LEFT_RIGHT
            }
            when {
                colors.size > 1 -> setColors(colors.toIntArray())
                colors.size == 1 -> setColor(colors.first())
                else -> setColor(Color.TRANSPARENT)
            }
        }
        return if (strokeWidth <= 0) foregroundDrawable
        else {
            /** 边框 **/
            val strokeColors = arrayOf(strokeStartColor, strokeCenterColor, strokeEndColor).filter { it != -1 }
            if (strokeColors.size == 1) {
                foregroundDrawable.setStroke(strokeWidth.toInt(), strokeColors.first())
                foregroundDrawable
            } else {
                val strokeDrawable = GradientDrawable().apply {
                    cornerRadii = radii.toFloatArray()
                    shape = GradientDrawable.RECTANGLE
                    orientation = when (strokeDirection) {
                        0 -> GradientDrawable.Orientation.LEFT_RIGHT
                        1 -> GradientDrawable.Orientation.TOP_BOTTOM
                        2 -> GradientDrawable.Orientation.TL_BR
                        else -> GradientDrawable.Orientation.LEFT_RIGHT
                    }
                    when {
                        strokeColors.size > 1 -> setColors(strokeColors.toIntArray())
                        strokeColors.size == 1 -> setColor(strokeColors.first())
                        else -> setColor(Color.TRANSPARENT)
                    }
                }
                val padding = strokeWidth.toInt()
                LayerDrawable(arrayOf(strokeDrawable, foregroundDrawable)).apply {
                    setLayerInset(1, padding, padding, padding, padding)
                }
            }
        }
    }
}

fun GradientDelegate.applyViewDelegate(block: GradientDelegate.() -> Unit) = apply {
    startColor = -1
    centerColor = -1
    endColor = -1
    direction = 0
    radius = 0f
    leftTopRadius = 0f
    leftBottomRadius = 0f
    rightTopRadius = 0f
    rightBottomRadius = 0f
    strokeDirection = 0
    strokeStartColor = -1
    strokeCenterColor = -1
    strokeEndColor = -1
    strokeWidth = 0f
    block()
    refresh()
}