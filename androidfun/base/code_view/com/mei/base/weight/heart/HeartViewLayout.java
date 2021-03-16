package com.mei.base.weight.heart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mei.wood.R;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实现点赞动画布局
 *
 * @author caowei
 * @email 646030315@qq.com
 * @time Created on 2016/12/6 0006
 */
public class HeartViewLayout extends RelativeLayout {

    private AtomicInteger mHeartCount = new AtomicInteger();
    private HeartViewManager mManager;
    private int[] resId = new int[]{R.drawable.praise_heart_one, R.drawable.praise_heart_two,
            R.drawable.praise_heart_three, R.drawable.praise_heart_four, R.drawable.praise_heart_five};

    private static final int SCALE_DURATION = 300;
    private static final int BESSEL_DURATION = 3000;
    private static final int HEART_MAX_COUNT = Integer.MAX_VALUE;

    private final Object mLock = new Object();
    private Random mRandom = new Random();

    public HeartViewLayout(Context context) {
        super(context);
        init();
    }

    public HeartViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeartViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mManager = new HeartViewManager(getContext());
    }

    /**
     * 生成对应的点赞view并开始动画
     *
     * @param isRandom
     */
    public void start(boolean isRandom) {
        synchronized (mLock) {
            if (mHeartCount.get() < HEART_MAX_COUNT) {
                mHeartCount.incrementAndGet();
                View praiseView = mManager.getPraiseView(isRandom);
                addView(praiseView);
                startAnimation(praiseView, getWidth(), getHeight());
            }
        }
    }

    /**
     * 获取点赞图片
     */
    public View getPraiseView() {
        ImageView praise = new ImageView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        praise.setScaleType(ImageView.ScaleType.FIT_XY);
        praise.setImageResource(resId[mRandom.nextInt(5)]);
        praise.setLayoutParams(params);
        return praise;
    }

    /**
     * 开始实际的贝塞尔曲线
     */
    public void startAnimation(final View target, int rankWidth, int rankHeight) {
        LayoutParams params = (LayoutParams) target.getLayoutParams();
        int targetSize = params.width;
        float[] point0 = new float[2];
        point0[0] = (float) (rankWidth / 3 + (rankWidth * 2 / 3 - targetSize) * Math.random());
        point0[1] = rankHeight - targetSize;

        float[] point1 = new float[2];
        point1[0] = (float) ((rankWidth - targetSize) * (0.10)) + (float) (Math.random() * (rankWidth - targetSize) * (0.8));
        point1[1] = (float) (rankHeight - Math.random() * rankHeight * (0.5));

        float[] point2 = new float[2];
        point2[0] = (float) (Math.random() * (rankWidth - targetSize));
        point2[1] = (float) (Math.random() * (rankHeight - point1[1]));

        float[] point3 = new float[2];
        point3[0] = (float) (Math.random() * (rankWidth - targetSize));
        point3[1] = 0;

        BesselEvaluator evaluator = new BesselEvaluator(point1, point2);
        AnimatorSet set = new AnimatorSet();
        ValueAnimator besselAnimator = ValueAnimator.ofObject(evaluator, point0, point3).setDuration(BESSEL_DURATION);
        besselAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float[] position = (float[]) animation.getAnimatedValue();
                target.setTranslationX(position[0]);
                target.setTranslationY(position[1]);
            }
        });

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(target, "alpha", 1, 0).setDuration(BESSEL_DURATION);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(target, "scaleX", 0, 1).setDuration(SCALE_DURATION);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(target, "scaleY", 0, 1).setDuration(SCALE_DURATION);
        target.setRotation(getRandomRotation());
        set.playTogether(besselAnimator, alphaAnimator, scaleXAnimator, scaleYAnimator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeView(target);
                mHeartCount.decrementAndGet();
            }
        });
        set.start();
    }

    /**
     * 生成点赞图片的倾斜角度
     */
    private float getRandomRotation() {
        return 330 + mRandom.nextInt(61);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 控件销毁的时候，初始化静态数据,HeartView资源释放
        mManager.destroy();
    }
}
