package com.mei.dialog.adapter

import android.view.ViewGroup
import com.mei.dialog.adapter.item.PayExtContentListViewHolder
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.bag.NewPeopleGiftBagInfo

/**
 *
 * @author Created by lenna on 2020/11/17
 * 显示支付购买的内容列表
 */
class PayExtContentListAdapter(list: MutableList<NewPeopleGiftBagInfo.GiftBagResources>) : MeiMultiQuickAdapter<NewPeopleGiftBagInfo.GiftBagResources, PayExtContentListViewHolder>(list) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): PayExtContentListViewHolder {
        return PayExtContentListViewHolder(parent.layoutInflaterKtToParent(R.layout.item_pay_ext_contents))
    }

    override fun convert(holder: PayExtContentListViewHolder, item: NewPeopleGiftBagInfo.GiftBagResources) {
        holder.refreshData(item)
    }
}