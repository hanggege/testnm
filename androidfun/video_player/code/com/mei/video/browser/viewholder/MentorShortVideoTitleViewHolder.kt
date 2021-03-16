package com.mei.video.browser.viewholder

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.dip
import com.mei.wood.R

/**
 *
 * @author Created by lenna on 2020/8/20
 */
class MentorShortVideoTitleViewHolder(itemView: View) : BaseViewHolder(itemView), IShortVideoViewHolder {
    override fun refreshItem(item: Any) {
        if (item is String) {
            setText(R.id.title_tv, item)
            val paddingTop = if ("热门标签" == item) {
                itemView.dip(50)
            } else {
                itemView.dip(25)
            }
            itemView.setPadding(0, paddingTop, 0, itemView.dip(10))
        }
    }
}