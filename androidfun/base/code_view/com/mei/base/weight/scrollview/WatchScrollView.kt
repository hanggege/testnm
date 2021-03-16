package com.mei.base.weight.scrollview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.ScrollView

/**
 * Created by Ling on 2018/4/9.
 *
 * 描述：可监听页面滚动的ScrollView
 */
class WatchScrollView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : ScrollView(context, attributeSet, defStyleAttr) {

    private var downYPoint: Float = 0.toFloat()
    private val dis = ViewConfiguration.get(context).scaledTouchSlop.toFloat()

    private var mOnVerticalScrollWatcher: OnVerticalScrollWatcher? = null

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> downYPoint = ev.y
            MotionEvent.ACTION_MOVE -> {
                val distance = Math.abs(ev.y - downYPoint)
                if (distance > dis && mOnVerticalScrollWatcher != null) {
                    mOnVerticalScrollWatcher!!.onVerticalTouchChanged()
                }
            }
            MotionEvent.ACTION_UP -> performClick()
        }
        return super.onTouchEvent(ev)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (childCount < 1) return
        if (mOnVerticalScrollWatcher != null) {
            mOnVerticalScrollWatcher!!.onSizeChanged(getChildAt(0).height < h)
        }
    }

    fun setOnVerticalScrollWatcher(watcher: OnVerticalScrollWatcher) {
        mOnVerticalScrollWatcher = watcher
    }

    interface OnVerticalScrollWatcher {

        fun onVerticalTouchChanged()

        fun onSizeChanged(isChildShorter: Boolean)
    }
}
