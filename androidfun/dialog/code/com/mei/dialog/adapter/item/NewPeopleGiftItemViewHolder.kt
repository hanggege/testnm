package com.mei.dialog.adapter.item

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.live.ext.createSplitSpannable
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.net.model.bag.NewPeopleGiftBagInfo

/**
 *
 * @author Created by lenna on 2020/11/16
 * 新人礼包所领取的优惠券展示
 */
class NewPeopleGiftItemViewHolder(view: View) : BaseViewHolder(view) {
    fun refreshData(item: NewPeopleGiftBagInfo.GiftBagResources) {
        getView<TextView>(R.id.gift_bag_coupon_title).apply {
            text = item.tag?.createSplitSpannable(Cxt.getColor(R.color.color_333333))
        }
        getView<TextView>(R.id.gift_tag_coupon_subtitle).apply {
            text = item.mainTitle?.createSplitSpannable(Cxt.getColor(R.color.color_333333))
        }
        getView<TextView>(R.id.gift_tag_coupon_hint).apply {
            text = item.subTitle?.createSplitSpannable(Cxt.getColor(R.color.color_333333))
        }
        getView<ImageView>(R.id.new_people_gift_bag_coupon_bg).apply {
            glideDisplay(item.backGround.orEmpty())
        }
    }
}