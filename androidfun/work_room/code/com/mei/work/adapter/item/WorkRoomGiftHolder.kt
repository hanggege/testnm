package com.mei.work.adapter.item

import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.net.model.chick.friends.UserHomePagerResult
import kotlinx.android.synthetic.main.layout_special_studio_top_container.view.*

/**
 * WorkRoomGiftHolder
 *
 * 工作室收到礼物信息
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-27
 */
class WorkRoomGiftHolder(view: View) : WorkRoomBaseHolder(view) {

    override fun refresh(item: Any) {
        if (item is WorkRoomGiftData) {
            val data = item.data
            getView<ImageView>(R.id.room_main_gift_img).glideDisplay(data.giftImage)
            setText(R.id.room_main_gift_count, data.num)
            setText(R.id.room_main_gift_name, data.giftName)
        }
    }
}

class WorkRoomGiftData(val data: UserHomePagerResult.ReceiveGiftBean, val position: GiftPosition)

enum class GiftPosition {
    LEFT, MID, RIGHT
}