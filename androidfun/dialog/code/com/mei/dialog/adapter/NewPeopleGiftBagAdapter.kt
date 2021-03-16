package com.mei.dialog.adapter

import android.view.ViewGroup
import com.mei.dialog.adapter.item.NewPeopleGiftItemViewHolder
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.bag.NewPeopleGiftBagInfo

/**
 *
 * @author Created by lenna on 2020/11/16
 * 新人礼包显示优惠券列表
 */
class NewPeopleGiftBagAdapter(list: MutableList<NewPeopleGiftBagInfo.GiftBagResources>) : MeiMultiQuickAdapter<NewPeopleGiftBagInfo.GiftBagResources, NewPeopleGiftItemViewHolder>(list) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): NewPeopleGiftItemViewHolder {
        return NewPeopleGiftItemViewHolder(parent.layoutInflaterKtToParent(R.layout.item_new_people_gift_bag))
    }

    override fun convert(holder: NewPeopleGiftItemViewHolder, item: NewPeopleGiftBagInfo.GiftBagResources) {
        holder.refreshData(item)
    }
}