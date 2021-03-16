package com.mei.message.holder

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 *
 * @author Created by lenna on 2020/6/15
 */
abstract class VisitorsBaseViewHolder(itemView: View) : BaseViewHolder(itemView) {
    abstract fun refreshItem(data: Any?, isLastPosition: Boolean)
}