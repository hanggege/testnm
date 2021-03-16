package com.mei.push

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import com.mei.base.ui.nav.Nav
import com.mei.orc.util.permission.JumpPermissionManagement
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher


/**
 *  Created by zzw on 2019-10-08
 *  Des:
 */


/**
 * 检查是否有通知权限
 */
fun FragmentActivity.checkPushStatus() {
    if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
        val data = DialogData("为了更好的体验知心，关注的知心达人开播通知及活动提醒", title = "开启消息通知", cancelStr = "", okStr = "开启")
        NormalDialogLauncher.newInstance().showDialog(this, data = data, okBack = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent().apply {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo.uid)
                }
                if (Nav.isIntentAvailable(this, intent)) startActivity(intent)
            } else {
                JumpPermissionManagement.GoToSetting(this)
            }
        })
    }

}