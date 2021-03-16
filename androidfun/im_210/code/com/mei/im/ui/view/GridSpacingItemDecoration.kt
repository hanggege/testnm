package com.mei.im.ui.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.im.ui.view
 * @ClassName:      GridSpacingItemDecoration
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/6/12 18:09
 * @UpdateUser:
 * @UpdateDate:     2020/6/12 18:09
 * @UpdateRemark:
 * @Version:
 */
class GridSpacingItemDecoration(private var spanCount: Int, private var spacing: Int, private var rude: Int, private var includeEdge: Boolean) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position: Int = parent.getChildAdapterPosition(view) - rude
        val column = position % spanCount
        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }

    }
}