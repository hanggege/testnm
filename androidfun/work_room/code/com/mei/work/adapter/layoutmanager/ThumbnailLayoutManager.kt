package com.mei.work.adapter.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs


/**
 * ThumbnailLayoutManager
 *
 * 工作室封面缩放动画
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-28
 */
class ThumbnailLayoutManager(context: Context) : LinearLayoutManager(context, HORIZONTAL, false) {

    var shrinkRatio: Float = 0.882f


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        scaleItemView()
    }


    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        scaleItemView()
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    private fun scaleItemView() {
        (0 until childCount).mapNotNull { getChildAt(it) }.forEach {
            // 移动缩放的距离
            val scaleWidth = it.measuredWidth
            // 当前控件放大到的最大的X点
            val maxPointX = paddingLeft
            val currentLeft = getDecoratedLeft(it)
            val currentRight = getDecoratedRight(it)
            val scaleRadio: Float = when {
                scaleWidth == 0 -> 1f// 本身控件==0，没必要处理了
                currentLeft >= maxPointX + scaleWidth -> 1f // 如果控件的X位置远远大于缩放的空间内，不去计算
                currentRight <= maxPointX -> 1f // 如果控件的X位置远远小于缩放的空间内，不去计算
                else -> abs(currentLeft - maxPointX) / scaleWidth.toFloat()
            }
            it.scaleX = 1 - (1 - shrinkRatio) * scaleRadio
            it.scaleY = it.scaleX

        }
    }


}