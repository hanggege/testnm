package com.mei.me.ext

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView


/**
 * @method
 * @author: zxj
 * @param
 * @return
 */
class VideoImageBrowserLayoutManager(context: Context) : LinearLayoutManager(context, HORIZONTAL, false),
        RecyclerView.OnChildAttachStateChangeListener {


    val pagerSpaner: PagerSnapHelper by lazy { PagerSnapHelper() }
    var viewPagerListener: OnViewPagerListener? = null
    var diffY = 0

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        view.addOnChildAttachStateChangeListener(this)
        pagerSpaner.attachToRecyclerView(view)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun onScrollStateChanged(state: Int) {
        if (RecyclerView.SCROLL_STATE_IDLE == state) {
            val view = pagerSpaner.findSnapView(this)
            val position = view?.let { getPosition(it) }
            position?.let { viewPagerListener?.onPageSelected(it, position == itemCount - 1) }
        }
        super.onScrollStateChanged(state)
    }

    public fun setOnViewPagerListener(listener: OnViewPagerListener) {
        viewPagerListener = listener
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        diffY = dx
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        val position = getPosition(view)
        if (0 < diffY) {
            viewPagerListener?.onPageRelease(true, position)
        } else {
            viewPagerListener?.onPageRelease(false, position)
        }
    }

    override fun onChildViewAttachedToWindow(view: View) {
        val position = getPosition(view)
        if (0 == position) {
            viewPagerListener?.onPageSelected(position, false)
        }
    }


}