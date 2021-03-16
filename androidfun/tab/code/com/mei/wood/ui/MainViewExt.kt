package com.mei.wood.ui

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.mei.orc.ext.dip
import com.mei.wood.R
import com.mei.wood.view.TabMenuView

/**
 *  Created by zzw on 2019-10-24
 *  Des: tab_main_layout.xml
 */

val contentViewIds = arrayListOf(R.id.tab_content_view1, R.id.tab_content_view2, R.id.tab_content_view3, R.id.tab_content_view4, R.id.tab_content_view5)

fun TabMainActivity.createView(): FrameLayout = FrameLayout(this).apply {
    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    val contentViews: List<ViewGroup> by lazy {
        contentViewIds.map { ID ->
            FrameLayout(context).apply {
                id = ID
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            }
        }
    }

    val main_tab_bar: TabMenuView by lazy {
        TabMenuView(context).apply {
            id = R.id.main_tab_bar
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dip(65)).apply {
                gravity = Gravity.BOTTOM
            }
        }
    }

    val contentViewRoot: View by lazy {
        FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setPadding(0, 0, 0, dip(50))
            contentViews.forEach { addView(it) }
        }
    }

    addView(contentViewRoot)
    addView(main_tab_bar)

}