package com.mei.me.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.net.model.chick.friends.UserHomePagerResult

/**
 *
 *
 * @author Created by Song Jian on 2019-12-16
 */

class GiftReceiveAdapter(val list: MutableList<UserHomePagerResult.ReceiveGiftBean>) :
        BaseQuickAdapter<UserHomePagerResult.ReceiveGiftBean, BaseViewHolder>(R.layout.item_gift_show, list) {


    override fun convert(holder: BaseViewHolder, item: UserHomePagerResult.ReceiveGiftBean) {
        val img = holder.getView<ImageView>(R.id.gift_show_img)
        val count = holder.getView<TextView>(R.id.gift_show_count)
        holder.getView<TextView>(R.id.gift_show_name).text = item.giftName
        img.glideDisplay(item.giftImage, R.drawable.temp_icon_gift)
        count.text = "x${item.num}"
    }

}