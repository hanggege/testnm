package com.mei.widget.swipe

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import androidx.customview.widget.ViewDragHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mei.orc.R
import com.mei.orc.util.click.isNotOnDoubleClick
import kotlin.math.abs

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/1/9
 */

const val STATE_CLOSE = 0
const val STATE_OPEN_LEFT = 1
const val STATE_OPEN_RIGHT = 2

/**
 * 挂载到RecyclerView上，item有滑动菜单
 */
@SuppressLint("ClickableViewAccessibility")
fun RecyclerView.attachSwipeMenu() {
    adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            closeMenu()
        }
    })
    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_MOVE) {
            (layoutManager as? LinearLayoutManager)?.let { manager ->
                for (index in manager.findFirstVisibleItemPosition() until manager.findLastVisibleItemPosition()) {
                    findViewHolderForAdapterPosition(index)?.itemView?.apply {
                        isNotOnDoubleClick(300, this.id.toString()) {
                            closeMenu()
                        }
                    }
                }
            }
        }
        false
    }
}

/**
 * 关闭View下的所有菜单
 */
fun View.closeMenu() {
    if (this is SwipeMenuLayout) {
        swipe(STATE_CLOSE)
    }
    (this as? ViewGroup)?.forEach {
        it.closeMenu()
    }
}

class SwipeMenuLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {


    private var contentView: Pair<Int, View?> = Pair(View.NO_ID, null)
    private var leftMenuView: Pair<Int, View?> = Pair(View.NO_ID, null)
    private var rightMenuView: Pair<Int, View?> = Pair(View.NO_ID, null)

    private var valueAnimator: ValueAnimator? = null
    var swipeState = STATE_CLOSE
    var isLayoutFrozen = false

    init {
        val typeArr = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenuLayout)
        contentView = Pair(typeArr.getResourceIdOrThrow(R.styleable.SwipeMenuLayout_content_view), null)
        leftMenuView = Pair(typeArr.getResourceId(R.styleable.SwipeMenuLayout_left_menu_view, View.NO_ID), null)
        rightMenuView = Pair(typeArr.getResourceId(R.styleable.SwipeMenuLayout_right_menu_view, View.NO_ID), null)

        typeArr.recycle()
    }

    private val dragHelper: ViewDragHelper by lazy {
        ViewDragHelper.create(this, 1.0f, object : ViewDragHelper.Callback() {
            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                val leftMenu = leftMenuView.second
                val rightMenu = rightMenuView.second
                return when {
                    rightMenu == null && left < 0 -> 0
                    leftMenu == null && left > 0 -> 0
                    leftMenu != null && left > leftMenu.measuredWidth -> leftMenu.measuredWidth
                    rightMenu != null && left < -rightMenu.measuredWidth -> -rightMenu.measuredWidth
                    else -> left
                }
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int = 0
            override fun getViewVerticalDragRange(child: View): Int = 0

            override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
                rightMenuView.second?.translationX = left.toFloat()
                leftMenuView.second?.translationX = left.toFloat()

            }

            override fun tryCaptureView(child: View, pointerId: Int): Boolean = !isLayoutFrozen && child == contentView.second

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                val leftMenu = leftMenuView.second
                val rightMenu = rightMenuView.second
                val left = releasedChild.left
                when {
                    leftMenu != null && left > leftMenu.measuredWidth / 2 -> swipe(STATE_OPEN_LEFT)
                    rightMenu != null && left < -rightMenu.measuredWidth / 2 -> swipe(STATE_OPEN_RIGHT)
                    else -> swipe(STATE_CLOSE)
                }
            }
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        swipe(STATE_CLOSE)
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        forEach {
            if (it.id != View.NO_ID) {
                when (it.id) {
                    contentView.first -> contentView = Pair(it.id, it)
                    leftMenuView.first -> leftMenuView = Pair(it.id, it)
                    rightMenuView.first -> rightMenuView = Pair(it.id, it)
                }
            }
        }
        val contentRight = contentView.second!!.measuredWidth
        contentView.second?.let {
            it.layout(0, 0, contentRight, it.measuredHeight)
        }
        leftMenuView.second?.let {
            it.layout(-it.measuredWidth, 0, 0, it.measuredHeight)
        }
        rightMenuView.second?.let {
            it.layout(contentRight, 0, contentRight + it.measuredWidth, it.measuredHeight)
        }

    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = dragHelper.shouldInterceptTouchEvent(ev)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            invalidate()
        }
    }


    /**
     * 选中所需的状态
     */
    fun swipe(state: Int) {
        if (!isLayoutFrozen) {
            val contentLeft = contentView.second!!.left
            val offset = when {
                state == STATE_OPEN_LEFT && leftMenuView.second != null -> leftMenuView.second!!.measuredWidth - contentLeft
                state == STATE_OPEN_RIGHT && rightMenuView.second != null -> -rightMenuView.second!!.measuredWidth - contentLeft
                else -> -contentLeft
            }
            swipeState = state
            valueAnimator?.cancel()
            if (abs(offset) > 0) {
                var lastValue = 0
                valueAnimator = ValueAnimator.ofInt(0, offset).apply {
                    addUpdateListener {
                        val value = it.animatedValue as Int
                        ViewCompat.offsetLeftAndRight(contentView.second!!, value - lastValue)
                        lastValue = value
                        rightMenuView.second?.translationX = contentView.second!!.left.toFloat()
                        leftMenuView.second?.translationX = contentView.second!!.left.toFloat()
                    }
                    addListener(onEnd = {
                        valueAnimator = null
                    })
                    duration = 200
                    start()
                }
            }
        }
    }
}