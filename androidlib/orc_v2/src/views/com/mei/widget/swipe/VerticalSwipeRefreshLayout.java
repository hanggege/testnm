package com.mei.widget.swipe;

/**
 * Created by joker on 16/6/21.
 * 只在竖直方向才能下拉刷新的控件
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class VerticalSwipeRefreshLayout extends SwipeRefreshLayout {

    private int mTouchSlop;
    // 上一次触摸时的X坐标
    private float mPrevX;
    private float mPrevY;

    public VerticalSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 触发移动事件的最短距离，如果小于这个距离就不触发移动控件
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = event.getX();
                mPrevY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                final float eventY = event.getY();
                float xDiff = Math.abs(eventX - mPrevX);
                float yDiff = Math.abs(eventY - mPrevY);
                // Log.d("refresh" ,"move----" + eventX + "   " + mPrevX + "   " + mTouchSlop);
                // 增加60的容差，让下拉刷新在竖直滑动时就可以触发
                if (xDiff - yDiff > mTouchSlop) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(event);
    }
}
