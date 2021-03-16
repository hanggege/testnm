package com.mei.base

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-24
 */

internal fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

/**屏幕的宽高**/
internal inline val screenWidth: Int
    get() = Resources.getSystem().displayMetrics.widthPixels

internal inline val screenHeight: Int
    get() = Resources.getSystem().displayMetrics.heightPixels

/**
 * 沉浸式状态栏
 */
internal fun Activity.transparentStatusBar() {
    window?.transparentStatusBar()
}


/**
 * 沉浸式状态栏
 */
internal fun Window.transparentStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = Color.TRANSPARENT
        } else {
            val attributes = attributes
            val flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            attributes.flags = attributes.flags or flagTranslucentStatus
            setAttributes(attributes)
        }
    }
}