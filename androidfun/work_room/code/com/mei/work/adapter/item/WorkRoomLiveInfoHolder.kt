package com.mei.work.adapter.item

import android.view.View
import android.widget.TextView
import com.mei.wood.R

/**
 * WorkRoomLiveInfoHolder
 *
 * 工作室直播情况
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-27
 */
class WorkRoomLiveInfoHolder(view: View) : WorkRoomBaseHolder(view) {

    override fun refresh(item: Any) {
        if (item is WorkRoomLiveInfo) {
            setText(R.id.room_main_live_left_count,"${item.coin}")
            setText(R.id.room_main_live_center_count, "${item.fansCount}")
            setText(R.id.room_main_live_right_count, item.time)
        }
    }
}

class WorkRoomLiveInfo(val coin: Int = 0, val fansCount: Int = 0, val time: String = "")