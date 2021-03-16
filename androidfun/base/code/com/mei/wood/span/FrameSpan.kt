package com.mei.wood.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

/**
 * Created by hang on 2020/8/14.
 * 画边框的span
 */
class FrameSpan(strokeColor: Int, strokeWidth: Float, private val textColor: Int) : ReplacementSpan() {
    private var strokePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        this.strokeWidth = strokeWidth
        color = strokeColor
    }
    private var width = 0;

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        width = paint.measureText(text, start, end).toInt()
        return width
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        canvas.drawRect(x, top.toFloat(), x + width, bottom.toFloat(), strokePaint)
        paint.color = textColor
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }
}