package com.mei.me.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.customview.widget.ViewDragHelper
import com.mei.orc.ext.dip
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020-01-08
 */
class InviteDragView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {

    var offsetTop = -1
    val dragHelper: ViewDragHelper by lazy {
        ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean = child == iconImageView

            override fun getViewHorizontalDragRange(child: View): Int = this@InviteDragView.measuredWidth - iconImageView.measuredWidth
            override fun getViewVerticalDragRange(child: View): Int = this@InviteDragView.measuredHeight - iconImageView.measuredHeight
            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = when {
                left < 0 -> 0
                left > (measuredWidth - child.measuredWidth) -> measuredWidth - child.measuredWidth
                else -> left
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int = when {
                top < 0 -> 0
                top > (measuredHeight - child.measuredHeight) -> measuredHeight - child.measuredHeight
                else -> top
            }

            override fun onViewReleased(child: View, xvel: Float, yvel: Float) {
                super.onViewReleased(child, xvel, yvel)
                offsetTop = child.top
                dragHelper.smoothSlideViewTo(child, measuredWidth - child.measuredWidth, child.top)
                invalidate()
            }
        })
    }
    val iconImageView: ImageView by lazy {
        ImageView(context).apply {
            layoutParams = LayoutParams(dip(70), dip(70))
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(R.drawable.invite_matchmaker_img)
        }
    }

    init {
        addView(iconImageView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (offsetTop > 0) {
            iconImageView.top = offsetTop
            iconImageView.bottom = iconImageView.top + iconImageView.measuredHeight
            iconImageView.left = measuredWidth - iconImageView.measuredWidth
            iconImageView.right = measuredWidth
        } else {
            iconImageView.top = measuredHeight - iconImageView.measuredHeight
            iconImageView.bottom = iconImageView.top + iconImageView.measuredHeight
            iconImageView.left = measuredWidth - iconImageView.measuredWidth
            iconImageView.right = measuredWidth
        }
    }


    var intercept = false
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            intercept = ev.x.toInt() in iconImageView.left..(iconImageView.left + iconImageView.measuredWidth)
                    && ev.y.toInt() in iconImageView.top..(iconImageView.top + iconImageView.measuredHeight)
        }
        return intercept && dragHelper.shouldInterceptTouchEvent(ev)

    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            intercept = ev.x.toInt() in iconImageView.left..(iconImageView.left + iconImageView.measuredWidth)
                    && ev.y.toInt() in iconImageView.top..(iconImageView.top + iconImageView.measuredHeight)
        }
        if (intercept) dragHelper.processTouchEvent(ev)
        return intercept
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            postInvalidateOnAnimation()
        }
    }
}