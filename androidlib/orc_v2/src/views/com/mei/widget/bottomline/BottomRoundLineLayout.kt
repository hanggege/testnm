package com.mei.widget.bottomline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Created by joker on 2018/3/20.
 */

class BottomRoundLineLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : BottomLineLayout(context, attrs, defStyleAttr) {


    override fun drawLine(canvas: Canvas, paint: Paint, childView: View) {
        var childWidth = lineWidth
        if (lineWidth == View.NO_ID.toFloat()) {
            childWidth = childView.measuredWidth - lineOffset * 2
        }
        val rect = RectF()
        rect.left = childView.left + (childView.measuredWidth - childWidth) / 2
        rect.right = childView.left + (childView.measuredWidth + childWidth) / 2
        rect.top = measuredHeight.toFloat() - lineHeight / 2 - paddingBottom.toFloat()
        rect.bottom = rect.top + lineHeight

        canvas.drawRoundRect(rect, lineHeight / 2, lineHeight / 2, paint)
    }
}
