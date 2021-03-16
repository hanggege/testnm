package com.mei.base.weight.recyclerview.listener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mei.base.weight.recyclerview.manager.WrapLayoutManager;
import com.mei.orc.imageload.glide.GlideImageLoaderKt;

/**
 * Created by joker on 16/6/29.
 */
@Deprecated
public abstract class RecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = RecyclerOnScrollListener.class.getSimpleName();

    private boolean isBootom = false;
    private boolean showBottom = false;
    private boolean noMoreData = false;
    private boolean loading = false; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    protected int firstVisibleItem, lastVisibleItemPosition, visibleItemCount, totalItemCount;

    private WrapLayoutManager mWrapLayoutManager;

    public RecyclerOnScrollListener(WrapLayoutManager linearLayoutManager) {
        this.mWrapLayoutManager = linearLayoutManager;
    }

    public RecyclerOnScrollListener(WrapLayoutManager linearLayoutManager, int visibleThreshold) {
        this.mWrapLayoutManager = linearLayoutManager;
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mWrapLayoutManager.getItemCount();
        firstVisibleItem = mWrapLayoutManager.findFirstVisibleItemPosition();
        lastVisibleItemPosition = mWrapLayoutManager.findLastVisibleItemPosition();
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            GlideImageLoaderKt.resumeRequests(recyclerView.getContext());
            if (!loading && !noMoreData && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                onLoadMore();
            }

            isBootom = visibleItemCount > 0 && lastVisibleItemPosition >= totalItemCount - 1;
            if (isBootom && noMoreData && !showBottom) {
                onBottom();
                showBottom = true;
            }
        } else {
            GlideImageLoaderKt.pauseRequests(recyclerView.getContext());
        }

    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isNoMoreData() {
        return noMoreData;
    }

    public boolean canLoadMore() {
        return !loading && !noMoreData;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void setNoMoreData(boolean noMoreData) {
        this.noMoreData = noMoreData;
        if (noMoreData && isBootom && !showBottom) {
            onBottom();
            this.showBottom = true;
        } else {
            this.showBottom = false;
        }
    }

    public abstract void onLoadMore();

    public void onBottom() {
    }

}