package com.mei.message.adapter

import android.graphics.Color
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.Conversation
import com.joker.im.custom.CustomType
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.chat.ui.adapter.item.chatTimeString
import com.mei.live.ext.createSplitSpannable
import com.mei.live.manager.genderAvatar
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.util.string.maxNinetyNine
import com.mei.widget.holder.setVisibleAndGone
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.net.MeiUser
import com.net.model.chick.tab.tabbarConfig

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020-02-19
 */


class ConversationAdapter(val list: MutableList<Conversation>, val stickTopList: MutableSet<String>) :
        BaseQuickAdapter<Conversation, BaseViewHolder>(R.layout.instant_conversation_adapter_item, list) {

    override fun convert(holder: BaseViewHolder, item: Conversation) {
        val info = getCacheUserInfo(item.peer.toIntOrNull() ?: 0)
        holder.setText(R.id.iv_service_representation_view, tabbarConfig.diamondEmoji.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_333333)))
        holder.setVisibleAndGone(R.id.iv_service_representation_view, info?.specialFlag == true)
        if (MeiUser.getSharedUser().info?.isPublisher == true) {
            holder.setGone(R.id.conversation_list_tag_iv, info?.upFlag != true)
            if (info?.upFlag == true) {
                holder.setImageResource(R.id.conversation_list_tag_iv, R.drawable.icon_message_visitors_connect)
            }
        }


        holder.setVisible(R.id.conver_divider_view, holder.bindingAdapterPosition < list.size)
                .setText(R.id.time_stamp_tv, chatTimeString(item.getLastMessageTime()))
                .setText(R.id.last_msg_content_tv, item.getLastMessageSummary())
                .setText(R.id.user_name_tv, info?.nickname.orEmpty())
                .setVisibleAndGone(R.id.work_room_tag_tv, info?.groupInfo != null)
                .setText(R.id.work_room_tag_tv, info?.groupInfo?.groupName.orEmpty())
        holder.getView<RoundImageView>(R.id.user_avatar_img).glideDisplay(info?.avatar.orEmpty(), info?.gender.genderAvatar(info?.isPublisher))
        /**产品刘紫茵说了，最后一条消息是拉黑的消息时不进行未读计数，但是如果最后一条消息之前有拉黑未读的消息需要进行计数**/
        var count = 0
        if ((item.timConversation.lastMsg.mapToMeiMessage()
                        as? CustomMessage)?.customMsgType
                == CustomType.exclusive_system_notify && !item.timConversation.lastMsg.isRead) {
            count = 1
        }
        val unReadNum = item.timConversation.unreadMessageNum - count
        holder.setText(R.id.red_point_view, if (unReadNum > 0) unReadNum.maxNinetyNine() else "")
        holder.setGone(R.id.chat_online_v, info?.online != true)
        holder.itemView.setBackgroundColor(if (stickTopList.contains(item.peer)) Color.parseColor("#F9F9F9") else Color.WHITE)

    }
}