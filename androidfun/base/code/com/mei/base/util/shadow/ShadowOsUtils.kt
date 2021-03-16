package com.mei.base.util.shadow

import android.os.Build
import android.view.View
import com.mei.orc.ext.dip

/**
 * @description:
 * @author: lenna
 * @date: 2019-09-05
 * @update: 2019-09-05
 * @version: 1.0
 */

/**
 * 获取view 需要设置的阴影透明度
 */
fun View.getShadowAlpha(): Float {
    val sdk = Build.VERSION.SDK_INT
    var alpha = 0.7f
    if (sdk < Build.VERSION_CODES.P) {
        alpha = 0.2f
    }
    return alpha
}

fun View.getShadowTranslationZ(): Float {
    val sdk = Build.VERSION.SDK_INT
    var transLationZ = dip(SIZE_SHADOW_OS_P_TRANSLATIONZ).toFloat()
    if (sdk < Build.VERSION_CODES.P) {
        transLationZ = dip(SIZE_SHADOW_DEFAULT_TRANSLATIONZ).toFloat()
    }
    return transLationZ
}