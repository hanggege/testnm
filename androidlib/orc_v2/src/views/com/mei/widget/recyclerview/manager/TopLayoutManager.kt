package com.mei.widget.recyclerview.manager

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by hang on 2019/2/18.
 * 自定义layoutManager：调用smoothScrollToPosition滚动的position条目置顶
 */
class TopLayoutManager(context: Context, @RecyclerView.Orientation orientation: Int, reverse: Boolean) :
        LinearLayoutManager(context, orientation, reverse) {


    override fun smoothScrollToPosition(recyclerView: RecyclerView,
                                        state: RecyclerView.State, position: Int) {

        val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            // 返回：滑过1px时经历的时间(ms)。
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 100f / displayMetrics.densityDpi
            }

            override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                return boxStart - viewStart
            }
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }
}
