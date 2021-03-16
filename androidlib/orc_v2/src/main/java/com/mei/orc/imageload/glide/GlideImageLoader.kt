package com.mei.orc.imageload.glide

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.doOnAttach
import androidx.core.view.doOnLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


/**
 *  Created by zzw on 2019-07-18
 *  Des:
 */

/**
 * 滑动暂停加载
 */
fun Context.pauseRequests() {
    try {
        if (checkContentIsDestroyed())
            Glide.with(this).pauseRequests()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 滑动完成启动加载
 */
fun Context.resumeRequests() {
    try {
        if (checkContentIsDestroyed())
            Glide.with(this).resumeRequests()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@JvmOverloads
fun <T> ImageView.displayCenterRound(
        uri: T,
        @DrawableRes placeholderId: Int = 0,
        @DrawableRes errorId: Int = 0,
        /*对图片进行圆角处理并显示*/
        radiusDP: Int = 0,
        cornerType: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL) {

    val transformation = RoundedCornersTransformation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusDP.toFloat(),
            context.resources.displayMetrics).toInt(), 0, cornerType)

    _glideDisplay(uri, RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), transformation)).pe(placeholderId, errorId))

}

@JvmOverloads
fun <T> ImageView.displayCircle(uri: T,
                                @DrawableRes placeholderId: Int = 0,
                                @DrawableRes errorId: Int = 0,
                                noTransform: Boolean = true) {
    _glideDisplay(uri, RequestOptions.circleCropTransform().pe(placeholderId, errorId), noTransform = noTransform)
}


@JvmOverloads
fun <T> ImageView.displayFitCenter(uri: T,
                                   @DrawableRes placeholderId: Int = 0,
                                   @DrawableRes errorId: Int = 0,
                                   isGif: Boolean = false) {
    _glideDisplay(uri, RequestOptions.fitCenterTransform().pe(placeholderId, errorId), isGif = isGif)
}


/**
 * 截取从顶部开始已宽为基准的
 */
@JvmOverloads
fun <T> ImageView.displayImage(
        uri: T,
        @DrawableRes placeholderId: Int = 0,
        @DrawableRes errorId: Int = 0,
        noTransform: Boolean = true,
        isGif: Boolean = false,
        isFitTop: Boolean = false) {

    _glideDisplay(uri, RequestOptions().pe(placeholderId, errorId).apply {
        transform(if (isFitTop) FitTopBitmapTransformation() else CenterCrop()
        )
    }, noTransform, isGif)
}


@JvmOverloads
fun <T> ImageView.glideDisplay(
        uri: T,
        @DrawableRes placeholderId: Int = 0,
        @DrawableRes errorId: Int = 0,
        /*对图片进行圆角处理并显示*/
        radius: Int = 0,
        noTransform: Boolean = true,
        isGif: Boolean = false) {

    _glideDisplay(uri, RequestOptions().pe(placeholderId, errorId).apply {
        if (radius != 0) {
            transform(RoundedCorners(radius))
        }
    }, noTransform, isGif)
}


private fun <T> ImageView._glideDisplay(uri: T,
                                        requestOptions: RequestOptions,
                                        noTransform: Boolean = true,
                                        isGif: Boolean = false) {
    if (!context.checkContentIsDestroyed()) return
//    doOnAttach {
        Glide.with(context).apply {
            if (isGif) {
                asGif()
            }
        }.load(uri).apply {
            if (!noTransform) {
                //CircleImageView希望去掉它，要不然会让图片第一次加载不出来的
                transition(DrawableTransitionOptions.withCrossFade())
            }
            apply(requestOptions)
        }.into(this)
//    }
}

fun <T> Context.displayWithCompleteListener(uri: T, target: DrawableImageViewTarget) {
    try {
        if (checkContentIsDestroyed()) {
            Glide.with(this)
                    .load(if (uri is String) uri.conversion7NiuUrl(target.view.measuredWidth, target.view.measuredHeight) else uri)
                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .apply(RequestOptions.centerCropTransform())
                    .into(target)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

/**
 * 转换为七牛的图片url
 * https://developer.qiniu.com/dora/api/1279/basic-processing-images-imageview2
 */
@Suppress("UNUSED_PARAMETER")
fun String.conversion7NiuUrl(width: Int, height: Int): String {
    return if (isNotEmpty() && (width > 0 || height > 0) && !contains("?") && !startsWith("/storage")) {
        //等比缩放，比例值为宽缩放比和高缩放比的较小值，Width 和 Height 取值范围1-9999。
        when {
            width == 0 -> "${this}?imageMogr2/thumbnail/x${height}"
            height == 0 -> "${this}?imageMogr2/thumbnail/${width}x"
            else -> "${this}?imageMogr2/thumbnail/${width}x${height}"
        }
    } else {
        this
    }
}


@SuppressLint("CheckResult")
private fun RequestOptions.pe(@DrawableRes placeholderId: Int = 0,
                              @DrawableRes errorId: Int = 0): RequestOptions {
    var hasError = false
    if (errorId != 0) {
        error(errorId)
        hasError = true
    }

    if (placeholderId != 0) {
        placeholder(placeholderId)
        if (!hasError) {
            error(placeholderId)
        }
    }

    return this
}


@SuppressLint("ObsoleteSdkInt")
fun Context.checkContentIsDestroyed(): Boolean {
    if (this is ContextWrapper) {
        val activity = (this as? Activity) ?: (this.baseContext as? Activity)
        if (activity != null && (activity.isDestroyed)) {
            return false
        }
    }
    return true

}