package com.mei.orc.util.notification

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

/**
 *  Created by zzw on 2019-12-19
 *  Des:
 */


fun Context.hasNotificationPermission(): Boolean = NotificationManagerCompat.from(this).areNotificationsEnabled()


// https://www.jianshu.com/p/af6b080b547f
fun Context.jumpNotificationSetting() {
    try {
        /**
         * 跳到通知栏设置界面
         * @param context
         */
        /**
         * 跳到通知栏设置界面
         * @param context
         */
        val localIntent = Intent()
        ///< 直接跳转到应用通知设置的代码
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            localIntent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            localIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            localIntent.putExtra("app_package", packageName)
            localIntent.putExtra("app_uid", applicationInfo.uid)
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            localIntent.addCategory(Intent.CATEGORY_DEFAULT)
            localIntent.data = Uri.parse("package:$packageName")
        }
        startActivity(localIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }

}