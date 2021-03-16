package com.mei.orc.util.click

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.TextView

/**
 * Created by Administrator on 2016/11/14.
 * TextView链接点击和长按冲突
 */
class ClickMovementMethod private constructor() : View.OnTouchListener {
    private var startDownTime: Long = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {

        val widget = v as TextView
        // MovementMethod设为空，防止消费长按事件
        widget.movementMethod = null
        val text = widget.text
        val spannable = Spannable.Factory.getInstance().newSpannable(text)
        val action = event.action
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY
            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())
            val link = spannable.getSpans(off, off, ClickableSpan::class.java)
            if (link.isNotEmpty()) {
                val longPressTime = ViewConfiguration.getLongPressTimeout().toLong()
                if (action == MotionEvent.ACTION_DOWN) {
                    startDownTime = System.currentTimeMillis()
                } else if (action == MotionEvent.ACTION_UP && System.currentTimeMillis() - startDownTime > longPressTime) {
                    widget.performLongClick()
                } else if (System.currentTimeMillis() - startDownTime < longPressTime) {
                    link[0].onClick(widget)
                }
                return true
            }
        }

        return false
    }


    companion object {

        fun newInstance(): ClickMovementMethod {
            return ClickMovementMethod()
        }
    }
}