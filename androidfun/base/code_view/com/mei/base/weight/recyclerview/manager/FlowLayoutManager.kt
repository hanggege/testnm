package com.mei.base.weight.recyclerview.manager

import android.graphics.Rect
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @author Created by LingYun on 2017/10/9.
 * 流式布局
 * modify by hang on 2018/5/27.
 * 加了水平间隔的流式布局
 */

class FlowLayoutManager(var mHorizontalOffset: Int = 0) : RecyclerView.LayoutManager() {

    private var mVerticalOffset: Int = 0//竖直偏移量 每次换行时，要根据这个offset判断
    private var mFirstVisPos: Int = 0//屏幕可见的第一个View的Position
    private var mLastVisPos: Int = 0//屏幕可见的最后一个View的Position
    var isScrollEnabled = true//可否滑动

    private val mItemRects: SparseArray<Rect> = SparseArray()//key 是View的position，保存View的bounds 和 显示标志，

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {//没有Item，界面空着吧
            detachAndScrapAttachedViews(recycler)
            return
        }
        if (childCount == 0 && state.isPreLayout) {//state.isPreLayout()是支持动画的
            return
        }
        //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
        detachAndScrapAttachedViews(recycler)

        //初始化区域
        mVerticalOffset = 0
        mFirstVisPos = 0
        mLastVisPos = itemCount

