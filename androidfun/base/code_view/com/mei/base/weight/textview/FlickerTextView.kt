package com.mei.base.weight.textview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.mei.wood.R

/**
 * 备注：闪动的字体类
 */
class FlickerTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var mViewWidth: Int = 0
    private var mPaint: Paint? = null
    private var mLinearGradient: LinearGradient? = null
    private var mGradientMatrix: Matrix? = null
    private var mTranslate: Int = 0

    private var flickerColor: Int = 0
    private var secondColor: Int = 0

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.FlickerTextView)
            flickerColor = a.getColor(R.styleable.FlickerTextView_flickerColor, Color.WHITE)
            secondColor = a.getColor(R.styleable.FlickerTextView_secondColor, Color.TRANSPARENT)
            a.recycle()
        }
    }

    //组件大小改变时回调
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (mViewWidth == 0) {
            mViewWidth = measuredWidth
            if (mViewWidth > 0) {
                mPaint = paint
                mLinearGradient = LinearGradient(0f, 0f, mViewWidth.toFloat(), 0f, intArrayOf(flickerColor, secondColor, flickerColor), null, Shader.TileMode.CLAMP)
                mPaint?.shader = mLinearGradient
                mGradientMatrix = Matrix()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mGradientMatrix?.let {
            mTranslate += mViewWidth / 5
            if (mTranslate > mViewWidth) {
                mTranslate = -mViewWidth
            }
            it.setTranslate(mTranslate.toFloat(), 0f)
            mLinearGradient?.setLocalMatrix(mGradientMatrix)
            postInvalidateDelayed(100)
        }
    }
}