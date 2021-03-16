package com.mei.chat.ui.adapter.item.receive.custom

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import com.joker.im.Message
import com.joker.im.custom.chick.ChickCustomData
import com.mei.base.ui.nav.Nav
import com.mei.chat.ui.adapter.item.receive.IMReceiveBaseMessageHolder
import com.mei.live.ext.createSplitSpannable
import com.mei.orc.util.click.ClickMovementMethod
import com.mei.widget.holder.addOnLongClickListener
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMReceiveCustomTextMessageHolder(itemView: View) : IMReceiveBaseMessageHolder(itemView) {
    @SuppressLint("ClickableViewAccessibility")
    override fun refreshChild(msg: Message) {
        val data = getCustomData()?.data as? ChickCustomData
        addOnLongClickListener(R.id.im_msg_content)
        val contentTextView = getView<TextView>(R.id.im_msg_content)
        contentTextView.setOnTouchListener(ClickMovementMethod.newInstance())
        contentTextView.buildChickContent(data)
    }
}

fun TextView.buildChickContent(data: ChickCustomData?, color: Int = Color.parseColor("#333333")) {
    movementMethod = LinkMovementMethod.getInstance()
    text = data?.content.orEmpty().createSplitSpannable(color, click = {
        Nav.toAmanLink(context, it)
    })
}
