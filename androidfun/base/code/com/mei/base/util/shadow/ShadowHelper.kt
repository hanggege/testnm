package com.mei.base.util.shadow

import android.annotation.TargetApi
import android.graphics.Color
import android.graphics.Outline
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import androidx.core.content.ContextCompat
import com.mei.orc.ext.dip
import com.mei.wood.R


/**
 * @description:
 * @author: lenna
 * @date: 2019-07-10 14:22
 * @version: 1.0
 */
private var SIZE_SHADOW_DEFAULT_CORNER: Float = 8f
var SIZE_SHADOW_STRA_CORNER = 17f

var SIZE_SHADOW_DEFAULT_ELEVATION = 2f

var SIZE_SHADOW_DEFAULT_TRANSLATIONZ = 6f
var SIZE_SHADOW_OS_P_TRANSLATIONZ = 16f

var SHADOW_COLOR: Int = Color.parseColor("#0DFFFFFF")
var SHADOW_DEFAULT_BACKGROUND_COLOR: Int = Color.parseColor("#ffffff")
fun View.setListShadowDefault(v: View) {
    setListShadow(v)
}

fun View.setViewShadowWithCorner(v: View, corner: Float) {
    setListShadow(v, corner = corner)
}

fun View.setListShadow(v: View, corner: Float = dip(SIZE_SHADOW_DEFAULT_CORNER).toFloat(),
                       alpha: Float = getShadowAlpha(),
                       elevation: Float = dip(SIZE_SHADOW_DEFAULT_ELEVATION).toFloat(),
                       translationZ: Float = getShadowTranslationZ(),
                       shadowColor: Int = SHADOW_COLOR) {


    val sdk = Build.VERSION.SDK_INT
    if (sdk >= Build.VERSION_CODES.LOLLIPOP) {
        setListShadowImpl21(v, corner, alpha, elevation, translationZ)
        if (sdk >= Build.VERSION_CODES.P) {
            setListShadowImpl28(v, corner, alpha, elevation, translationZ, shadowColor)
        }
    }
}

fun View.setListNoRoundShadowDefault() {
    setListNoRoundShadow(this)
}


fun View.setListNoRoundShadow(v: View,
                              alpha: Float = getShadowAlpha(),
                              elevation: Float = dip(SIZE_SHADOW_DEFAULT_ELEVATION).toFloat(),
                              translationZ: Float = getShadowTranslationZ(),
                              shadowColor: Int = SHADOW_COLOR) {

    val sdk = Build.VERSION.SDK_INT
    if (sdk >= Build.VERSION_CODES.LOLLIPOP) {
        setListNoRoundShadowImpl21(v, alpha, elevation, translationZ)
        if (sdk >= Build.VERSION_CODES.P) {
            setListNoRoundShadowImpl28(v, alpha, elevation, translationZ, shadowColor)
        }
    }
}


/**
 * 设置view圆形阴影
 */
fun View.setViewCircularShadow(v: View,
                               alpha: Float = getShadowAlpha(),
                               elevation: Float = dip(SIZE_SHADOW_DEFAULT_ELEVATION).toFloat(),
                               translationZ: Float = getShadowTranslationZ(),
                               shadowColor: Int = SHADOW_COLOR) {

    val sdk = Build.VERSION.SDK_INT
    if (sdk >= Build.VERSION_CODES.LOLLIPOP) {
        setViewCircularShadowImpl21(v, alpha, elevation, translationZ)
        if (sdk >= Build.VERSION_CODES.P) {
            setViewCircularShadowImpl28(v, alpha, elevation, translationZ, shadowColor)
        }
    }
}

@TargetApi(Build.VERSION_CODES.P)
private fun setListShadowImpl28(v: View, corner: Float, alpha: Float, elevation: Float, translationZ: Float, shadowColor: Int) {
    setListShadowImpl21(v, corner, alpha, elevation, translationZ)
    v.outlineSpotShadowColor = shadowColor
}

@TargetApi(Build.VERSION_CODES.P)
private fun setViewCircularShadowImpl28(v: View, alpha: Float, elevation: Float, translationZ: Float, shadowColor: Int) {
    setViewCircularShadowImpl21(v, alpha, elevation, translationZ)
    v.outlineSpotShadowColor = shadowColor
}

