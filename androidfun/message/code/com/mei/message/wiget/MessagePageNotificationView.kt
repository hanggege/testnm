package com.mei.message.wiget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.orc.util.date.formatToString
import com.mei.orc.util.save.mmkv
import com.mei.wood.R
import com.net.MeiUser
import kotlinx.android.synthetic.main.include_open_notifiction_view.view.*
import java.util.*

/**
 *
 * @author Created by lenna on 2020/6/15
 */
class MessagePageNotificationView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attributeSet, defStyle) {
    init {
        var view = layoutInflaterKtToParent(R.layout.include_open_notifiction_view)
        addView(view)
        isVisible = false
    }

    fun loadingNotificationState(isOpen: Boolean) {
        val info = MeiUser.getSharedUser().info
        if (info?.isPublisher == true) {
            isVisible = isOpen
            notification_hint_tv.text = "推送通知未开启"
            close_open_notification_iv.isVisible = false
        } else {
            notification_hint_tv.text = "开启推送通知，不再错过消息提醒"
            close_open_notification_iv.isVisible = true
            val date = Date(System.currentTimeMillis())
            val dateStr = date.formatToString("yy年MM月dd日")
            val resultDateStr = mmkv.getString("conversationNotification", null)
            isVisible = if (resultDateStr != null) {
                if (resultDateStr == dateStr) {
                    false
                } else {
                    isOpen
                }
            } else {
                isOpen
            }
        }
        close_open_notification_iv.setOnClickListener {
            val date = Date(System.currentTimeMillis())
            val dateStr = date.formatToString("yy年MM月dd日")
            mmkv.putString("conversationNotification", dateStr)
            isVisible = false
        }
    }
}