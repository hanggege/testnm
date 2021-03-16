package com.mei.me.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import com.joker.im.custom.chick.InviteJoinInfo
import com.mei.base.ui.nav.Nav
import com.mei.orc.Cxt
import com.mei.orc.util.permission.JumpPermissionManagement
import com.mei.orc.util.save.getBooleanMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.wood.R
import com.mei.wood.event.ISystemInviteJoinShow
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.MeiUser
import kotlinx.android.synthetic.main.activity_new_message_notification_setting.*

/**
 * 新消息通知提醒设置
 * @author Created by lenna on 2020/6/11
 */
class NewMessageNotificationSettingActivity : MeiCustomBarActivity(), ISystemInviteJoinShow {
    private var mIsOpenNotification: Boolean = false

    override fun isShow(sendId: String, info: InviteJoinInfo): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message_notification_setting)
        title = "新消息提醒"
        initView()
    }

    private fun initView() {
        /**本地存储基本设置*/
        val userId = MeiUser.getSharedUser().info?.userId
        new_message_banner_sb.isChecked = "new_message_banner_sb$userId".getBooleanMMKV(true)
        new_message_voice_sb.isChecked = "new_message_voice_sb$userId".getBooleanMMKV(true)
        new_message_vibration_sb.isChecked = "new_message_vibration_sb$userId".getBooleanMMKV(true)
        new_message_invitation_sb.isChecked = "new_message_invitation_sb$userId".getBooleanMMKV(true)
        new_message_banner_sb.setOnCheckedChangeListener { _, isChecked ->
            "new_message_banner_sb$userId".putMMKV(isChecked)
        }
        new_message_voice_sb.setOnCheckedChangeListener { _, isChecked ->
            "new_message_voice_sb$userId".putMMKV(isChecked)
        }
        new_message_vibration_sb.setOnCheckedChangeListener { _, isChecked ->
            "new_message_vibration_sb$userId".putMMKV(isChecked)
        }
        new_message_invitation_sb.setOnCheckedChangeListener { _, isChecked ->
            "new_message_invitation_sb$userId".putMMKV(isChecked)
//            postAction(SYSTEM_INVITE_OPEN_OFF)
        }
        system_message_notification_setting_rl.setOnClickListener {
            if (!mIsOpenNotification) {
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
            }
        }
    }


    override fun onResume() {
        super.onResume()
        mIsOpenNotification = NotificationManagerCompat.from(this).areNotificationsEnabled()
        setSystemNotification(mIsOpenNotification)
    }

    private fun setSystemNotification(openNotification: Boolean) {
        system_message_arrow.isVisible = !openNotification
        if (openNotification) {
            notification_state_tv.text = "已开启"
            notification_state_tv.setTextColor(Cxt.getColor(R.color.color_999999))
        } else {
            notification_state_tv.text = "未开启"
            notification_state_tv.setTextColor(Cxt.getColor(R.color.color_333333))
        }
    }

}