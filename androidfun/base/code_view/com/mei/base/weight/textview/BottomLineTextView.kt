package com.mei.base.weight.textview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import com.mei.wood.R

/**
 * Created by zzw on 2019/4/11
 * Des:
 */
class BottomLineTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var mLinePaint: Paint
    private var mLineHeight: Int = 0
    private var mLineColor: Int = 0
    private var mLineRadius: Int = 0
    private var mLinePadBottom: Int = 0

    init {
        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.BottomLineTextView)
            mLineHeight = ta.getDimensionPixelSize(R.styleable.BottomLineTextView_lineHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, context.resources.displayMetrics).toInt())
            mLineRadius = ta.getDimensionPixelSize(R.styleable.BottomLineTextView_lineRadius, 0)
            mLinePadBottom = ta.getDimensionPixelSize(R.styleable.BottomLineTextView_linePadBottom, 0)
            mLineColor = ta.getColor(R.styleable.BottomLineTextView_lineColor, -0x2f1bec)
            ta.recycle()
        }
        mLinePaint = Paint()
        mLinePaint.isAntiAlias = true
        mLinePaint.color = mLineColor
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val rect = Rect()
        paint.getTextBounds(text.toString(), 0, text.length, rect)
        val d = (measuredWidth - rect.width()) / 2

        val rectF = RectF()
        rectF.left = d.toFloat()
        rectF.right = (measuredWidth - d).toFloat()
        rectF.top = (measuredHeight - mLineHeight - mLinePadBottom).toFloat()
        rectF.bottom = measuredHeight.toFloat()
        canvas.drawRoundRect(rectF, mLineRadius.toFloat(), mLineRadius.toFloat(), mLinePaint)
        super.onDraw(canvas)
    }

    fun setLinePaint(mLinePaint: Paint) {
        this.mLinePaint = mLinePaint
    }

    override fun setLineHeight(mLineHeight: Int) {
        this.mLineHeight = mLineHeight
    }

    fun setLineColor(mLineColor: Int) {
        this.mLineColor = mLineColor
        mLinePaint.color = mLineColor
    }

    fun setLineRadius(mLineRadius: Int) {
        this.mLineRadius = mLineRadius
    }

    fun setLinePadBottom(mLinePadBottom: Int) {
        this.mLinePadBottom = mLinePadBottom
    }
}
