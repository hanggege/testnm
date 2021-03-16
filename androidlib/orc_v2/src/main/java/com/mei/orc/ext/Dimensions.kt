@file:Suppress("NOTHING_TO_INLINE")

package com.mei.orc.ext

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import androidx.fragment.app.Fragment as SupportFragment

/**
 * 佛祖保佑         永无BUG
 *
 * Created by joker on 2017/3/1.
 */

//returns dip(dp) dimension value in pixels

inline fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

inline fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()

//return sp dimension value in pixels
inline fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()

inline fun Context.sp(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

//converts px value into dip or sp
inline fun Context.px2dip(px: Int): Float = (px.toFloat() / resources.displayMetrics.density)

inline fun Context.px2sp(px: Int): Float = (px.toFloat() / resources.displayMetrics.scaledDensity)

inline fun Context.dimen(resource: Int): Int = resources.getDimensionPixelSize(resource)

inline fun Context.sp2px(spValue: Float): Int = (spValue * resources.displayMetrics.scaledDensity + 0.5f * (if (spValue >= 0.0f) 1 else -1).toFloat()).toInt()

//the same for the views
inline fun View.dip(value: Int): Int = context.dip(value)

inline fun View.dip(value: Float): Int = context.dip(value)
inline fun View.sp(value: Int): Int = context.sp(value)
inline fun View.sp(value: Float): Int = context.sp(value)
inline fun View.px2dip(px: Int): Float = context.px2dip(px)
inline fun View.px2sp(px: Int): Float = context.px2sp(px)
inline fun View.dimen(resource: Int): Int = context.dimen(resource)

//the same for Fragments
inline fun SupportFragment.dip(value: Int): Int = activity?.dip(value) ?: 0
inline fun dip(value: Int): Int = (value * Resources.getSystem().displayMetrics.density).toInt()

inline fun SupportFragment.dip(value: Float): Int = activity?.dip(value) ?: 0
inline fun SupportFragment.sp(value: Int): Int = activity?.sp(value) ?: 0
inline fun SupportFragment.sp(value: Float): Int = activity?.sp(value) ?: 0
inline fun SupportFragment.px2dip(px: Int): Float = activity?.px2dip(px) ?: 0f
inline fun SupportFragment.px2sp(px: Int): Float = activity?.px2sp(px) ?: 0f
inline fun SupportFragment.dimen(resource: Int): Int = activity?.dimen(resource) ?: 0

inline val Int.dp: Float
    get() = Resources.getSystem().displayMetrics.density * this

inline val Float.dp2px: Float
    get() = Resources.getSystem().displayMetrics.density * this

/**屏幕的宽高**/
inline val screenWidth: Int
    get() = Resources.getSystem().displayMetrics.widthPixels

inline val screenHeight: Int
    get() = Resources.getSystem().displayMetrics.heightPixels


/**
 * 获取真正的高度,包括状态栏
 */
fun Activity.getRealScreenHeight(): Int {
    val metrics = DisplayMetrics()
    //获取真正的高度  正常使用
    windowManager.defaultDisplay.getRealMetrics(metrics)
    return metrics.heightPixels
}