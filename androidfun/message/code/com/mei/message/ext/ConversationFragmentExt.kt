package com.mei.message.ext

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.contains
import com.mei.base.ui.nav.Nav
import com.mei.message.ConversationFragment
import com.mei.message.wiget.MentorMessageHeaderView
import com.mei.message.wiget.MessagePageNotificationView
import com.mei.orc.http.exception.RxThrowable
import com.mei.orc.http.listener.RequestListener
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.permission.JumpPermissionManagement
import com.net.MeiUser
import com.net.model.chick.user.ChickUserInfo
import java.lang.ref.SoftReference


/**
 *
 * @author Created by lenna on 2020/6/12
 * 会话扩展文件
 */

/**加载是否打开通知提醒*/
fun ConversationFragment.loadingOpenNotification() {
    if (isAdded) {
        val sr = SoftReference(activity)
        val isOpenNotification = sr.get()?.let { NotificationManagerCompat.from(it).areNotificationsEnabled() }
        if (mNotifyLayout == null) {
            mNotifyLayout = sr.get()?.let { MessagePageNotificationView(it) }
        }
        mNotifyLayout?.let {
            val headerContainer = conversationAdapter.headerLayout
            if (headerContainer == null || (headerContainer as? ViewGroup)?.contains(it) == false) {
                conversationAdapter.removeHeaderView(it)
                conversationAdapter.addHeaderView(it)
                conversationAdapter.notifyDataSetChanged()
            }

        }
        mNotifyLayout?.loadingNotificationState(isOpenNotification != true)
        mNotifyLayout?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent().apply {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, activity?.packageName)
                    putExtra(Settings.EXTRA_CHANNEL_ID, activity?.applicationInfo?.uid)
                }
                if (Nav.isIntentAvailable(activity, intent)) startActivity(intent)
            } else {
                JumpPermissionManagement.GoToSetting(activity)
            }
        }
    }

}

/***
 * 添加顶部菜单栏
 */
fun ConversationFragment.addHeaderMenuItemView() {
    if (isAdded) {
        val sr = SoftReference(activity)
        if (mMentorMessageHeaderView == null) {
            mMentorMessageHeaderView = sr.get()?.let { MentorMessageHeaderView(it) }
        }
        mMentorMessageHeaderView?.let {
            val headerContainer = conversationAdapter.headerLayout
            if (headerContainer == null || (headerContainer as? ViewGroup)?.contains(it) == false) {
                conversationAdapter.removeHeaderView(it)
                conversationAdapter.addHeaderView(it)
                conversationAdapter.notifyDataSetChanged()
            }

            if (JohnUser.getSharedUser().hasLogin()) {
                refreshHeaderState(true)
            }
        }
    }
}

/**刷新头部状态*/
fun ConversationFragment.refreshHeaderState(isLogin: Boolean?) {
    if (isLogin == true) {
        MeiUser.resetUser(apiSpiceMgr, object : RequestListener<ChickUserInfo.Response> {
            override fun onRequestFailure(retrofitThrowable: RxThrowable?) {
            }

            override fun onRequestSuccess(result: ChickUserInfo.Response) {
                mMentorMessageHeaderView?.loadState(result.data?.info?.isPublisher)
            }

        })
    } else {
        mMentorMessageHeaderView?.loadState(false)
    }
}

/**更新头部通知权限状态*/
fun ConversationFragment.notifyNotificationState() {
    val isOpenNotification = activity?.let { NotificationManagerCompat.from(it).areNotificationsEnabled() }
    mNotifyLayout?.loadingNotificationState(isOpenNotification != true)
}
