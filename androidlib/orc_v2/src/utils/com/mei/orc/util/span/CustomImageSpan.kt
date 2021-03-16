package com.mei.orc.util.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

/**
 * Created by joker on 16/3/10.
 */
class CustomImageSpan : ImageSpan {

    constructor(context: Context, resourceId: Int) : super(context, resourceId, ALIGN_BASELINE)

    constructor(drawable: Drawable) : super(drawable, ALIGN_BASELINE)

    /**
     * 居中对齐
     */
    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val d: Drawable? = drawable
        val rect = d?.bounds
        fm?.apply {
            val fmPaint = paint.fontMetricsInt
            val fontHeight = fmPaint.bottom - fmPaint.top
            val drHeight = rect?.bottom ?: 0 - (rect?.top ?: 0)

            val top = drHeight / 2 - fontHeight / 4
            val bottom = drHeight / 2 + fontHeight / 4

            this.ascent = -bottom
            this.top = -bottom
            this.bottom = top
            this.descent = top
        }
        return rect?.right ?: 0
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int,
                      paint: Paint) {
        val fm = paint.fontMetricsInt
        val drawable = drawable
        val transY = ((y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.bottom / 2)
        canvas.save()
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}
