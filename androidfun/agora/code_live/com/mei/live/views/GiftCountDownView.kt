package com.mei.live.views

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.mei.orc.ext.dip
import com.mei.wood.R

/**
 * Created by hang on 2019-12-09.
 */
const val ANIMATOR_DURATION = 4000L

class GiftCountDownView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : View(context, attributeSet, defStyle) {

    /** 边框画笔 **/
    private val mStrokePaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
    }

    private val mBgPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#1affffff")
        }
    }

    var mStrokeWidth = dip(5).toFloat()
        set(value) {
            field = value
            mStrokePaint.strokeWidth = value
            invalidate()
        }

    private var mStartColor = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    private var mEndColor = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    @SuppressLint("AnimatorKeep")
    var mProgress: Float = 100f
        set(value) {
            field = value
            invalidate()
        }

    private var linearGradient: LinearGradient? = null
    private val rectF = RectF()

    val mAnimator: ValueAnimator = ObjectAnimator.ofFloat(this, "mProgress", 100f, 0f).apply {
        duration = ANIMATOR_DURATION
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.GiftCountDownView)
        mStartColor = typedArray.getColor(R.styleable.GiftCountDownView_gift_start_color, Color.TRANSPARENT)
        mEndColor = typedArray.getColor(R.styleable.GiftCountDownView_gift_end_color, Color.TRANSPARENT)
        mStrokeWidth = typedArray.getDimension(R.styleable.GiftCountDownView_gift_gradient_stroke_width, dip(5).toFloat())

        typedArray.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        linearGradient = LinearGradient(paddingLeft + mStrokeWidth / 2, paddingTop + mStrokeWidth / 2,
                measuredWidth - paddingRight - mStrokeWidth / 2, measuredHeight - paddingBottom - mStrokeWidth / 2,
                mStartColor, mEndColor, Shader.TileMode.CLAMP)
        mStrokePaint.shader = linearGradient
        rectF.set(mStrokeWidth / 2 + paddingLeft, mStrokeWidth / 2 + paddingTop,
                measuredWidth - mStrokeWidth / 2 - paddingRight, measuredHeight - mStrokeWidth / 2 - paddingBottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mAnimator.isRunning) {
            canvas?.drawCircle(rectF.centerX(), rectF.centerY(), rectF.height() / 2 + mStrokeWidth / 2, mBgPaint)
            canvas?.drawArc(rectF, -90f, mProgress * 360 / 100, false, mStrokePaint)
        }
    }

    fun startAnim() {
        mAnimator.start()
    }

    fun endAnim() {
        mAnimator.end()
        invalidate()
    }
}