package com.mei.mentor_home_page.wiget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import java.lang.reflect.Field


/**
 *
 * @author Created by lenna on 2020/6/2
 */
class CustomBehavior(context: Context, attrs: AttributeSet) : AppBarLayout.Behavior(context, attrs) {
    private var mScroller: OverScroller? = null

    init {
        getParentScroller(context)
    }

    /**
     * 反射获得滑动属性。
     *
     * @param context
     */
    private fun getParentScroller(context: Context) {
        if (mScroller != null) return
        mScroller = OverScroller(context)
        try {
            val reflexClass: Class<*>? = javaClass.superclass?.superclass //父类AppBarLayout.Behavior父类的父类HeaderBehavior
            val fieldScroller: Field = reflexClass!!.getDeclaredField("mScroller")
            fieldScroller.isAccessible = true
            fieldScroller.set(this, mScroller)
        } catch (e: Exception) {
        }
    }


    //fling上滑appbar然后迅速fling下滑recycler时, HeaderBehavior的mScroller并未停止, 会导致上下来回晃动
    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (mScroller != null) { //当recyclerView 做好滑动准备的时候 直接干掉Appbar的滑动
            if (mScroller!!.computeScrollOffset()) {
                mScroller!!.abortAnimation()
            }
        }
        if (type == ViewCompat.TYPE_NON_TOUCH && topAndBottomOffset == 0) { //recyclerview的惯性比较大 ,会顶在头部一会儿, 到头直接干掉它的滑动
            ViewCompat.stopNestedScroll(target, type)
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
            }
        }
        return super.onTouchEvent(parent, child, e)
    }
}

