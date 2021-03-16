package com.mei.im.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.Conversation
import com.mei.live.manager.genderAvatar
import com.mei.orc.ext.letElse
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.util.string.getInt
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.tencent.imsdk.TIMConversationType

/**
 * 佛祖保佑         永无BUG

 * Created by joker on 2017/2/21.
 */

class IMSelectConverAdapter(data: MutableList<Conversation>) : BaseQuickAdapter<Conversation, BaseViewHolder>(R.layout.chat_share_select_item, data) {

    override fun convert(holder: BaseViewHolder, item: Conversation) {
        holder.getView<View>(R.id.root_conver_select).isActivated = holder.layoutPosition + 1 == itemCount
        if (item.type == TIMConversationType.C2C) {
            val userInfo = getCacheUserInfo(item.peer.getInt())
            userInfo.letElse(nonullBack = {
                holder.setText(R.id.message_nick_name, userInfo?.realName)
                holder.getView<RoundImageView>(R.id.avatar_img).glideDisplay(userInfo?.avatar.orEmpty(), userInfo?.gender.genderAvatar(userInfo?.isPublisher))
            }, nullBack = {
                holder.setText(R.id.message_nick_name, "")
                        .setImageResource(R.id.avatar_img, R.drawable.default_avatar_72)
            })
        }

    }
}
