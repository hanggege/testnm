package com.mei.widget.refresh;

import android.content.Context;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/6/20.
 */

public abstract class FooterParentLayout extends FrameLayout implements FooterRefreshProvider {

    private Animation.AnimationListener mListener;
    private boolean isLoadMore;

    public FooterParentLayout(@NonNull Context context) {
        super(context);
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mListener != null) {
            mListener.onAnimationStart(getAnimation());
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mListener != null) {
            mListener.onAnimationEnd(getAnimation());
        }
    }

    @Override
    public void setLoadMore(boolean loadMore) {
        isLoadMore = loadMore;
    }


    public boolean isLoadMoreing() {
        return isLoadMore;
    }
}
