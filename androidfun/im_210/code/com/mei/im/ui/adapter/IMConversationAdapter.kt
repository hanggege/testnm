package com.mei.im.ui.adapter

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.core.view.isVisible
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.Conversation
import com.mei.chat.ui.adapter.item.chatTimeString
import com.mei.live.manager.genderAvatar
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.string.maxNinetyNine
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.net.MeiUser
import com.net.model.room.RoomInfo

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/22
 */
class IMConversationAdapter(val list: MutableList<Any>) : MeiMultiQuickAdapter<Any, BaseViewHolder>(list) {

    override fun getDefItemViewType(position: Int): Int {
        return when (list.getOrNull(position)) {
            is Conversation -> 1
            is RoomInfo -> 2
            else -> 0
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView = when (viewType) {
            1 -> parent.layoutInflaterKt(R.layout.dialog_conversation_item)
            2 -> parent.layoutInflaterKt(R.layout.dialog_conversation_mentor_item)
            else -> Space(parent.context)
        }
        return BaseViewHolder(itemView)
    }

    override fun convert(holder: BaseViewHolder, item: Any) {

        when (item) {
            is Conversation -> {
                //专属服务标签
                val info = getCacheUserInfo(item.peer.toIntOrNull() ?: 0)
                val serviceInfo = if (MeiUser.getSharedUser().info?.groupInfo != null) {
                    info?.medalMap.orEmpty()[MeiUser.getSharedUser().info?.groupInfo?.groupId.toString()]
                } else {
                    info?.medalMap.orEmpty()[JohnUser.getSharedUser().userIDAsString]
                }
                holder.getView<TextView>(R.id.live_im_message_service_representation).apply {
                    isVisible = serviceInfo != null
                    text = serviceInfo?.medal.orEmpty()
                }
                holder.setVisible(R.id.conver_divider_view, holder.bindingAdapterPosition < list.size)
                        .setText(R.id.time_stamp_tv, chatTimeString(item.getLastMessageTime()))
                        .setText(R.id.last_msg_content_tv, item.getLastMessageSummary())
                        .setText(R.id.user_name_tv, info?.nickname.orEmpty())
                holder.getView<RoundImageView>(R.id.user_avatar_img).glideDisplay(info?.avatar.orEmpty(), info?.gender.genderAvatar(info?.isPublisher))
                val unReadNum = item.timConversation.unreadMessageNum
                holder.setText(R.id.red_point_view, if (unReadNum > 0) unReadNum.maxNinetyNine() else "")
            }
            is RoomInfo -> {
                holder.getView<ImageView>(R.id.user_avatar_img).glideDisplay(item.createUser?.avatar.orEmpty(), item.createUser?.gender.genderAvatar())
                holder.setText(R.id.user_name_tv, item.createUser?.nickName)
            }

        }

    }
}