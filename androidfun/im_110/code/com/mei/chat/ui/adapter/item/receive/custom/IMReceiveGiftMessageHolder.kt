package com.mei.chat.ui.adapter.item.receive.custom

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.joker.im.Message
import com.joker.im.custom.chick.ChickCustomData
import com.mei.chat.ui.adapter.item.receive.IMReceiveBaseMessageHolder
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMReceiveGiftMessageHolder(itemView: View) : IMReceiveBaseMessageHolder(itemView) {
    @SuppressLint("ClickableViewAccessibility")
    override fun refreshChild(msg: Message) {
        val data = getCustomData()?.data as? ChickCustomData
        data?.gift?.let {
            getView<TextView>(R.id.im_gift_msg_title).apply {
                text = "${data.content.firstOrNull()?.text ?: ""} "
            }
            getView<ImageView>(R.id.im_gift_img).apply {
                glideDisplay(it.giftImg)
            }
        }
    }
}

