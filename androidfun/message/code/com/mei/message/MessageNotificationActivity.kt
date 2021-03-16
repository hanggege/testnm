package com.mei.message

import android.os.Bundle
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomBarActivity
import launcher.MakeResult

/**
 * 通知页面
 * @author Created by lenna on 2020/6/12
 */
@MakeResult(includeStartForResult = true)
class MessageNotificationActivity : MeiCustomBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_notification)
        title = "通知"
    }
}