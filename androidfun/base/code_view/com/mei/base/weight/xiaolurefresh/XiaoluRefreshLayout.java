package com.mei.base.weight.xiaolurefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.recyclerview.widget.RecyclerView;

import com.mei.widget.refresh.BaseRefreshLayout;
import com.mei.widget.refresh.HeaderParentLayout;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/6/14.
 */

public class XiaoluRefreshLayout extends BaseRefreshLayout {

    private CircleHeader xiaoluHeader;


    public XiaoluRefreshLayout(Context context) {
        super(context);
        // 触发移动事件的最短距离，如果小于这个距离就不触发移动控件
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public XiaoluRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 触发移动事件的最短距离，如果小于这个距离就不触发移动控件
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected HeaderParentLayout provideHeader(Context context) {
        return xiaoluHeader = new CircleHeader(context);
    }

    public void setRefreshText(CharSequence charSequence) {
        xiaoluHeader.setRefreshTv(charSequence);
    }


    @Override
    public void setRefreshing(boolean refreshing) {
        xiaoluHeader.setRefresh(refreshing);
        super.setRefreshing(refreshing);
        stateCheck();
    }


    /**
     * xiaolu 定制刷新
     */
    public void setXiaoRefreshing(final boolean refreshing) {
        if (refreshing) {
            setRefreshing(true);
        } else {
            //停止刷新播放动画
            UIHandler.postUi(() -> setRefreshing(false));
        }
    }


    private int mTouchSlop;
    // 上一次触摸时的X坐标
    private float mPrevX;
    private float mPrevY;

    private boolean lastState = false;
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

                stateCheck();
        }

        return super.onInterceptTouchEvent(event);
    }

    private void stateCheck() {
        if(isRefreshing() != lastState){
            lastState = isRefreshing();
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) child;
                    recyclerView.suppressLayout(lastState);
                }
            }
        }
    }
}
