package com.mei.browser.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/17
 */
class HackyViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ViewPager(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return try {
            super.onInterceptTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}