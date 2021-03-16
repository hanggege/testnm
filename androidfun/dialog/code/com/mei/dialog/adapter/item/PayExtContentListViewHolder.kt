package com.mei.dialog.adapter.item

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.wood.R
import com.net.model.bag.NewPeopleGiftBagInfo

/**
 *
 * @author Created by lenna on 2020/11/17
 */
class PayExtContentListViewHolder(view: View) : BaseViewHolder(view) {
    fun refreshData(item: NewPeopleGiftBagInfo.GiftBagResources) {
        getView<TextView>(R.id.pay_ext_content).apply {
            paint.isFakeBoldText = true
            text = item.mainTitleText.orEmpty()
        }
    }
}