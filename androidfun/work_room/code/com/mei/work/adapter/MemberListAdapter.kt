package com.mei.work.adapter

import android.view.ViewGroup
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.layoutInflaterKt
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.mei.work.adapter.item.MemberListViewHolder
import com.net.model.chick.workroom.WorkRoomMember

/**
 *
 * @author Created by lenna on 2020/7/30
 * 工作室成员显示适配器
 */
class MemberListAdapter(data: MutableList<WorkRoomMember>) : MeiMultiQuickAdapter<WorkRoomMember, BaseViewHolder>(data) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return MemberListViewHolder(parent.layoutInflaterKt(R.layout.item_member_list_view))
    }

    override fun convert(holder: BaseViewHolder, item: WorkRoomMember) {
        (holder as? MemberListViewHolder)?.refreshItem(item,getItemPosition(item))
    }
}