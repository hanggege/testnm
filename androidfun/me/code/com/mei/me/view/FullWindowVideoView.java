package com.mei.me.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * 全屏播放
 */
public class FullWindowVideoView extends VideoView {

    public FullWindowVideoView(Context context) {
        super(context);
    }

    public FullWindowVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullWindowVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
