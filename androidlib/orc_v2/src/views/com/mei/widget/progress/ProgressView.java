package com.mei.widget.progress;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.mei.orc.R;


public class ProgressView extends View {

    private int mProgressId;

    public static final int MODE_DETERMINATE = 0;
    public static final int MODE_INDETERMINATE = 1;

    private Drawable mProgressDrawable;

    public ProgressView(Context context) {
        this(context, null, 0, 0);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        applyStyle(context, attrs, defStyleAttr, defStyleRes);
    }

    private boolean needCreateProgress(boolean circular) {
        if (mProgressDrawable == null)
            return true;

        return !(mProgressDrawable instanceof CircularProgressDrawable);
    }

    protected void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        int progressId = a.getResourceId(R.styleable.ProgressView_pv_progressStyle, 0);
        ;

        if (needCreateProgress(true)) {
            mProgressId = progressId;
            mProgressDrawable = new CircularProgressDrawable.Builder(context, mProgressId, isInEditMode()).build();
            setBackground(mProgressDrawable);
        } else if (mProgressId != progressId) {
            mProgressId = progressId;
            ((CircularProgressDrawable) mProgressDrawable).applyStyle(context, mProgressId);
        }
        a.recycle();
    }

    /**
     * Start showing progress.
     */
    public void start() {
        if (mProgressDrawable != null)
            ((Animatable) mProgressDrawable).start();
    }

    /**
     * Stop showing progress.
     */
    public void stop() {
        if (mProgressDrawable != null)
            ((Animatable) mProgressDrawable).stop();
    }
}
