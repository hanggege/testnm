package com.mei.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-24
 */
class PhotoRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RecyclerView(context, attrs, defStyleAttr) {

    private var isLock: Boolean = false// 是否锁住 RecyclerView ，避免和 PhotoView 双指放大缩小操作冲突

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        when (e?.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> isLock = true // 非第一个触点按下
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isLock = false
        }
        if (isLock) {
            return false// 不拦截，交给子View处理
        }
        return super.onInterceptTouchEvent(e)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> isLock = true
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isLock = false
        }
        return super.dispatchTouchEvent(ev)
    }
}
