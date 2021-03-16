package com.mei.base.weight.xiaolurefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.mei.widget.refresh.HeaderParentLayout;
import com.mei.wood.R;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/6/15.
 */

public class CircleHeader extends HeaderParentLayout {
    private long mRefreshStartTime;

    private Animation circle_anim;

    ImageView xiaoluRefreshIcon;
    TextView xiaoluRefreshTv;
    private boolean isPull = true;
    @DrawableRes
    int resImg;

    CircleHeader(Context context) {
        super(context);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.circle_header_layout, null);
        xiaoluRefreshTv = view.findViewById(R.id.xiaolu_refresh_tv);
        xiaoluRefreshIcon = view.findViewById(R.id.xiaolu_refresh_icon);
        addView(view);
//        setRefreshTv("下拉刷新");
    }


    public void setRefreshTv(CharSequence sequence) {
        if (!TextUtils.equals(xiaoluRefreshTv.getText(), sequence))
            xiaoluRefreshTv.setText(sequence);
    }

    public void setRefreshImg(@DrawableRes int resId) {
        xiaoluRefreshIcon.setImageResource(resId);
        resImg = resId;
    }

    public Animation setRefreshAnimImg() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.small_circle_loading_anm);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        animation.setInterpolator(interpolator);
        return animation;
    }

    public long getrefreshStartTime() {
        return mRefreshStartTime;
    }


    @Override
    public int provideOffsetDistance() {
        return 0;
    }

    @Override
    public boolean needStickTop() {
        return true;
    }

    @Override
    public void positionChange(float percent, int currDragPosition, int marginTopDistance, int allDistance) {
//        Log.d("Ling", "percent = " + percent + " , currDragPosition = " + currDragPosition + " , marginTopDistance = " + marginTopDistance + " , allDistance = " + allDistance);
        xiaoluRefreshIcon.setRotation(percent * 360);
        if (percent == 1f) {
//            setRefreshTv("放开刷新");
            isPull = false;
        } else if (!isPull) {
//            setRefreshTv("下拉刷新");
            isPull = true;
        }
    }

    @Override
    public void setRefresh(boolean refreshing) {
        super.setRefresh(refreshing);
        if (refreshing) {
            mRefreshStartTime = System.currentTimeMillis();
            startImageAnim();
        } else if (circle_anim != null) {
            circle_anim.cancel();
        }
    }

    private void startImageAnim() {
        circle_anim = setRefreshAnimImg();
        if (circle_anim != null) {
            xiaoluRefreshIcon.startAnimation(circle_anim);
        }
    }

}
