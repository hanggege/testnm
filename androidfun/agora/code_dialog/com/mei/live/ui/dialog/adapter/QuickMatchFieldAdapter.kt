package com.mei.live.ui.dialog.adapter

import android.view.ViewGroup
import com.mei.live.ui.dialog.adapter.item.FieldViewHolder
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.room.MatchInfo

/**
 *
 * @author Created by lenna on 2020/11/16
 * 快速咨询擅长领域
 */
class QuickMatchFieldAdapter(list: MutableList<MatchInfo.PublisherSkillsBean>) : MeiMultiQuickAdapter<MatchInfo.PublisherSkillsBean, FieldViewHolder>(list) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        return FieldViewHolder(parent.layoutInflaterKtToParent(R.layout.item_quick_match_field))
    }

    override fun convert(holder: FieldViewHolder, item: MatchInfo.PublisherSkillsBean) {
        holder.refreshData(item)
    }
}