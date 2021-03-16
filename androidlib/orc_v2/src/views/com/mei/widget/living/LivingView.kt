package com.mei.widget.living

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.mei.orc.R


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/22
 * @attr ref R.styleable#line_number  跳动的线的数量
 * @attr ref R.styleable#line_color 线的颜色
 * @attr ref R.styleable#line_width 线的宽度，不传的话则线与间距均分
 * @attr ref R.styleable#line_duration 执行一遍需要的时间（单位秒,可用小数点）
 *
 *
 * 绘制时会去掉padding
 *
 *
 * 放列表中的话用[LivingItemView]
 * 如果一定要用这个，请用[android.view.ViewGroup]包裹起来用
 */
class LivingView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    /**
     * 线的数量
     */
    private var lineNum = 4

    /**
     * 线宽
     */
    private var lineWidth = 0f

    /**
     * 执行一遍动画时长
     */
    private var line_duration = 1.8f

    @ColorInt
    private var lineColor: Int = 0

    private var interpolated = 0f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.living_style)
        lineColor = typedArray.getColor(R.styleable.living_style_line_color, -0x6000)
        lineNum = typedArray.getInt(R.styleable.living_style_line_number, 4)
        lineWidth = typedArray.getDimension(R.styleable.living_style_line_width, 0f)
        line_duration = typedArray.getFloat(R.styleable.living_style_line_duration, 1.8f)
        typedArray.recycle()

        startAnim()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /** 可以画的空间  */
        val height = measuredHeight - paddingBottom - paddingTop
        val width = measuredWidth - paddingLeft - paddingRight

        /** 线宽  */
        lineWidth = if (lineWidth == 0f) width / (lineNum.toFloat() * 2 - 1) else lineWidth
        /** 间距  */
        val spaceWidth = (width - lineWidth * lineNum) / (lineNum - 1)

        val pain = Paint()
        pain.color = lineColor
        pain.style = Paint.Style.STROKE
        pain.strokeWidth = lineWidth
        pain.isAntiAlias = false

        /** 0~1时向上移动  1~2时向下移动 */
        val percent = interpolated * 2


        var firstStartY = 0f
        for (i in 0 until lineNum) {
            val startX = i * (lineWidth + spaceWidth) + lineWidth / 2 + paddingLeft.toFloat()

            /**两条线移动差 */
            val offset = (i % 3 * height / 4).toFloat()

            var startY = paddingTop.toFloat() + height * percent + offset
            val endY = (paddingTop + height).toFloat()
            if (percent < 1) {
                if (firstStartY > endY - offset) {
                    startY = 2 * endY - startY
                }
            } else {
                startY = 2 * endY - startY
                if (startY < paddingTop) {
                    startY = 2 * paddingTop - startY
                }
            }
            if (startY < paddingTop) startY = paddingTop.toFloat()

            if (i == 0) firstStartY = startY
            canvas.drawLine(startX, startY, startX, endY, pain)
        }

    }

    fun setLineNum(lineNum: Int) {
        this.lineNum = lineNum
        invalidate()
    }

    fun setLineWidth(lineWidth: Float) {
        this.lineWidth = lineWidth
        invalidate()
    }

    fun setLineColor(@ColorInt lineColor: Int) {
        this.lineColor = lineColor
        invalidate()
    }

    fun setLine_duration(line_duration: Float) {
        this.line_duration = line_duration
        showLiving(false)
        startAnim()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isVisible) startAnim()

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearAnimation()
    }

    override fun setVisibility(visibility: Int) {
        if (visibility == View.VISIBLE && getVisibility() != View.VISIBLE) {
            startAnim()
        } else if (visibility == View.INVISIBLE || visibility == View.GONE) {
            showLiving(false)
        }
        super.setVisibility(visibility)
        invalidate()

    }

    /**
     * 开启动画与关闭动画
     */
    fun showLiving(isLiving: Boolean) {
        if (isLiving) {
            if (animation == null) {
                startAnim()
            }
        } else
            clearAnimation()
    }

    private fun startAnim() {
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                super.applyTransformation(interpolatedTime, t)
                interpolated = interpolatedTime
                invalidate()
            }
        }
        animation.interpolator = LinearInterpolator()
        animation.duration = (line_duration * 1000).toInt().toLong()
        animation.repeatCount = Animation.INFINITE
        startAnimation(animation)
    }
}
