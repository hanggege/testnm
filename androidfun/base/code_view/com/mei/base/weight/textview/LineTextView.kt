package com.mei.base.weight.textview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * Created by Joker on 2015/6/23.
 * 加一条线
 */
class LineTextView : AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val p = Paint()
        p.isAntiAlias = true
        p.color = currentTextColor
        p.style = Paint.Style.STROKE
        p.strokeWidth = 2f
        canvas.drawLine(0f, (this.height / 2).toFloat(), (this.width - 2).toFloat(), (this.height / 2).toFloat(), p)
    }
}
