package com.mei.radio.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.wood.R
import com.net.model.radio.RadioSchedulingInfo

/**
 * RadioTimingAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-22
 */
class RadioScheduleAdapter(list: MutableList<RadioSchedulingInfo.RadioScheduleBean>) : BaseQuickAdapter<RadioSchedulingInfo.RadioScheduleBean, BaseViewHolder>(R.layout.item_radio_timing, list) {
    var selectedPos = 0
    override fun convert(holder: BaseViewHolder, item: RadioSchedulingInfo.RadioScheduleBean) {
        val isSelected = selectedPos == holder.layoutPosition
        holder.setText(R.id.radio_timing_title, item.title.orEmpty())
                .itemView.isSelected = isSelected

    }
}