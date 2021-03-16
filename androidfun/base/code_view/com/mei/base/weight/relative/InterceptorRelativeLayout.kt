package com.mei.base.weight.relative

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout

/**
 * Created by LingYun on 2017/4/19.
 * 拦截点击事件
 */

class InterceptorRelativeLayout constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    private var interceptClick: Boolean = false

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return interceptClick || super.onInterceptTouchEvent(ev)
    }

    fun setInterceptClick(interceptClick: Boolean) {
        this.interceptClick = interceptClick
    }
}
