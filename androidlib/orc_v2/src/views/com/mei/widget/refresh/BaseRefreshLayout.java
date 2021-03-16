package com.mei.widget.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;

/**
 * 佛祖保佑         永无BUG
 * 下拉刷新任意控件
 *
 * @author Created by joker on 2017/5/26.
 */

public class BaseRefreshLayout extends BaseNestedScrollLayout implements NestedScrollingParent,
        NestedScrollingChild {

    private static final String LOG_TAG = BaseRefreshLayout.class.getSimpleName();

    private OnRefreshListener mRefreshListener;
    private OnLoadMoreListener mloadMoreListener;
    private boolean mRefreshing = false;
    private boolean mLoadMore = false;

    private int mTouchSlop;
    private int mCurrentTargetOffsetTop = 0;
    private int mCurrentTargetOffsetBottom = 0;
    private float density;

    private float mInitialMotionY;//操作的初始位置
    private float mInitialDownY;//点击时的位置
    private boolean mIsBeingDraggedDown;//向下拉，刷新
    private boolean mIsBeingDraggedUp;//向上拉，加载更多
    private int mActivePointerId = INVALID_POINTER;

    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };

    protected int mHeaderOffsetDistance = 50;//header拉动后最后回到的位置，即刷新距离点

    protected int mFooterOffsetDistance = 50;//footer拉动后最后回到的位置，即刷新距离点


    private AnimationListener mRefreshAnim = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @SuppressLint("NewApi")
        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                // Make sure the progress view is fully visible
                if (mRefreshListener != null) {
                    mRefreshListener.onRefresh();
                }
                if (mHeaderView != null) {
                    mHeaderView.setRefresh(true);
                }
            } else if (mLoadMore) {
                if (mloadMoreListener != null) {
                    mloadMoreListener.onLoadMore();
                }
                if (mFooterView != null) {
                    mFooterView.setLoadMore(true);
                }
            } else {
                reset();
            }
            if (mTarget != null) {
                mCurrentTargetOffsetTop = mTarget.getTop();
                mCurrentTargetOffsetBottom = mTarget.getBottom() - mTarget.getMeasuredHeight();
            }
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mFooterView != null) mFooterView.bringToFront();
        if (mHeaderView != null) mHeaderView.bringToFront();
    }

    void reset() {
        if (mHeaderView != null) {
            mHeaderView.clearAnimation();
            mHeaderView.setVisibility(GONE);
        }
        if (mFooterView != null) {
            mFooterView.clearAnimation();
            mFooterView.setVisibility(GONE);
        }
        // Return the circle to its start position
        if (mCurrentTargetOffsetTop > 0) {
            setTargetOffsetTopAndBottom(0 - mCurrentTargetOffsetTop, true /* requires update */);
        } else if (mCurrentTargetOffsetBottom < 0) {
            setTargetOffsetTopAndBottom(0 - mCurrentTargetOffsetBottom, true);
        }
        if (mTarget != null) {
            mCurrentTargetOffsetTop = mTarget.getTop();
            mCurrentTargetOffsetBottom = mTarget.getBottom() - mTarget.getMeasuredHeight();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            reset();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    /**
     * Simple constructor to use when creating a RefreshView from code.
     */
    public BaseRefreshLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating RefreshView from XML.
     */
    public BaseRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        createHeaderView();
        createFooterView();
        density = getResources().getDisplayMetrics().density;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        setWillNotDraw(false);
        setChildrenDrawingOrderEnabled(true);
        // the absolute offset has to take into account that the circle starts at an offset
        setNestedScrollingEnabled(true);

        if (mHeaderView != null) {
            int offset = -mHeaderView.getMeasuredHeight() - mHeaderView.getTop();
            setTargetOffsetTopAndBottom(offset, false /* requires update */);
        }

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
    }


    private void createHeaderView() {
        mHeaderView = provideHeader(getContext());
        if (mHeaderView != null) {
            mHeaderView.setVisibility(View.GONE);
            addView(mHeaderView);
            mHeaderView.bringToFront();
        }
        ensureTarget();
    }

    private void createFooterView() {
        mFooterView = provideFooter(getContext());
        if (mFooterView != null) {
            mFooterView.setVisibility(View.GONE);
            addView(mFooterView);
            mFooterView.bringToFront();
        }
        ensureTarget();
    }

    protected HeaderParentLayout provideHeader(Context context) {
        return null;
    }

    protected FooterParentLayout provideFooter(Context context) {
        return null;
    }


    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (!mLoadMore && refreshing && mRefreshing != refreshing) {
            mRefreshing = refreshing;
            if (mHeaderView != null) {
                mHeaderView.setVisibility(View.VISIBLE);
            }
            animateOffsetToCorrectPosition(mRefreshAnim);
        } else {
            setRefreshingEnd(refreshing);
        }
    }

    public void setLoadMore(boolean loadMore) {
        if (!mRefreshing && loadMore && loadMore != mLoadMore) {
            mLoadMore = true;
            if (mFooterView != null) mFooterView.setVisibility(VISIBLE);
            animateFooterOffsetToCorrectPosition(mRefreshAnim);
        } else {
            setLoadMoreEnd(loadMore);
        }
    }


    private void setRefreshingEnd(boolean refreshing) {
        if (!mLoadMore && mRefreshing != refreshing) {
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mRefreshAnim);
            } else {
                animateOffsetToStartPosition(mRefreshAnim);
            }
        }
    }

    private void setLoadMoreEnd(boolean loadMore) {
        if (!mRefreshing && mLoadMore != loadMore) {
            ensureTarget();
            mLoadMore = loadMore;
            if (mLoadMore) {
                animateFooterOffsetToCorrectPosition(mRefreshAnim);
            } else {
                animateFooterOffsetToStartPosition(mRefreshAnim);
            }
        }
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mHeaderView) && !child.equals(mFooterView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {

        if (mHeaderView != null && !canChildScrollUp() && (!mHeaderView.needStickTop() || (mHeaderView.getTop() <= 0 || offset < 0))) {
            mHeaderView.bringToFront();
            ViewCompat.offsetTopAndBottom(mHeaderView, offset);
        }
        if (mFooterView != null && !canChildScrollDown() && (!mFooterView.needStickTop() || (mFooterView.getTop() >= 0 || offset > 0))) {
            mFooterView.bringToFront();
            ViewCompat.offsetTopAndBottom(mFooterView, offset);
        }
        if (mTarget != null && requiresUpdate) {
            ViewCompat.offsetTopAndBottom(mTarget, offset);
            mCurrentTargetOffsetTop = mTarget.getTop();
            mCurrentTargetOffsetBottom = mTarget.getBottom() - mTarget.getMeasuredHeight();
        }
        if (requiresUpdate) {
            invalidate();
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        refreshLayout();
    }

    private void refreshLayout() {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        if (mCurrentTargetOffsetTop < 0) {
            mCurrentTargetOffsetTop = 0;
        }
        if (mCurrentTargetOffsetBottom > 0) {
            mCurrentTargetOffsetBottom = 0;
        }

        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();

        int top = childTop;
        int bottom = childTop + childHeight;
        if (Math.abs(mCurrentTargetOffsetBottom) > Math.abs(mCurrentTargetOffsetTop)
                && mCurrentTargetOffsetBottom < 0
                && !canChildScrollDown()) {
            bottom += mCurrentTargetOffsetBottom;
//            top += mCurrentTargetOffsetBottom;
        } else if (mCurrentTargetOffsetTop > 0 && !canChildScrollUp()) {
            top += mCurrentTargetOffsetTop;
//            bottom += mCurrentTargetOffsetTop;
        }


        //target
        child.layout(childLeft, top,
                childLeft + childWidth, bottom);

        if (Math.abs(mCurrentTargetOffsetBottom) > Math.abs(mCurrentTargetOffsetTop)
                && canChildScrollUp()
                && mFooterView != null && mFooterView.getVisibility() == VISIBLE
        ) {
            child.scrollTo(0, child.getMeasuredHeight());
        }
        //header
        if (mHeaderView != null) {
            int headTop = mCurrentTargetOffsetTop - mHeaderView.getMeasuredHeight();
            if (headTop >= 0 && mHeaderView.needStickTop()) {
                headTop = 0;
            }
            mHeaderView.layout(0, headTop, width, headTop + mHeaderView.getMeasuredHeight());
            //call back
            float percent = 0f;
            if (mCurrentTargetOffsetTop > mHeaderOffsetDistance) {
                percent = 1;
            } else if (mHeaderOffsetDistance > 0) {
                percent = ((float) mCurrentTargetOffsetTop) / mHeaderOffsetDistance;
            }
            mHeaderView.positionChange(percent, mCurrentTargetOffsetTop, headTop, mHeaderOffsetDistance);
        }
        if (mFooterView != null && mTarget != null) {
            int footerBottom = mCurrentTargetOffsetBottom + mTarget.getMeasuredHeight();
            if (mFooterView.needStickTop() && footerBottom + mFooterView.getMeasuredHeight() < mTarget.getMeasuredHeight()) {
                footerBottom = mTarget.getMeasuredHeight() - mFooterView.getMeasuredHeight();
            }
            mFooterView.layout(0, footerBottom, width, footerBottom + mFooterView.getMeasuredHeight());
            //call back
            float percent = 0f;
            if (Math.abs(mCurrentTargetOffsetTop) > mFooterOffsetDistance) {
                percent = 1;
            } else if (mFooterOffsetDistance > 0) {
                percent = Math.abs(mCurrentTargetOffsetTop) / mFooterOffsetDistance;
            }
            mFooterView.positionChange(percent, mCurrentTargetOffsetBottom, footerBottom, mFooterOffsetDistance);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        if (mHeaderView != null) {
            mHeaderView.measure(widthMeasureSpec,
                    LayoutParams.WRAP_CONTENT);
            mHeaderOffsetDistance = (int) (mHeaderView.getMeasuredHeight() + (mHeaderView.provideOffsetDistance() * density));
        }
        if (mFooterView != null) {
            mFooterView.measure(widthMeasureSpec,
                    LayoutParams.WRAP_CONTENT);
            mFooterOffsetDistance = (int) (mFooterView.getMeasuredHeight() + (mFooterView.provideOffsetDistance() * density));
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        final int action = ev.getAction();

        int pointerIndex;
        if (canBeIntercept()) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (canStartNestedScroll()) {
                    reset();
                }
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDraggedDown = false;
                mIsBeingDraggedUp = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                float y = ev.getY(pointerIndex);
                startDragging(y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDraggedDown = false;
                mIsBeingDraggedUp = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDraggedDown || mIsBeingDraggedUp;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        int pointerIndex = -1;
        if (canBeIntercept()) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDraggedDown = false;
                mIsBeingDraggedUp = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                startDragging(y);

                if (mIsBeingDraggedDown) {
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (mTarget.getTop() + overscrollTop > 0) {
                        moveSpinner(overscrollTop);
                        mInitialMotionY = y;
                    } else {
                        return false;
                    }
                }
                if (mIsBeingDraggedUp) {
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (mTarget.getBottom() - mTarget.getMeasuredHeight() + overscrollTop < 0) {
                        // : 2017/6/20  joker moveUp
                        moveSpinner(overscrollTop);
                        mInitialMotionY = y;
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG,
                            "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                finishMove();
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                finishMove();
                mActivePointerId = INVALID_POINTER;
                return false;
            }

        }

        return true;
    }

    private void finishMove() {
        if (mIsBeingDraggedDown) {
            mIsBeingDraggedDown = false;
            finishSpinner(mTarget.getTop());
        }
        if (mIsBeingDraggedUp) {
            mIsBeingDraggedUp = false;
//            finish pull up
            finishLoadSpinner(Math.abs(mTarget.getBottom() - mTarget.getMeasuredHeight()));
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void moveSpinner(float overscrollTop) {
        // where 1.0f is a full circle
        if (mHeaderView != null && mHeaderView.getVisibility() != View.VISIBLE && mIsBeingDraggedDown) {
            mHeaderView.setVisibility(View.VISIBLE);
        }
        if (mFooterView != null && mFooterView.getVisibility() != View.VISIBLE && mIsBeingDraggedUp) {
            mFooterView.setVisibility(View.VISIBLE);
        }

        int offset = 0;
        if (mIsBeingDraggedDown) {
            offset = (int) overscrollTop;
        } else if (mIsBeingDraggedUp) {
            offset = (int) overscrollTop;
        }

        setTargetOffsetTopAndBottom(offset, true /* requires update */);
    }

    @Override
    protected void finishSpinner(float overscrollTop) {
        if (overscrollTop > mHeaderOffsetDistance) {
            setRefreshingEnd(true);
        } else {
            // cancel refresh
            mRefreshing = false;
            animateOffsetToStartPosition(null);
        }
    }

    protected void finishLoadSpinner(float overscrollTop) {
        if (overscrollTop > mFooterOffsetDistance) {
            //  load more
            setLoadMoreEnd(true);
        } else {
            // cancel refresh
            mLoadMore = false;
            // : 2017/6/20  joker revert to start
            animateFooterOffsetToStartPosition(null);
        }
    }

    @Override
    protected boolean canStartNestedScroll() {
        return !mRefreshing && !mLoadMore;
    }

    private boolean canBeIntercept() {
        boolean intercept = (canChildScrollUp() && canChildScrollDown())
                || !isEnabled()
                || mRefreshing || mLoadMore || mNestedScrollInProgress
                || (mHeaderView == null && mFooterView == null);
        if (intercept) {
            if (!isEnabled() || mRefreshing || mLoadMore || mNestedScrollInProgress
                    || (mHeaderView == null && mFooterView == null)) {

            } else {
                finishLoadSpinner(Math.abs(mTarget.getBottom() - mTarget.getMeasuredHeight()));
            }
        }
        return intercept;
    }

    private void startDragging(float y) {
        float downDiff = y - mInitialDownY;
        if (downDiff > mTouchSlop && !mIsBeingDraggedDown && !canChildScrollUp() && !mIsBeingDraggedUp) {
            mInitialMotionY = y + mTouchSlop;
            mIsBeingDraggedDown = mHeaderView != null;
        }
        float upDiff = mInitialDownY - y;
        if (upDiff > mTouchSlop && !mIsBeingDraggedUp && !canChildScrollDown() && !mIsBeingDraggedDown) {
            mInitialMotionY = y + mTouchSlop;
            mIsBeingDraggedUp = mFooterView != null;
        }

    }

    /**
     * header move to correct position
     */
    private void animateOffsetToCorrectPosition(AnimationListener listener) {
        if (mHeaderView != null && mTarget != null) {
            final int moveDistance = mTarget.getTop() - mHeaderOffsetDistance;
            Animation mAnimateToCorrectPosition = new Animation() {
                @Override
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    mCurrentTargetOffsetTop = (int) (moveDistance - (moveDistance * interpolatedTime) + mHeaderOffsetDistance);
                    refreshLayout();
                }
            };
            mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
            mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
            mHeaderView.setAnimationListener(listener);
            mHeaderView.clearAnimation();
            mHeaderView.startAnimation(mAnimateToCorrectPosition);
        }
    }

    private void animateFooterOffsetToCorrectPosition(AnimationListener listener) {
        if (mFooterView != null && mTarget != null) {
            final int moveDistance = mTarget.getBottom() - mTarget.getMeasuredHeight() + mFooterOffsetDistance;
            Animation mAnimateToCorrectPosition = new Animation() {
                @Override
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    mCurrentTargetOffsetBottom = (int) (moveDistance - (moveDistance * interpolatedTime) - mFooterOffsetDistance);
                    refreshLayout();
                }
            };
            mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
            mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
            mFooterView.setAnimationListener(listener);
            mFooterView.clearAnimation();
            mFooterView.startAnimation(mAnimateToCorrectPosition);
        }
    }

    /**
     * move to start position
     */
    private void animateOffsetToStartPosition(AnimationListener listener) {
        if (mHeaderView != null && mTarget != null) {
            final int moveDistance = mTarget.getTop();
            Animation mAnimateToStartPosition = new Animation() {
                @Override
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    mCurrentTargetOffsetTop = (int) (moveDistance - (moveDistance * interpolatedTime));
                    refreshLayout();
                }
            };

            mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
            mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
            mHeaderView.setAnimationListener(listener);
            mHeaderView.clearAnimation();
            mHeaderView.startAnimation(mAnimateToStartPosition);
        }
    }

    private void animateFooterOffsetToStartPosition(AnimationListener listener) {
        if (mFooterView != null && mTarget != null) {
            final int moveDistance = mTarget.getBottom() - mTarget.getMeasuredHeight();
            Animation mAnimateToStartPosition = new Animation() {
                @Override
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    mCurrentTargetOffsetBottom = (int) (moveDistance - (moveDistance * interpolatedTime));
                    refreshLayout();
                }
            };

            mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
            mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
            mFooterView.setAnimationListener(listener);
            mFooterView.clearAnimation();
            mFooterView.startAnimation(mAnimateToStartPosition);
        }
    }


    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mloadMoreListener) {
        this.mloadMoreListener = mloadMoreListener;
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    public boolean isLoadMore() {
        return mLoadMore;
    }

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        void onLoadMore();
    }

}
