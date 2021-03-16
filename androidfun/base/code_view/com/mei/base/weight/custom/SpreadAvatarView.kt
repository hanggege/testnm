package com.mei.base.weight.custom

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.mei.orc.ext.dip
import com.mei.wood.R

/**
 * Created by hang on 2020/5/6.
 * 头像扩散波纹动画
 * spread_big_start_color:大圆的起始颜色
 * spread_big_end_color:大圆的结束颜色
 *
 * spread_small_start_color:小圆的起始颜色
 * spread_small_end_color:小圆的结束颜色
 *
 * spread_out_radius:外圆半径 一般是宽度的一半
 * spread_in_radius:内圆半径 一般是包裹的头像半径
 *
 * spread_is_stroke:是否是边框样式  支持fill扩散 效果见im界面等待页面 || stoke扩散 效果见直播中的头像扩散样式
 * spread_stroke_width:边框宽度
 */
class SpreadAvatarView(context: Context, attributeSet: AttributeSet) :
        View(context, attributeSet) {

    private var bigStartColor = Color.parseColor("#4dffe000")
    private var bigEndColor = Color.parseColor("#00ffe000")

    private var smallStartColor = Color.parseColor("#4dffe000")
    private var smallEndColor = Color.parseColor("#4dffe000")

    private var isStrokeStyle = false
    private var strokeWidth = dip(2).toFloat()

    private val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bigPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var outRadius = 100f
    private var inRadius = 100f

    private var bigRadius = 100f
        set(value) {
            field = value
            invalidate()
        }
    private var smallRadius = 100f
        set(value) {
            field = value
            invalidate()
        }

    init {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.SpreadAvatarView)
        bigStartColor = ta.getColor(R.styleable.SpreadAvatarView_spread_big_start_color, Color.parseColor("#4dffe000"))
        bigEndColor = ta.getColor(R.styleable.SpreadAvatarView_spread_big_end_color, Color.parseColor("#00ffe000"))
        smallStartColor = ta.getColor(R.styleable.SpreadAvatarView_spread_small_start_color, Color.parseColor("#4dffe000"))
        smallEndColor = ta.getColor(R.styleable.SpreadAvatarView_spread_small_end_color, Color.parseColor("#4dffe000"))
        strokeWidth = ta.getDimension(R.styleable.SpreadAvatarView_spread_stroke_width, dip(2).toFloat())
        isStrokeStyle = ta.getBoolean(R.styleable.SpreadAvatarView_spread_is_stroke, false)

        outRadius = ta.getDimension(R.styleable.SpreadAvatarView_spread_out_radius, 100f)
        inRadius = ta.getDimension(R.styleable.SpreadAvatarView_spread_in_radius, 100f)
        ta.recycle()
    }

    private fun initPaint() {
        if (isStrokeStyle) {
            bigPaint.style = Paint.Style.STROKE
            bigPaint.strokeWidth = strokeWidth

            smallPaint.style = Paint.Style.STROKE
            smallPaint.strokeWidth = strokeWidth

            notifyGradientState()
        } else {
            bigPaint.style = Paint.Style.FILL
            smallPaint.style = Paint.Style.FILL

            bigPaint.color = currentPaintColor
            smallPaint.color = smallStartColor
        }
    }


    private val path = Path()
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        path.reset()
        path.addCircle(width / 2f, height / 2f, inRadius, Path.Direction.CW)
        path.addCircle(width / 2f, height / 2f, outRadius, Path.Direction.CCW)

        initPaint()
    }

    private fun notifyGradientState() {
        val bigGradient = LinearGradient(width / 2 - bigRadius + strokeWidth / 2, height / 2 - bigRadius + strokeWidth / 2,
                width / 2 + bigRadius - strokeWidth / 2, width / 2 + bigRadius - strokeWidth / 2,
                bigStartColor, bigEndColor, Shader.TileMode.CLAMP)
        bigPaint.shader = bigGradient

        val smallGradient = LinearGradient(width / 2 - smallRadius / 2, height / 2 - smallRadius + strokeWidth / 2,
                width / 2 + smallRadius - strokeWidth / 2, height / 2 + smallRadius - strokeWidth / 2,
                smallStartColor, smallEndColor, Shader.TileMode.CLAMP)
        smallPaint.shader = smallGradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isStrokeStyle) {
            notifyGradientState()
        }
        canvas.clipPath(path)
        canvas.drawCircle(width / 2f, height / 2f, bigRadius, bigPaint)
        canvas.drawCircle(width / 2f, height / 2f, smallRadius, smallPaint)
    }


    private var valueAnimator: ValueAnimator? = null

    private var currentPaintColor: Int = Color.parseColor("#4dffe000")
        set(value) {
            field = value
            bigPaint.color = currentPaintColor
            invalidate()
        }

    fun startAnim(duration: Long = 1500L) {
        getAnimator().removeAllUpdateListeners()
        val listener = if (isStrokeStyle) strokeTypeListener else fillTypeListener
        getAnimator().duration = duration
        getAnimator().addUpdateListener(listener)
        getAnimator().start()
    }

    private fun getAnimator(): ValueAnimator {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                interpolator = LinearInterpolator()
                repeatCount = ValueAnimator.INFINITE
            }
        }
        return valueAnimator!!
    }

    private val fillTypeListener = ValueAnimator.AnimatorUpdateListener { animation ->
        val fraction = animation.animatedFraction
        val startRadius = inRadius + 0.3f * (outRadius - inRadius)
        bigRadius = startRadius + fraction * (outRadius - startRadius)
        currentPaintColor =
                ArgbEvaluator().evaluate(fraction, bigStartColor, bigEndColor) as Int

        smallRadius = if (fraction > 0.7f) {
            inRadius + (fraction - 0.7f) * (outRadius - inRadius)
        } else {
            inRadius
        }
    }

    private val strokeTypeListener = ValueAnimator.AnimatorUpdateListener { animation ->
        val fraction = animation.animatedFraction
        val startRadius = inRadius + 0.3f * (outRadius - inRadius)
        bigRadius = startRadius + fraction * (outRadius - startRadius)
        bigPaint.alpha = ((1 - fraction) * 255).toInt()

        smallRadius = if (fraction > 0.7f) {
            inRadius + (fraction - 0.7f) * (outRadius - inRadius)
        } else {
            inRadius
        }
    }

    fun cancelAnim() {
        valueAnimator?.cancel()
    }

}