package com.mei.video.browser.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.abs

/**
 *
 * @author Created by lenna on 2020/8/21
 */
class CustomConstraintLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    private var startX: Float = 0f
    private var startY: Float = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        var intercept = false
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
                intercept = false
            }
            MotionEvent.ACTION_MOVE -> {
                //来到新的坐标
                val endX = ev.x
                val endY = ev.y
                //计算偏移量
                val distanceX = endX - startX
                val distanceY = endY - startY
                //判断滑动方向
                intercept = abs(distanceX) > abs(distanceY)
            }
        }
        return intercept
    }

}