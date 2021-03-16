package com.mei.radio.viewHolder

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.net.model.user.UserInfo

/**
 *
 * @author Created by lenna on 2020/9/23
 * 电台顶部在线听用户viewHolder
 */
class ListenerUserViewHolder(itemView: View) : BaseViewHolder(itemView) {
    fun refreshData(userInfo: UserInfo?) {
        getView<ImageView>(R.id.radio_listening_user_avatar_iv).glideDisplay(userInfo?.avatar)
    }
}