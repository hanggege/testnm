package com.mei.browser.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/23
 */
class PagerRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RecyclerView(context, attrs, defStyleAttr) {

    val snapHelper = PagerSnapHelper()
    private var pagerSelected: (old: Int, new: Int, holder: ViewHolder?) -> Unit = { _, _, _ -> }
    var currentPosition = 0

    fun setOnViewPagerListener(pagerSelected: (old: Int, new: Int, holder: ViewHolder?) -> Unit) {
        this.pagerSelected = pagerSelected
    }

    /**只有一条数据时上滑监听*/
    private var scrollLastItemListener: () -> Unit = {}
    fun setScrollLastItemListener(scrollLastItemListener: () -> Unit = {}) {
        this.scrollLastItemListener = scrollLastItemListener
    }

    var scrollOffsetY = 0

    init {
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        snapHelper.attachToRecyclerView(this)
        addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrollOffsetY += dy
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                snapHelper.findSnapView(layoutManager)?.let { childView ->
                    val position = layoutManager?.getPosition(childView) ?: -1
                    if (newState == SCROLL_STATE_DRAGGING && position == 0 && currentPosition == 0) {
                        val startScroll = scrollOffsetY
                        postDelayed({
                            if (scrollOffsetY == startScroll) {
                                scrollLastItemListener()
                            }
                        }, 100)
                    }

                    if (position > -1 && position != currentPosition) {
                        pagerSelected(currentPosition, position, getChildViewHolder(childView))
                        currentPosition = position
                    }
                }
            }
        })
    }

    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(position)
        pagerSelected(currentPosition, position, null)
        currentPosition = position
    }

    var isLock = false
    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        when (e?.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> isLock = true
            MotionEvent.ACTION_UP -> isLock = false
        }
        if (isLock) {
            return false
        }
        return super.onInterceptTouchEvent(e)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> isLock = true
            MotionEvent.ACTION_UP -> isLock = false
        }
        return super.dispatchTouchEvent(ev)
    }

}