        //初始化时调用 填充childView
        fill(recycler, state)
    }

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        //位移0、没有子View 当然不移动
        if (dy == 0 || childCount == 0) {
            return 0
        }
        var realOffset = dy//实际滑动的距离， 可能会在边界处被修复
        //边界修复代码
        if (mVerticalOffset + realOffset < 0) {//上边界
            realOffset = -mVerticalOffset
        } else if (realOffset > 0) {//下边界
            //利用最后一个子View比较修正
            val lastChild = getChildAt(childCount - 1)
            if (getPosition(lastChild!!) == itemCount - 1) {
                val gap = height - paddingBottom - getDecoratedBottom(lastChild)
                realOffset = when {
                    gap > 0 -> -gap
                    gap == 0 -> 0
                    else -> Math.min(realOffset, -gap)
                }
            }
        }
        realOffset = fill(recycler, state, realOffset)//先填充，再位移。
        mVerticalOffset += realOffset//累加实际滑动距离
        offsetChildrenVertical(-realOffset)//滑动
        return realOffset
    }

    /**
     * 初始化时调用 填充childView
     */
    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        fill(recycler, state, 0)
    }

    /**
     * 填充childView的核心方法,应该先填充，再移动。
     * 在填充时，预先计算dy的在内，如果View越界，回收掉。
     * 一般情况是返回dy，如果出现View数量不足，则返回修正后的dy.
     *
     * @param dy RecyclerView给我们的位移量,+,显示底端， -，显示头部
     * @return 修正以后真正的dy（可能剩余空间不够移动那么多了 所以return <|dy|）
     */
    @Suppress("UNUSED_PARAMETER")
    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State, dy: Int): Int {
        var dyCopy = dy
        var topOffset = paddingTop
        //回收越界子View
        if (childCount > 0) {//滑动时进来的
            for (i in childCount - 1 downTo 0) {
                val child = getChildAt(i)
                child?.let {
                    if (dyCopy > 0) {//需要回收当前屏幕，上越界的View
                        if (getDecoratedBottom(it) - dyCopy < topOffset) {
                            removeAndRecycleView(it, recycler)
                            mFirstVisPos++
                        }
                    } else if (dyCopy < 0) {//回收当前屏幕，下越界的View
                        if (getDecoratedTop(it) - dyCopy > height - paddingBottom) {
                            removeAndRecycleView(it, recycler)
                            mLastVisPos--
                        }
                    }
                }

            }
        }

        var leftOffset = paddingLeft
        var lineMaxHeight = 0
        //布局子View阶段
        if (dyCopy >= 0) {
            var minPos = mFirstVisPos
            mLastVisPos = itemCount - 1
            if (childCount > 0) {
                val lastView = getChildAt(childCount - 1)
                lastView?.let {
                    minPos = getPosition(it) + 1//从最后一个View+1开始吧
                    topOffset = getDecoratedTop(it)
                    leftOffset = getDecoratedRight(it)
                    lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(it))
                }
            }
            var horizontalOffset: Int
            //顺序addChildView
            for (i in minPos..mLastVisPos) {
                horizontalOffset = if (i == 0) 0 else mHorizontalOffset
                //找recycler要一个childItemView,我们不管它是从scrap里取，还是从RecyclerViewPool里取，亦或是onCreateViewHolder里拿。
                val child = recycler.getViewForPosition(i)
                addView(child)
                measureChildWithMargins(child, 0, 0)
                //计算宽度 包括margin
                if (leftOffset + horizontalOffset + getDecoratedMeasurementHorizontal(child) <= getHorizontalSpace()) {//当前行还排列的下
                    layoutDecoratedWithMargins(child, leftOffset + horizontalOffset, topOffset, leftOffset + horizontalOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child))

                    //保存Rect供逆序layout用
                    val rect = Rect(leftOffset + horizontalOffset, topOffset + mVerticalOffset, leftOffset + horizontalOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child) + mVerticalOffset)
                    mItemRects.put(i, rect)

                    //改变 left  lineHeight
                    leftOffset += getDecoratedMeasurementHorizontal(child) + horizontalOffset
                    lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child))
                } else {//当前行排列不下
                    //改变top  left  lineHeight
                    leftOffset = paddingLeft
                    topOffset += lineMaxHeight
                    lineMaxHeight = 0

                    //新起一行的时候要判断一下边界
                    if (height > 0 && topOffset - dyCopy > height - paddingBottom) {
                        //越界了 就回收
                        removeAndRecycleView(child, recycler)
                        mLastVisPos = i - 1
                    } else {
                        layoutDecoratedWithMargins(child, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child))

                        //保存Rect供逆序layout用
                        val rect = Rect(leftOffset, topOffset + mVerticalOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child) + mVerticalOffset)
                        mItemRects.put(i, rect)

                        //改变 left  lineHeight
                        leftOffset += getDecoratedMeasurementHorizontal(child)
                        lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child))
                    }
                }
            }
            //添加完后，判断是否已经没有更多的ItemView，并且此时屏幕仍有空白，则需要修正dy
            val lastChild = getChildAt(childCount - 1)
            if (getPosition(lastChild!!) == itemCount - 1) {
                val gap = height - paddingBottom - getDecoratedBottom(lastChild)
                if (gap > 0) {
                    dyCopy -= gap
                }

            }

        } else {
            /**
             *
             * ##  利用Rect保存子View边界
             * 正序排列时，保存每个子View的Rect，逆序时，直接拿出来layout。
             */
            var maxPos = itemCount - 1
            mFirstVisPos = 0
            if (childCount > 0) {
                val firstView = getChildAt(0)
                maxPos = getPosition(firstView!!) - 1
            }
            for (i in maxPos downTo mFirstVisPos) {
                val rect = mItemRects.get(i)

                if (rect.bottom - mVerticalOffset - dyCopy < paddingTop) {
                    mFirstVisPos = i + 1
                    break
                } else {
                    val child = recycler.getViewForPosition(i)
                    addView(child, 0)//将View添加至RecyclerView中，childIndex为1，但是View的位置还是由layout的位置决定
                    measureChildWithMargins(child, 0, 0)

                    layoutDecoratedWithMargins(child, rect.left, rect.top - mVerticalOffset, rect.right, rect.bottom - mVerticalOffset)
                }
            }
        }
        Log.d("TAG", "count= [" + childCount + "]" + ",[recycler.getScrapList().size():" + recycler.scrapList.size + ", dyCopy:" + dyCopy + ",  mVerticalOffset" + mVerticalOffset + ", ")
        return dyCopy
    }

    /**
     * 获取某个childView在水平方向所占的空间
     */
    private fun getDecoratedMeasurementHorizontal(view: View): Int {
        val params = view.layoutParams as? RecyclerView.LayoutParams
        return (getDecoratedMeasuredWidth(view) + (params?.leftMargin ?: 0) + (params?.rightMargin
                ?: 0))
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     */
    private fun getDecoratedMeasurementVertical(view: View): Int {
        val params = view.layoutParams as? RecyclerView.LayoutParams
        return (getDecoratedMeasuredHeight(view) + (params?.topMargin ?: 0) + (params?.bottomMargin
                ?: 0))
    }

    private fun getVerticalSpace(): Int {
        return height - paddingTop - paddingBottom
    }

    private fun getHorizontalSpace(): Int {
        return width - paddingLeft - paddingRight
    }

}