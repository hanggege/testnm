package com.mei.live.ui.dialog.adapter.item

import android.annotation.SuppressLint
import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.wood.R

/**
 *
 * @author Created by lenna on 2020/9/9
 */
class WeekRankListFooterViewHolder(itemView: View) : BaseViewHolder(itemView) {
    @SuppressLint("SetTextI18n")
    fun refreshItem(data: String?) {
        setText(R.id.footer_tv, data.orEmpty())
    }
}