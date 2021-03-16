package com.mei.radio.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

/**
 * RadioItemDecoration
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-22
 */
class LineItemDecoration : RecyclerView.ItemDecoration() {

    @ColorInt
    var color = Color.GRAY
        set(value) {
            if (field != value) {
                field = value
                paint.color = value
            }
        }

    var dividerHeight = 2

    var marginHorizontal = 0
        set(value) {
            field = value
            marginLeft = value
            marginRight = value
        }
    var marginLeft = 0
    var marginRight = 0

    private val mBounds = Rect()

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = this@LineItemDecoration.color
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter ?: return
        val position = (view.layoutParams as? RecyclerView.LayoutParams)?.viewLayoutPosition ?: -1
        if (position != adapter.itemCount - 1) {
            outRect.bottom = dividerHeight
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null) {
            return
        }
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft + marginLeft
            right = parent.width - parent.paddingRight - marginRight
            canvas.clipRect(left, parent.paddingTop, right,
                    parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom: Float = mBounds.bottom + child.translationY
            val top: Float = bottom - dividerHeight
            canvas.drawRect(left.toFloat(), top, right.toFloat(), bottom, paint)
        }
        canvas.restore()
    }

}