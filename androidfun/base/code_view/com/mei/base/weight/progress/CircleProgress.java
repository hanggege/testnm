package com.mei.base.weight.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.mei.orc.ext.DimensionsKt;
import com.mei.wood.R;

/**
 * @author caowei
 * @email 646030315@qq.com
 * @time Created on 2016/10/11 0011
 */

public class CircleProgress extends View {

    private Paint mPaint;
    private RectF mRectF;

    private int mProgressColor = -1;
    private int mProgressWidth;
    private int mCurSwipeAngle;

    public CircleProgress(Context context) {
        super(context);
        mProgressWidth = DimensionsKt.dip(context, 1);
        init();
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init();
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        mProgressWidth = ta.getDimensionPixelSize(R.styleable.CircleProgress_progressWidth, DimensionsKt.dip(context, 1));
        mProgressColor =ta.getColor(R.styleable.CircleProgress_progressColor,getResources().getColor(R.color.main_theme_color));
        ta.recycle();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRectF == null) {
            int size = Math.min(getWidth(), getHeight());
            mRectF = new RectF((getWidth() - size + mProgressWidth) >> 1, (getHeight() - size + mProgressWidth) >> 1, (getWidth() + size - mProgressWidth) >> 1, (getHeight() + size - mProgressWidth) / 2);
        }
        mPaint.setColor(mProgressColor);
        canvas.drawArc(mRectF, -90, mCurSwipeAngle, false, mPaint);
    }

    public void setProgress(int progress) {
        mCurSwipeAngle = progress * 360 / 100;
        invalidate();
    }
}
