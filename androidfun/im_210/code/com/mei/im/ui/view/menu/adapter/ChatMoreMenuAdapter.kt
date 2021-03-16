package com.mei.im.ui.view.menu.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.im.ui.view.menu.ChatMenu
import com.mei.orc.ext.screenWidth
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R

/**
 *
 * @author Created by lenna on 2020/7/14
 */
class ChatMoreMenuAdapter(list: MutableList<ChatMenu>) : BaseQuickAdapter<ChatMenu, BaseViewHolder>(R.layout.include_chat_menu_item, list) {
    override fun convert(holder: BaseViewHolder, item: ChatMenu) {
        holder.setText(R.id.menu_name_tv, item.menuName)
        holder.getView<ImageView>(R.id.menu_icon_iv).glideDisplay(item.menuIcon,item.menuIconId)
        holder.itemView.updateLayoutParams<ViewGroup.LayoutParams> {
            width = screenWidth / 4
        }
    }
}