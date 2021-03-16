package com.mei.player.view

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/7
 */
abstract class FloatDragView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    abstract val dragItemView: ViewGroup

    private val dragHelper: ViewDragHelper by lazy {
        ViewDragHelper.create(this, 1.0f, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean = dragItemView == child
            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = left
            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int = top
            override fun getViewHorizontalDragRange(child: View): Int = measuredWidth - child.measuredWidth
            override fun getViewVerticalDragRange(child: View): Int = measuredHeight - child.measuredHeight

            override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
                super.onViewPositionChanged(changedView, left, top, dx, dy)
                dragItemView.left = left
                dragItemView.top = top
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                super.onViewReleased(releasedChild, xvel, yvel)
                val childCenterX = releasedChild.left + releasedChild.measuredWidth / 2
                val parentCenterX = measuredWidth / 2
                val left = if (childCenterX > parentCenterX) measuredWidth - releasedChild.measuredWidth else 0

                val maxTop = measuredHeight - releasedChild.measuredHeight
                val top = when {
                    releasedChild.top < 0 -> 0
                    releasedChild.top > maxTop -> maxTop
                    else -> releasedChild.top
                }
                dragHelper.smoothSlideViewTo(releasedChild, left, top)
                endDrag(top, left)
                invalidate()
            }
        })
    }

    abstract fun endDrag(top: Int, left: Int)

    /** 手指首次点击屏幕的坐标  **/
    private val touchPoint = Point()

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return checkIsRect(event) && dragHelper.shouldInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return checkIsRect(event)
    }

    fun checkIsRect(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            touchPoint.x = event.x.toInt()
            touchPoint.y = event.y.toInt()
        }
        /**点击卡片上的位置才拦截事件**/
        var intercept: Boolean
        dragItemView.apply {
            intercept = touchPoint.x in left..(left + measuredWidth)
                    && touchPoint.y in top..(top + measuredHeight)
        }
        return intercept
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            invalidate()
        }
    }
}