@TargetApi(Build.VERSION_CODES.P)
private fun setListNoRoundShadowImpl28(v: View, alpha: Float, elevation: Float, translationZ: Float, shadowColor: Int) {
    setListNoRoundShadowImpl21(v, alpha, elevation, translationZ)
    v.outlineSpotShadowColor = shadowColor
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private fun setListShadowImpl21(v: View, corner: Float, alpha: Float, elevation: Float, translationZ: Float) {
    val outlineProvider = @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            //                    outline.setRoundRect(0, 0, DeviceUtils.getScreenSize(getContext())[0] - ScreenUtils.dipToPx(getContext(), 15 * 2), ScreenUtils.dipToPx(getContext(), 143), ScreenUtils.dipToPx(getContext(), 8));
            outline.setRoundRect(0, 0, v.width, v.height, corner)
            outline.alpha = alpha
        }
    }
    v.setBackgroundResource(R.drawable.bg_card_white_radius)
    v.elevation = elevation
    v.translationZ = translationZ
    v.clipToOutline = true
    v.outlineProvider = outlineProvider
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private fun setViewCircularShadowImpl21(v: View, alpha: Float, elevation: Float, translationZ: Float) {
    val outlineProvider = @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setOval(0, 0, v.width, v.height)
            outline.alpha = alpha
        }
    }
    v.setBackgroundResource(R.drawable.bg_card_white_circular)
    v.elevation = elevation
    v.translationZ = translationZ
    v.clipToOutline = true
    v.outlineProvider = outlineProvider
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private fun setListNoRoundShadowImpl21(v: View, alpha: Float, elevation: Float, translationZ: Float) {
    val outlineProvider = @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            //                    outline.setRoundRect(0, 0, DeviceUtils.getScreenSize(getContext())[0] - ScreenUtils.dipToPx(getContext(), 15 * 2), ScreenUtils.dipToPx(getContext(), 143), ScreenUtils.dipToPx(getContext(), 8));
            outline.setRect(0, 0, v.width, v.height)
            outline.alpha = alpha
        }
    }
    v.setBackgroundColor(ContextCompat.getColor(v.context, android.R.color.white))
    v.elevation = elevation
    v.translationZ = translationZ
    v.clipToOutline = true
    v.outlineProvider = outlineProvider
}


/**
 * 更加通用的设置阴影方法
 */
fun View.setViewShadow(v: View, corner: Float = dip(SIZE_SHADOW_DEFAULT_CORNER).toFloat(),
                       alpha: Float = getShadowAlpha(),
                       elevation: Float = dip(SIZE_SHADOW_DEFAULT_ELEVATION).toFloat(),
                       translationZ: Float = getShadowTranslationZ(),
                       shadowColor: Int = SHADOW_COLOR,
                       backgroundColor: Int = SHADOW_DEFAULT_BACKGROUND_COLOR) {
    val sdk = Build.VERSION.SDK_INT
    if (sdk >= Build.VERSION_CODES.LOLLIPOP) {
        setViewShadowImpl21(v, corner, alpha, elevation, translationZ, backgroundColor)
        if (sdk >= Build.VERSION_CODES.P) {
            setViewShadowImpl28(v, corner, alpha, elevation, translationZ, shadowColor, backgroundColor)
        }
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private fun setViewShadowImpl21(v: View, corner: Float, alpha: Float, elevation: Float, translationZ: Float, backgroundColor: Int) {
    val outlineProvider = @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            //                    outline.setRoundRect(0, 0, DeviceUtils.getScreenSize(getContext())[0] - ScreenUtils.dipToPx(getContext(), 15 * 2), ScreenUtils.dipToPx(getContext(), 143), ScreenUtils.dipToPx(getContext(), 8));
            outline.setRoundRect(0, 0, v.width, v.height, corner)
            outline.alpha = alpha
        }
    }
//        v.setBackgroundResource(R.drawable.bg_card_white_radius)
    v.setBackgroundColor(backgroundColor)
    v.elevation = elevation
    v.translationZ = translationZ
    v.clipToOutline = true
    v.outlineProvider = outlineProvider
}

@TargetApi(Build.VERSION_CODES.P)
private fun setViewShadowImpl28(v: View, corner: Float, alpha: Float, elevation: Float, translationZ: Float, shadowColor: Int, backgroundColor: Int) {
    setViewShadowImpl21(v, corner, alpha, elevation, translationZ, backgroundColor)
    v.outlineSpotShadowColor = shadowColor
}
