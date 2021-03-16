package com.mei.chat.ui.adapter.item.receive

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.joker.im.Message
import com.mei.chat.ui.adapter.item.IMBaseMessageHolder
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.tencent.imsdk.TIMConversationType

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 * 接收的消息基类
 */
abstract class IMReceiveBaseMessageHolder(itemView: View) : IMBaseMessageHolder(itemView) {

    override fun refreshBase(msg: Message) {
        super.refreshBase(msg)
        itemView.findViewById<TextView>(R.id.group_user_mark)?.apply {
            isVisible = msg.chatType == TIMConversationType.Group
            text = getCacheUserInfo(msg.sender.getInt())?.realName ?: ""
        }
    }


}