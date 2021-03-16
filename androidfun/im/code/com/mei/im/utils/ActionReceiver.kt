package com.mei.im.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 佛祖保佑         永无BUG

 * Created by joker on 2017/2/22.
 */

open class ActionReceiver(val callback: (Intent) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null)
            callback(intent)
    }
}
