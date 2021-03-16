package com.mei.video

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.abs

/**
 * Created by hang on 2020/9/2.
 */

class InterceptConstraintLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        ConstraintLayout(context, attrs) {

    private var startX = 0f
    private var startY = 0f

    private var intercept = false

    fun setIsIntercept(intercept: Boolean) {
        this.intercept = intercept
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val currX = ev.x
                val currY = ev.y
                if (startX > currX && abs(currX - startX) > abs(currY - startY) && intercept) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}