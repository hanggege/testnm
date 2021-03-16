package com.mei.im.ui.view.menu.view

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

/**
 *
 * @author Created by lenna on 2020/7/15
 */
class MenuGridLayoutManager constructor(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount) {
    override fun canScrollVertically(): Boolean {
        return false
    }

    override fun canScrollHorizontally(): Boolean {
        return false
    }
}