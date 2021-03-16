package com.mei.live.ui.dialog.adapter.item

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.wood.R
import com.net.model.room.MatchInfo

/**
 *
 * @author Created by lenna on 2020/11/16
 */
class FieldViewHolder(view: View) : BaseViewHolder(view) {
    fun refreshData(item: MatchInfo.PublisherSkillsBean?) {
        setText(R.id.field_name_tv, item?.name.orEmpty())
    }
}