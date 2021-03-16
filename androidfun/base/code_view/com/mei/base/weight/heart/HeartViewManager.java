package com.mei.base.weight.heart;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;

import com.mei.wood.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 点赞动画管理类，控制生成桃心还是点赞图片
 *
 * @author caowei
 * @email 646030315@qq.com
 * Created on 17/7/21.
 */
public class HeartViewManager {

    private static int sDrawCount = 0;
    private int sLastDrawableId = -1;

    private static final int COLOR_PINK = Color.parseColor("#fc85ad");
    private static final int COLOR_YELLOW = Color.parseColor("#ffdd33");
    private static final int COLOR_BLUE = Color.parseColor("#64c3fa");

    private ColorWrapper mPink;
    private Random sRandom;

    private List<Integer> sCustomPraiseList = new ArrayList<>();
    private List<ColorWrapper> sColorList = new ArrayList<>();
    private ColorWrapper sColorCircle;

    private static int sColor = -1;

    private Context mContext;

    HeartViewManager(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        sRandom = new Random();
        // 单循环链表控制星星颜色交替改变
        ColorWrapper mBlue = new ColorWrapper(COLOR_BLUE);
        ColorWrapper mYellow = new ColorWrapper(COLOR_YELLOW, mBlue);
        mPink = new ColorWrapper(COLOR_PINK, mYellow);
        sColorList.add(mPink);
        sColorList.add(mYellow);
        sColorList.add(mBlue);

        sCustomPraiseList.add(R.drawable.blue_hi);
        sCustomPraiseList.add(R.drawable.blue_praise);
        sCustomPraiseList.add(R.drawable.red_hi);
        sCustomPraiseList.add(R.drawable.red_praise);
        sCustomPraiseList.add(R.drawable.yellow_hi);
        sCustomPraiseList.add(R.drawable.yellow_praise);

        sColorCircle = sColorList.get(sRandom.nextInt(sColorList.size()));
    }

    /**
     * 获取点赞View，桃心还是图片
     *
     * @param isRandom 是否需要随机，固定颜色桃心和图片
     */
    View getPraiseView(boolean isRandom) {
        sDrawCount++;
        if (sDrawCount % 10 != 0) {
            int color;
            if (isRandom) {
                color = sColorCircle.color;
                sColorCircle = sColorCircle.next == null ? mPink : sColorCircle.next;
            } else {
                if (sColor == -1) {
                    randomSelfColor();
                }
                color = sColor;
            }
            HeartView heartView = new HeartView(mContext, color);
            int width = getDip(23);
            int height = width * 22 / 25;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
            heartView.setLayoutParams(params);
            return heartView;
        } else {
            ImageView praise = new ImageView(mContext);
            int width = (isPraise() ? getDip(29) : getDip(24));
            int height = (isPraise() ? getDip(28) : getDip(24));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
            praise.setScaleType(ImageView.ScaleType.FIT_XY);
            praise.setImageResource(getRandomDrawable(isRandom));
            praise.setLayoutParams(params);
            return praise;
        }
    }

    /**
     * 随机选定一个固定颜色，自己点赞的时候 全部使用这个颜色
     */
    public static void randomSelfColor() {
        List<Integer> colorList = new ArrayList<>();
        colorList.add(COLOR_PINK);
        colorList.add(COLOR_YELLOW);
        colorList.add(COLOR_BLUE);
        sColor = colorList.get(new Random().nextInt(colorList.size()));
    }

    private int getDip(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, mContext.getResources().getDisplayMetrics());
    }

    /**
     * 上一张图片是不是点赞
     *
     * @return
     */
    private boolean isPraise() {
        return sLastDrawableId == R.drawable.blue_praise || sLastDrawableId == R.drawable.red_praise || sLastDrawableId == R.drawable.yellow_praise;
    }

    /**
     * 获取点赞图片
     *
     * @param isDrawRandom
     * @return
     */
    private
    @DrawableRes
    int getRandomDrawable(boolean isDrawRandom) {
        if (isDrawRandom) {
            if (sColor == -1) {
                randomSelfColor();
            }
            if (sColor == COLOR_PINK) {
                if (sLastDrawableId == -1) {
                    return sLastDrawableId = (sRandom.nextInt(2) == 1 ? R.drawable.red_hi : R.drawable.red_praise);
                } else {
                    return sLastDrawableId = (sLastDrawableId == R.drawable.red_hi ? R.drawable.red_praise : R.drawable.red_hi);
                }
            } else if (sColor == COLOR_YELLOW) {
                if (sLastDrawableId == -1) {
                    return sLastDrawableId = (sRandom.nextInt(2) == 1 ? R.drawable.yellow_hi : R.drawable.yellow_praise);
                } else {
                    return sLastDrawableId = (sLastDrawableId == R.drawable.yellow_hi ? R.drawable.yellow_praise : R.drawable.yellow_hi);
                }
            } else if (sColor == COLOR_BLUE) {
                if (sLastDrawableId == -1) {
                    return sLastDrawableId = (sRandom.nextInt(2) == 1 ? R.drawable.blue_hi : R.drawable.blue_praise);
                } else {
                    return sLastDrawableId = (sLastDrawableId == R.drawable.blue_hi ? R.drawable.blue_praise : R.drawable.blue_hi);
                }
            }
        }
        return sCustomPraiseList.get(sRandom.nextInt(sCustomPraiseList.size()));
    }

    /**
     * 颜色包装类，颜色循环链表节点
     */
    private static final class ColorWrapper {
        int color;
        ColorWrapper next;

        ColorWrapper(int color, ColorWrapper next) {
            this.color = color;
            this.next = next;
        }

        ColorWrapper(int color) {
            this.color = color;
        }
    }

    void destroy() {
        sDrawCount = 0;
        sColor = -1;
        HeartView.destroy();
    }
}
