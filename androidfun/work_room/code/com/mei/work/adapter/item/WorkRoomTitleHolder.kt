package com.mei.work.adapter.item

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.widget.holder.activity
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MeiWebActivityLauncher
import com.mei.wood.web.MeiWebData
import com.mei.work.ui.getCurWorkRoomLivingRoomId

/**
 * WorkRoomTitleHolder
 *
 * 工作室的各个标题
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-24
 */
class WorkRoomTitleHolder(view: View) : WorkRoomBaseHolder(view) {

    private var info = WorkRoomTitleData()

    init {
        view.setOnClickNotDoubleListener {
            when (info.title) {
                "专属服务" -> MeiWebActivityLauncher.startActivity(activity, MeiWebData(
                        MeiUtil.appendParamsUrl(AmanLink.NetUrl.work_room_service,
                                "group_id" to "${info.groupId}",
                                "room_id" to getCurWorkRoomLivingRoomId(info.roomId, info.groupId)), 0).apply {
                    need_reload_web = 1
                })
                "课程" -> MeiWebActivityLauncher.startActivity(activity, MeiWebData(
                        MeiUtil.appendParamsUrl(AmanLink.NetUrl.work_room_course,
                                "group_id" to "${info.groupId}",
                                "publisherId" to "${info.publisherId}",
                                "room_id" to getCurWorkRoomLivingRoomId(info.roomId, info.groupId)), 0).apply {
                    need_reload_web = 1
                })
                else -> {
                }
            }
        }
    }

    override fun refresh(item: Any) {
        if (item is WorkRoomTitleData) {
            info = item
            getView<View>(R.id.room_main_title_line).isVisible = info.hasInterval
            getView<TextView>(R.id.room_main_title_show_all).isVisible = info.hasShowAll
            val text = if (info.size != 0) {
                "${info.title} (${info.size}人) "
            } else {
                info.title
            }
            setText(R.id.room_main_title_text, text)
        }
    }
}

class WorkRoomTitleData(val hasInterval: Boolean = false, val hasShowAll: Boolean = false) {
    var title: String = ""
    var size: Int = 0
    var groupId: Int = 0
    var publisherId: Int = 0
    var roomId: String? = null
}