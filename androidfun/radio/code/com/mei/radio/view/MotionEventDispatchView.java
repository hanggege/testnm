package com.mei.radio.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by lenna on 2020/9/22
 * 电台滚动条和转盘联动母控件（主要做事件分发）
 */
public class MotionEventDispatchView extends View {
    private Map<View, Boolean> viewMap;

    private Rect rect;

    private boolean isIntercept = false;

    public MotionEventDispatchView(Context context) {
        this(context, null);
    }

    public MotionEventDispatchView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public MotionEventDispatchView(Context context, AttributeSet attr, int def) {
        super(context, attr, def);
        viewMap = new HashMap<>();
        rect = new Rect();
    }

    private void onViewTouchEvent(MotionEvent paramMotionEvent) {
        for (View view : viewMap.keySet()) {
            view.getHitRect(rect);
            if (rect.contains(Math.round(paramMotionEvent.getX()), Math.round(paramMotionEvent.getY()))) {
                viewMap.put(view, true);
                MotionEvent motionEvent = MotionEvent.obtain(paramMotionEvent);
                motionEvent.setLocation(paramMotionEvent.getX() - rect.left, paramMotionEvent.getY() - rect.top);
                view.onTouchEvent(motionEvent);
                continue;
            }
            viewMap.put(view, false);
            view.onTouchEvent(paramMotionEvent);
        }
    }

    private void shouldEvent(MotionEvent event) {
        for (View view : viewMap.keySet()) {
            view.getHitRect(rect);
            if (viewMap.get(view)) {
                MotionEvent motionEvent = MotionEvent.obtain(event);
                motionEvent.setLocation(event.getX() - rect.left, event.getY() - rect.top);
                view.onTouchEvent(motionEvent);
                continue;
            }
            view.onTouchEvent(event);
        }
    }

    /***
     *  是否在矩形区域
     */
    private boolean isRectArea(MotionEvent paramMotionEvent) {
        for (View view : viewMap.keySet()) {
            view.getHitRect(rect);
            if (rect.contains(Math.round(paramMotionEvent.getX()), Math.round(paramMotionEvent.getY())))
                return true;
        }
        return false;
    }

    public void addView(View paramView) {
        viewMap.put(paramView, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isIntercept) {
            return true;
        }
        if (e.getAction() != MotionEvent.ACTION_DOWN) {
            shouldEvent(e);
            return true;
        }
        if (isRectArea(e)) {
            onViewTouchEvent(e);
            return true;
        }
        return false;
    }

    public boolean isIntercept() {
        return isIntercept;
    }

    public void setIntercept(boolean intercept) {
        isIntercept = intercept;
    }
}

