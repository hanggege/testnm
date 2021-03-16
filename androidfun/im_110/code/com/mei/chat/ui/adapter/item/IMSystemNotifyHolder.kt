package com.mei.chat.ui.adapter.item

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.joker.im.Message
import com.joker.im.custom.chick.ChickCustomData
import com.mei.chat.ui.adapter.item.receive.custom.buildChickContent
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/13
 */
class IMSystemNotifyHolder(itemView: View) : IMBaseMessageHolder(itemView) {

    override fun refreshChild(msg: Message) {
        val data = getCustomData()?.data as? ChickCustomData
        val imSystemNotifyTv = itemView.findViewById<TextView>(R.id.im_system_notify_tv)
        if (data == null) {
            imSystemNotifyTv.text = msg.getSummary()
        } else {
            imSystemNotifyTv.buildChickContent(data, Color.WHITE)
        }
    }
}