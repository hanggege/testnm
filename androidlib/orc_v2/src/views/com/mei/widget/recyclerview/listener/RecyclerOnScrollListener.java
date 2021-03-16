package com.mei.widget.recyclerview.listener;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mei.orc.imageload.glide.GlideImageLoaderKt;

/**
 * Created by joker on 16/6/29.
 */
public abstract class RecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = RecyclerOnScrollListener.class.getSimpleName();

    private boolean isBootom = false;
    private boolean showBottom = false;
    private boolean noMoreData = false;
    private boolean loading = false; // True if we are still waiting for the last set of data to load.
    protected int visibleThreshold = 4; // The minimum amount of items to have below your current scroll position before loading more.
    protected int firstVisibleItem, lastVisibleItemPosition, visibleItemCount, totalItemCount;

    private LinearLayoutManager mLinearLayoutManager;

    public RecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
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


    public void setLoading(boolean loading) {
        this.loading = loading;
    }


    public abstract void onLoadMore();

    public void onBottom() {
    }

}