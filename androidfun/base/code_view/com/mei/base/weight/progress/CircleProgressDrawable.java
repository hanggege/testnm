package com.mei.base.weight.progress;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;

import com.mei.orc.imageload.glide.GlideImageLoaderKt;
import com.mei.wood.R;


/**
 * @author caowei.
 * Created on 2017/9/19.
 * @email 646030315@qq.com
 */

public class CircleProgressDrawable extends RelativeLayout {

    private CircleProgress mProgress;
    private ImageView mImage;

    public CircleProgressDrawable(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.widget_circle_progress_drawable, this, true);
        initView();
    }

    public CircleProgressDrawable(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widget_circle_progress_drawable, this, true);
        initView();
    }

    public CircleProgressDrawable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.widget_circle_progress_drawable, this, true);
        initView();
    }

    private void initView() {
        mProgress = (CircleProgress) findViewById(R.id.progress);
        mImage = (ImageView) findViewById(R.id.image);
    }

    public void setProgress(int progress) {
        mProgress.setProgress(progress);
    }

    public void setImage(@DrawableRes int resId) {
        mImage.setImageResource(resId);
    }

    public void setImage(String url) {
        GlideImageLoaderKt.glideDisplay(mImage, url);
    }
}
