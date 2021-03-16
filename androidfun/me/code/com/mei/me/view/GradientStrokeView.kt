package com.mei.me.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.mei.wood.R

/**
 * Created by hang on 2019/1/2.
 */
class GradientStrokeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    /** 边框画笔 **/
    private val mStrokePaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE } }

    var mRadius: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    var mStrokeWidth: Float = 0f
        set(value) {
            field = value
            mStrokePaint.strokeWidth = value
            invalidate()
        }

    var mStartColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            notifyState()
            invalidate()
        }

    var mEndColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            notifyState()
            invalidate()
        }

    private var linearGradient: LinearGradient? = null
    private val rectF = RectF()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GradientStrokeView)
        mStartColor = typedArray.getColor(R.styleable.GradientStrokeView_start_color, Color.TRANSPARENT)
        mEndColor = typedArray.getColor(R.styleable.GradientStrokeView_end_color, Color.TRANSPARENT)
        mRadius = typedArray.getDimension(R.styleable.GradientStrokeView_round_radius, 0f)
        mStrokeWidth = typedArray.getDimension(R.styleable.GradientStrokeView_gradient_stroke_width, 0f)


        typedArray.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        notifyState()
    }

    private fun notifyState() {
        linearGradient = LinearGradient(paddingLeft + mStrokeWidth / 2, paddingTop + mStrokeWidth / 2,
                measuredWidth - paddingRight - mStrokeWidth / 2, measuredHeight - paddingBottom - mStrokeWidth / 2,
                mStartColor, mEndColor, Shader.TileMode.CLAMP)
        mStrokePaint.shader = linearGradient

        rectF.set(mStrokeWidth / 2 + paddingLeft, mStrokeWidth / 2 + paddingTop,
                measuredWidth - mStrokeWidth / 2 - paddingRight, measuredHeight - mStrokeWidth / 2 - paddingBottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRoundRect(rectF, mRadius, mRadius, mStrokePaint)
    }
}