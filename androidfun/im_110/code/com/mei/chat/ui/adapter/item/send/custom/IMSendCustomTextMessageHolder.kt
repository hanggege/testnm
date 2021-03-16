package com.mei.chat.ui.adapter.item.send.custom

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.bun.miitmdid.core.JLibrary.context
import com.joker.im.Message
import com.joker.im.custom.chick.ChickCustomData
import com.mei.base.ui.nav.Nav
import com.mei.chat.ui.adapter.item.send.IMSendBaseMessageHolder
import com.mei.live.ext.createSplitSpannable
import com.mei.orc.util.click.ClickMovementMethod
import com.mei.widget.holder.addOnLongClickListener
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMSendCustomTextMessageHolder(itemView: View) : IMSendBaseMessageHolder(itemView) {
    @SuppressLint("ClickableViewAccessibility")
    override fun refreshChild(msg: Message) {
        val data = getCustomData()?.data as? ChickCustomData
        addOnLongClickListener(R.id.im_msg_content)
        val contentTextView = getView<TextView>(R.id.im_msg_content)
        contentTextView.setOnTouchListener(ClickMovementMethod.newInstance())
        contentTextView.text = data?.content.orEmpty().createSplitSpannable(Color.parseColor("#333333"),click = {
            Nav.toAmanLink(itemView.context, it)
        } )
    }
}