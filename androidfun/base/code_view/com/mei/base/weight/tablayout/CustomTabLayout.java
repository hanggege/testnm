package com.mei.base.weight.tablayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;

/**
 * @author Created by Ling on 2019/1/3
 * 增加了设置下划线多个颜色的功能
 */
public class CustomTabLayout extends SlidingTabLayout {
    private int[] colors;

    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIndicatorStyle == STYLE_NORMAL && mIndicatorHeight > 0) {
            mIndicatorDrawable.setColors(colors);
            mIndicatorDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            mIndicatorDrawable.draw(canvas);
        }
    }

    public void setIndicatorColors(@ColorInt int[] indicatorColor) {
        this.colors = indicatorColor;
        invalidate();
    }
}
