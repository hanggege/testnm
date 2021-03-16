package com.mei.widget.recyclerview.banner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MarginLayoutParamsCompat;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/9
 */
public class PageIndicatorView extends LinearLayout {

    public PageIndicatorView(Context context) {
        this(context, null, 0);
    }

    public PageIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
    }

    private GradientDrawable provideGradientDrawable(@ColorInt int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        drawable.setSize(dip2px(6), dip2px(6));
        return drawable;
    }

    private StateListDrawable provideStateListDrawable() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        Drawable selected = provideGradientDrawable(Color.WHITE);
        Drawable unSelected = provideGradientDrawable(Color.parseColor("#cecece"));
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, selected);
        stateListDrawable.addState(new int[]{}, unSelected);
        return stateListDrawable;
    }

    public void buildIndicator(int number) {
        buildIndicator(number, NO_ID);
    }

    public void buildIndicator(int number, @DrawableRes int resId) {
        buildIndicator(number, resId, dip2px(5));
    }

    public void buildIndicator(int number, @DrawableRes int resId, int space) {
        removeAllViews();
        for (int i = 0; i < number && number > 1; i++) {
            Drawable drawable = resId == NO_ID ? provideStateListDrawable() : ContextCompat.getDrawable(getContext(), resId);
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageDrawable(drawable);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i > 0)
                MarginLayoutParamsCompat.setMarginStart(params, space);
            addView(imageView, params);
        }
        selectIndex(0);
    }


    public void selectIndex(int index) {
        if (getChildCount() > 0) {
            index = index % getChildCount();
            for (int i = 0; i < getChildCount(); i++) {
                ImageView view = (ImageView) getChildAt(i);
                view.setSelected(index == i);
                view.requestLayout();
            }
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
