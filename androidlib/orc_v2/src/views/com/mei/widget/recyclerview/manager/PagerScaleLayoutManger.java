package com.mei.widget.recyclerview.manager;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/8
 * <p>
 * 用于轮播图及卡片等Pager  替换ViewPager
 */
public class PagerScaleLayoutManger extends LinearLayoutManager {
    private PagerSnapHelper pagerSnapHelper;
    private onSelectedListener selectedListener;
    private float MILLISECONDS_PER_INCH = 0.3f;
    @FloatRange(from = 0, to = 1)
    private float scaleValue;


    public PagerScaleLayoutManger(Context context, int orientation, @FloatRange(from = 0, to = 1) float scale) {
        super(context, orientation, false);
        scaleValue = 1.0f - scale;

    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        view.setOnFlingListener(null);
        pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(view);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getItemCount() < 0 || state.isPreLayout()) return;
        if (getOrientation() == RecyclerView.HORIZONTAL) {
            scaleHorizontalChildView();
        } else if (getOrientation() == RecyclerView.VERTICAL) {
            scaleVerticalChildView();
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scaleHorizontalChildView();
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scaleVerticalChildView();
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    /**
     * 横向移动缩放
     */
    private void scaleHorizontalChildView() {
        float mid = getWidth() / 2.0f;
        float moveDistance = mid;
        if (getChildCount() > 1) {
            moveDistance = Math.abs(getHorizontalMiddlePosition(getChildAt(0)) - getHorizontalMiddlePosition(getChildAt(1)));
        }
        /**超出屏幕的位移**/
        float outScreenSize = moveDistance - mid;

        for (int i = 0; i < getChildCount() && getChildCount() > 1; i++) {
            View child = getChildAt(i);
            float childMid = getHorizontalMiddlePosition(child);
            childMid += outScreenSize;
            float scale = 1.0f - Math.abs((childMid - moveDistance) / moveDistance * scaleValue);
            child.setScaleX(scale);
            child.setScaleY(scale);
        }
    }


    /**
     * 横向移动缩放
     */
    private void scaleVerticalChildView() {
        float mid = getHeight() / 2.0f;
        float moveDistance = mid;
        if (getChildCount() > 1) {
            moveDistance = Math.abs(getVerticalMiddlePosition(getChildAt(0)) - getVerticalMiddlePosition(getChildAt(1)));
        }
        /**超出屏幕的位移**/
        float outScreenSize = moveDistance - mid;

        for (int i = 0; i < getChildCount() && getChildCount() > 1; i++) {
            View child = getChildAt(i);
            float childMid = getVerticalMiddlePosition(child);
            childMid += outScreenSize;
            float scale = 1.0f - Math.abs((childMid - moveDistance) / moveDistance * scaleValue);
            child.setScaleX(scale);
            child.setScaleY(scale);
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return PagerScaleLayoutManger.this.computeScrollVectorForPosition(targetPosition);
                    }

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH / displayMetrics.density;
                        //返回滑动一个pixel需要多少毫秒
                    }

                    @Override
                    public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                        return boxStart - viewStart;
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);

    }

    /**
     * 获取view横向的中心位置
     */
    private float getHorizontalMiddlePosition(View view) {
        return (getDecoratedLeft(view) + getDecoratedRight(view)) / 2;
    }

    /**
     * 获取view竖向的中心位置
     */
    private float getVerticalMiddlePosition(View view) {
        return (getDecoratedBottom(view) + getDecoratedTop(view)) / 2;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (pagerSnapHelper != null) {
                View view = pagerSnapHelper.findSnapView(this);
                if (view != null) {
                    int position = getPosition(view);
                    selectedView(view, position);
                }
            }
        }
    }

    public int getCurrentPosition() {
        View snapView = pagerSnapHelper.findSnapView(this);
        return snapView == null ? 0 : getPosition(snapView);
    }

    public void setScrollDuration(float millionSecondPerInch) {
        MILLISECONDS_PER_INCH = millionSecondPerInch;
    }

    /**
     * 选中目标
     *
     * @param view
     * @param position
     */
    protected void selectedView(View view, int position) {
        if (selectedListener != null) {
            selectedListener.onSelectedView(view, position);
        }
    }

    public void setSelectedListener(onSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public interface onSelectedListener {
        void onSelectedView(View view, int position);
    }
}
