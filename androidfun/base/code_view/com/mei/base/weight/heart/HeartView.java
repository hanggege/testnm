package com.mei.base.weight.heart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.ColorRes;

/**
 * 桃心控件
 *
 * @author caowei
 * @email 646030315@qq.com
 * @time Created on 2016/12/5 0005
 */

@SuppressLint("ViewConstructor")
public class HeartView extends View {

    private Paint mPaint;
    private int mColor;

    /**
     * 桃心的路径
     */
    private static Path sHeartPath;

    public HeartView(Context context, @ColorRes int color) {
        super(context);
        mColor = color;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawLove(canvas);
    }

    private void drawLove(Canvas canvas) {

        if (sHeartPath == null) {
            sHeartPath = new Path();
            sHeartPath.addArc(new RectF(0, 0, getWidth() >> 1, getWidth() >> 1), -225, 225);
            sHeartPath.arcTo(new RectF(getWidth() >> 1, 0, getWidth(), getWidth() >> 1), -180, 225);
            sHeartPath.lineTo(getWidth() >> 1, getHeight());
        }
        canvas.drawPath(sHeartPath, mPaint);
    }

    public static void destroy() {
        sHeartPath = null;
    }

}
