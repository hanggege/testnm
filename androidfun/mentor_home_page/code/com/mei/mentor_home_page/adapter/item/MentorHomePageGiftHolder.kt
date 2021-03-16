package com.mei.mentor_home_page.adapter.item

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mei.base.util.shadow.setListShadowDefault
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.net.model.chick.friends.UserHomePagerResult

/**
 *
 * @author Created by lenna on 2020/5/21
 * 导师礼物
 */
class MentorHomePageGiftHolder(var view: View) : MentorBaseHolder(view) {
    override fun refresh(item: Any) {
        view.apply { setListShadowDefault(this) }
        if (item is UserHomePagerResult.ReceiveGiftBean) {
            val img = getView<ImageView>(R.id.gift_show_img)
            val count = getView<TextView>(R.id.gift_show_count)
            getView<TextView>(R.id.gift_show_name).text = item.giftName
            img.glideDisplay(item.giftImage, R.drawable.temp_icon_gift)
            val giftCount = "x${item.num ?: ""}"
            count.text = giftCount
        }

    }


}