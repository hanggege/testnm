package com.mei.me

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by hang on 2020/9/4.
 */
class PageSnapEdgeHelper : PagerSnapHelper() {


    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        if (layoutManager is LinearLayoutManager && isAtEdgeOfList(layoutManager)) {
            return null
        }
        return super.findSnapView(layoutManager)
    }

    private fun isAtEdgeOfList(lm: LinearLayoutManager): Boolean {
        return lm.findFirstCompletelyVisibleItemPosition() == 0
                || lm.findLastCompletelyVisibleItemPosition() == lm.itemCount - 1
    }
}