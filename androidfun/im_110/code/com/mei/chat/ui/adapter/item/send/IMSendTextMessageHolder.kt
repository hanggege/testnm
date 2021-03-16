package com.mei.chat.ui.adapter.item.send

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.joker.im.Message
import com.mei.chat.ui.adapter.item.receive.makeCustomLink
import com.mei.orc.util.click.ClickMovementMethod
import com.mei.widget.holder.addOnLongClickListener
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMSendTextMessageHolder(itemView: View) : IMSendBaseMessageHolder(itemView) {
    @SuppressLint("ClickableViewAccessibility")
    override fun refreshChild(msg: Message) {
        addOnLongClickListener(R.id.im_msg_content)
        val contentTextView = getView<TextView>(R.id.im_msg_content)
        contentTextView.setOnTouchListener(ClickMovementMethod.newInstance())
        contentTextView.text = ""
        contentTextView.makeCustomLink(msg.getSummary())
    }
}