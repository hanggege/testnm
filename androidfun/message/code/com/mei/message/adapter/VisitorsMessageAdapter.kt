package com.mei.message.adapter

import android.view.ViewGroup
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.message.holder.VisitorsBaseViewHolder
import com.mei.message.holder.VisitorsItemViewHolder
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.chick.message.VisitorsMessage

/**
 *
 * @author Created by lenna on 2020/6/15
 * 访问记录适配
 */
const val MESSAGE_TYPE_VISITORS_MESSAGE = 0 //访客消息

class VisitorsMessageAdapter(val list: MutableList<Any>)
    : MeiMultiQuickAdapter<Any, BaseViewHolder>(list), LoadMoreModule {
    override fun getDefItemViewType(position: Int): Int {
        return when (getItem(position)) {

            is VisitorsMessage.Visitors -> {
                MESSAGE_TYPE_VISITORS_MESSAGE
            }
            else -> {
                MESSAGE_TYPE_VISITORS_MESSAGE
            }
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            MESSAGE_TYPE_VISITORS_MESSAGE ->
                VisitorsItemViewHolder(parent.layoutInflaterKtToParent(R.layout.item_message_visitors))
            else ->
                VisitorsItemViewHolder(parent.layoutInflaterKtToParent(R.layout.item_message_visitors))
        }
    }

    override fun convert(holder: BaseViewHolder, item: Any) {
        if (holder is VisitorsBaseViewHolder) {
            holder.refreshItem(item, getItemPosition(item) == list.size - 1)
        }
    }
}