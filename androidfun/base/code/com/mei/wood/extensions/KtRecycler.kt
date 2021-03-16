package com.mei.wood.extensions

import android.os.SystemClock
import android.view.MotionEvent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/7/31
 */

/**
 * 强制停止滑动
 */
fun RecyclerView?.forceStopRecyclerViewScroll() {
    this?.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0f, 0f, 0))
}

/**
 * [itemChange] 当前对象是否改变
 * [contentChange] 对象中的元素是否改变
 */
fun <T> RecyclerView.notifyDiff(oldList: List<T>, newList: List<T>,
                                itemChange: (T, T) -> Boolean = { old, new -> old == new },
                                contentChange: (T, T) -> Boolean) {
    val diffResult = DiffUtil.calculateDiff(DataDiffCallBack(oldList, newList, itemChange, contentChange))
    adapter?.let { diffResult.dispatchUpdatesTo(it) }

}


private class DataDiffCallBack<T>(private val oldList: List<T>, private val newList: List<T>,
                                  val itemChange: (T, T) -> Boolean,
                                  val contentChange: (T, T) -> Boolean) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return !itemChange(oldList[oldItemPosition], newList[newItemPosition])
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return !contentChange(oldList[oldItemPosition], newList[newItemPosition])
    }

}
