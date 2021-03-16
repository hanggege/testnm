package com.mei.base.weight.viewpager

import android.content.Context
import android.os.Build
import android.view.animation.Interpolator
import android.widget.Scroller

import androidx.viewpager.widget.ViewPager

/**
 * Created by hang on 2019/3/4.
 * ViewPage滑动速度
 */
class ViewPagerScroller constructor(context: Context, interpolator: Interpolator? = null,
                                    flywheel: Boolean = context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.HONEYCOMB)
    : Scroller(context, interpolator, flywheel) {

    private var mScrollDuration = 2000 // 滑动速度

    /**
     * 设置速度速度
     *
     * @param duration
     */
    fun setScrollDuration(duration: Int) {
        mScrollDuration = duration
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration)
    }

    fun initViewPagerScroll(viewPager: ViewPager) {
        try {
            val mScroller = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = true
            mScroller.set(viewPager, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
