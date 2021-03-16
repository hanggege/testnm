package com.mei.chat.ui.adapter.item.send

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.joker.im.Message
import com.joker.im.message.VoiceMessage
import com.mei.orc.ext.dip
import com.mei.orc.util.date.formatTimeVoice
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMSendVoiceMessageHolder(itemView: View) : IMSendBaseMessageHolder(itemView) {
    override fun refreshChild(msg: Message) {
        (msg as? VoiceMessage)?.let {

            itemView.findViewById<ImageView>(R.id.im_message_voice_img)?.let {
                it.setImageResource(if (bindingAdapterPosition == playVoicePosition) R.drawable.chat_voice_to_icon else R.drawable.chat_chatto_voice_playing_f3)
                (it.drawable as? AnimationDrawable)?.start()
            }
            itemView.findViewById<View>(R.id.im_message_container)?.let { v ->
                val voiceWidth = itemView.dip(70) + if (it.getDuration() > 20) itemView.dip(100) else 5 * it.getDuration().toInt()
                v.layoutParams = v.layoutParams?.apply { width = voiceWidth }
            }
            itemView.findViewById<TextView>(R.id.im_voice_time)?.text = it.getDuration().toInt().formatTimeVoice()
        }
    }
}