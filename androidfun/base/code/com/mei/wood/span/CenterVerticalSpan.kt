package com.mei.wood.span

import android.text.TextPaint
import android.text.style.MetricAffectingSpan


/**
 *
 * @author Created by lenna on 2020/8/14
 * 富文本垂直居中自定义
 */
class CenterVerticalSpan : MetricAffectingSpan() {
    override fun updateMeasureState(textPaint: TextPaint) {
        textPaint.baselineShift += getBaselineShift(textPaint)
    }

    override fun updateDrawState(tp: TextPaint?) {
        tp?.baselineShift = tp?.baselineShift?.plus(getBaselineShift(tp))
    }

    private fun getBaselineShift(tp: TextPaint): Int {
        val total = tp.ascent() + tp.descent()
        return (total / 3f).toInt()
    }

}