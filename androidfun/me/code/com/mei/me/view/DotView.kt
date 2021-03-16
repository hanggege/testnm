package com.mei.me.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.util.AttributeSet
import android.view.View
import com.mei.orc.Cxt
import com.mei.orc.ext.dip


class DotView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    val paint = Paint()

    init {
        paint.color = Cxt.getColor(android.R.color.white)
        paint.style = Paint.Style.FILL
        paint.strokeWidth = dip(2).toFloat()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas?.drawCircle((width).toFloat() - dip(10), (height / 2).toFloat(), dip(3).toFloat(), paint)
        canvas?.drawCircle((width).toFloat() - dip(20), (height / 2).toFloat(), dip(3).toFloat(), paint)
        canvas?.drawCircle((width).toFloat() - dip(30), (height / 2).toFloat(), dip(3).toFloat(), paint)
    }
}