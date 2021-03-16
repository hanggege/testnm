package com.mei.widget.webview;

import android.content.Context;
import android.util.AttributeSet;

import com.mei.widget.recyclerview.observable.ObservableInitWebView;

/**
 * Created by 杨强彪 on 2016/6/6.
 *
 * @描述： 监听滚动的webview
 */
public class WebViewObservable extends ObservableInitWebView {
    private OnScrollChangedCallback mOnScrollChangedCallback;

    public WebViewObservable(final Context context) {
        super(context);
    }

    public WebViewObservable(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public WebViewObservable(final Context context, final AttributeSet attrs,
                             final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl,
                                   final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mOnScrollChangedCallback != null) {
            float webcontent = getContentHeight() * getScale();// webview的高度
            float webnow = getHeight() + getScrollY();// 当前webview的高度
            if (Math.abs(webcontent - webnow) < 3) {
                // 已经处于底端
                // Log.i("TAG1", "已经处于底端");
                mOnScrollChangedCallback.onPageEnd(getScrollY(), t, oldl, oldt);
            } else if (getScrollY() == 0) {
                // Log.i("TAG1", "已经处于顶端");
                mOnScrollChangedCallback.onPageTop(l, t, oldl, oldt);
            } else {
                mOnScrollChangedCallback.onScrollChanged(getScrollY());
            }
        }

    }

    public void setOnScrollChangedCallback(OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    public static interface OnScrollChangedCallback {
        void onPageEnd(int l, int t, int oldl, int oldt);

        void onPageTop(int l, int t, int oldl, int oldt);

        void onScrollChanged(int l);
    }
}
