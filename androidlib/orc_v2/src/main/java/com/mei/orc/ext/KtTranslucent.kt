package com.mei.orc.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager


/**
 *  Created by zzw on 2019/5/9
 *  Des:沉浸式状态栏的一些函数
 */


/**
 * 沉浸式状态栏
 */
fun Activity.transparentStatusBar() {
    window?.transparentStatusBar()
}


/**
 * 沉浸式状态栏
 */
fun Window.transparentStatusBar() {
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


/**
 * 状态栏wifi颜色变黑
 */
fun Activity.lightMode() {
    window?.lightMode()
}

/**
 * 状态栏wifi颜色变白
 */
fun Activity.darkMode() {
    window?.darkMode()
}


/**
 * 状态栏wifi颜色变黑
 */
fun Window.lightMode() {
    setMIUIStatusBarDarkIcon(true)
    setMeizuStatusBarDarkIcon(true)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

/**
 * 状态栏wifi颜色变黑
 */
fun Window.darkMode() {
    setMIUIStatusBarDarkIcon(false)
    setMeizuStatusBarDarkIcon(false)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}


/**
 * 修改 MIUI V6  以上状态栏颜色
 */
@SuppressLint("PrivateApi")
private fun Window.setMIUIStatusBarDarkIcon(darkIcon: Boolean) {
    val clazz = this.javaClass
    try {
        val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
        val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
        val darkModeFlag = field.getInt(layoutParams)
        val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
        extraFlagField.invoke(this, if (darkIcon) darkModeFlag else 0, darkModeFlag)
    } catch (e: Exception) {
        //e.printStackTrace();
    }
}


/**
 * 修改魅族状态栏字体颜色 Flyme 4.0
 */
private fun Window.setMeizuStatusBarDarkIcon(darkIcon: Boolean) {
    try {
        val lp = attributes
        val darkFlag = WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
        val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
        darkFlag.isAccessible = true
        meizuFlags.isAccessible = true
        val bit = darkFlag.getInt(null)
        var value = meizuFlags.getInt(lp)
        if (darkIcon) {
            value = value or bit
        } else {
            value = value and bit.inv()
        }
        meizuFlags.setInt(lp, value)
        attributes = lp
    } catch (e: Exception) {
        //e.printStackTrace();
    }

}


/**
 * 获取状态栏高度
 * @return 状态栏高度
 */
fun getStatusBarHeight(): Int {
    val resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
    return Resources.getSystem().getDimensionPixelSize(resourceId)
}


/**
 * 判断是否显示了导航栏   也就是全面屏
 * @return
 */
fun Activity.isShowNavBar(): Boolean {

    val brand = Build.BRAND
    val deviceInfo = when {
        brand.equals("XIAOMI", ignoreCase = true) -> "force_fsg_nav_bar"
        brand.equals("VIVO", ignoreCase = true) || brand.equals("OPPO", ignoreCase = true) -> "navigation_gesture_on"
        else -> "navigationbar_is_min"
    }

    if ("navigationbar_is_min" == deviceInfo) {
        /**
         * 获取应用区域高度
         */
        val outRect1 = Rect()
        window.decorView.getWindowVisibleDisplayFrame(outRect1)
        val activityHeight = outRect1.height()

        /**
         * 获取状态栏高度
         */
        val statuBarHeight = getStatusBarHeight()

        /**
         * 屏幕物理高度 减去 状态栏高度
         */
        val remainHeight = getRealScreenHeight() - statuBarHeight
        /**
         * 剩余高度跟应用区域高度相等 说明导航栏没有显示 否则相反
         */
        return activityHeight != remainHeight
    } else {
        val navigationBarIsMin = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Settings.System.getInt(contentResolver,
                    deviceInfo, 0)
        } else {
            if (Build.BRAND.equals("VIVO", ignoreCase = true) || Build.BRAND.equals("OPPO", ignoreCase = true)) {
                Settings.Secure.getInt(contentResolver,
                        deviceInfo, 0)
            } else {
                Settings.Global.getInt(contentResolver,
                        deviceInfo, 0)
            }
        }
        return navigationBarIsMin != 1
    }

}



