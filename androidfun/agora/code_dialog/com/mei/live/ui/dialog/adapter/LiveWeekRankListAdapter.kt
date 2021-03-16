package com.mei.live.ui.dialog.adapter

import android.view.ViewGroup
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.live.ui.dialog.adapter.item.WeekRankListFooterViewHolder
import com.mei.live.ui.dialog.adapter.item.WeekRankListViewHolder
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.room.RoomWeekRankList

/**
 *
 * @author Created by lenna on 2020/9/9
 */
class LiveWeekRankListAdapter(list: MutableList<Any>)
    : MeiMultiQuickAdapter<Any, BaseViewHolder>(list), LoadMoreModule {

    override fun getDefItemViewType(position: Int): Int {
        return when (getItemOrNull(position)) {
            is String -> 1
            is RoomWeekRankList.WeekRank -> 2
            else -> -1
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            1 -> WeekRankListFooterViewHolder(parent.layoutInflaterKtToParent(R.layout.item_live_week_rank_footer))

            2 -> WeekRankListViewHolder(parent.layoutInflaterKtToParent(R.layout.item_live_week_rank))

            else -> WeekRankListFooterViewHolder(parent.layoutInflaterKtToParent(R.layout.item_live_week_rank_footer))
        }
    }

    override fun convert(holder: BaseViewHolder, item: Any) {
        (holder as? WeekRankListViewHolder)?.refreshItem(item as? RoomWeekRankList.WeekRank)
        (holder as? WeekRankListFooterViewHolder)?.refreshItem(item as? String)
    }
}