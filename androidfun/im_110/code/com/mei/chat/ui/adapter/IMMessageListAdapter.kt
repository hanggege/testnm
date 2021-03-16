package com.mei.chat.ui.adapter

import android.view.ViewGroup
import com.joker.im.Message
import com.mei.chat.ui.adapter.item.IMBaseMessageHolder
import com.mei.widget.holder.MeiMultiQuickAdapter

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/13
 */
class IMMessageListAdapter(list: MutableList<Message>) : MeiMultiQuickAdapter<Message, IMBaseMessageHolder>(list) {

    var playVoicePosition = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getDefItemViewType(position: Int): Int = getMessageType(getItemOrNull(position))
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): IMBaseMessageHolder = createHolder(parent, viewType)

    override fun convert(holder: IMBaseMessageHolder, item: Message) {
        holder.refresh(item, playVoicePosition)
    }
}