package com.mei.chat.ui.adapter.item.send

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.joker.im.Message
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.message.CustomMessage
import com.mei.chat.ui.view.startPrivateUpstream
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMSendCallStatusHolder(itemView: View) : IMSendBaseMessageHolder(itemView) {
    @SuppressLint("ClickableViewAccessibility")
    override fun refreshChild(msg: Message) {
//        addOnLongClickListener(R.id.im_msg_content)
        val contentTextView = getView<TextView>(R.id.im_msg_content)
//        contentTextView.setOnTouchListener(ClickMovementMethod.newInstance())

        val data = (msg as? CustomMessage)?.customInfo?.data as? ChickCustomData
        contentTextView.text = data?.exclusiveResult?.forSender.orEmpty()
        getView<View>(R.id.im_message_container).setOnClickNotDoubleListener {
            if (data?.exclusiveResult?.canCallbackForSender == true) {
                if (itemView.context is VideoLiveRoomActivity) {
                    UIToast.toast("请在私聊功能内进行回拨操作")
                } else {
                    (itemView.context as? MeiCustomActivity)?.startPrivateUpstream(msg.timMessage.conversation.peer.getInt())
                }
            }
        }
    }
}