package com.mei.radio.adapter

import android.view.ViewGroup
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.radio.viewHolder.ListenerUserViewHolder
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.user.UserInfo

/**
 *
 * @author Created by lenna on 2020/9/23
 * 正在听的用户显示适配
 */
class ListeningUserAdapter(list: MutableList<UserInfo>) : MeiMultiQuickAdapter<UserInfo, BaseViewHolder>(list) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ListenerUserViewHolder(parent.layoutInflaterKtToParent(R.layout.item_radio_listening_user))
    }

    override fun convert(holder: BaseViewHolder, item: UserInfo) {
        (holder as? ListenerUserViewHolder)?.refreshData(item)
    }
}