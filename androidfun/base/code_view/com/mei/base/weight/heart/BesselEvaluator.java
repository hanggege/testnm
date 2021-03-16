package com.mei.base.weight.heart;

import android.animation.TypeEvaluator;

/**
 * 实现贝塞尔曲线轨迹动画插值器
 *
 * @author caowei
 * @email 646030315@qq.com
 * @time Created on 2016/12/6 0006
 */

public class BesselEvaluator implements TypeEvaluator<float[]> {

    private float[] point1 = new float[2];
    private float[] point2 = new float[2];

    public BesselEvaluator(float[] point1, float[] point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    @Override
    public float[] evaluate(float fraction, float[] point0, float[] point3) {
        float[] currentPosition = new float[3];
        currentPosition[0] = point0[0] * (1 - fraction) * (1 - fraction) * (1 - fraction)
                + point1[0] * 3 * fraction * (1 - fraction) * (1 - fraction)
                + point2[0] * 3 * (1 - fraction) * fraction * fraction
                + point3[0] * fraction * fraction * fraction;
        currentPosition[1] = point0[1] * (1 - fraction) * (1 - fraction) * (1 - fraction)
                + point1[1] * 3 * fraction * (1 - fraction) * (1 - fraction)
                + point2[1] * 3 * (1 - fraction) * fraction * fraction
                + point3[1] * fraction * fraction * fraction;
        currentPosition[2] = fraction;
        return currentPosition;
    }
}
