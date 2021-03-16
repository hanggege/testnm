package com.mei.work.adapter.item

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.joker.im.custom.chick.CourseInfo
import com.mei.course_service.ui.MeiWebCourseServiceActivityLauncher
import com.mei.live.ext.createSplitSpannable
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.widget.holder.activity
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MeiWebData
import com.mei.work.ui.getCurWorkRoomLivingRoomId

/**
 * WorkRoomCourseHolder
 *
 * 工作室课程信息
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-27
 */
class WorkRoomCourseHolder(view: View) : WorkRoomBaseHolder(view) {

    private var info = CourseInfo()

    init {
        getView<View>(R.id.room_main_course_left_container).setOnClickNotDoubleListener(tag = "work_room_specific") {
            turn2SpecificList()
        }
        getView<View>(R.id.room_main_course_bought).setOnClickNotDoubleListener(tag = "work_room_specific") {
            turn2SpecificList()
        }
        getView<View>(R.id.room_main_course_free_listen).setOnClickNotDoubleListener(tag = "free_listen") {
            turn2SpecificList(true)
        }
    }

    override fun refresh(item: Any) {
        if (item is CourseInfo) {
            info = item
            setText(R.id.room_main_course_name_tv, info.courseName)

            val price = info.priceText.createSplitSpannable(Color.parseColor("#333333"))
            setText(R.id.room_main_course_price_tv, price)

            if (item.isGroupMember) {
                getView<LinearLayout>(R.id.room_main_course_free_listen).isVisible = false
                getView<TextView>(R.id.room_main_course_bought).isVisible = false
                setWholeItemClick(true)
                return
            }

            // 以下条件不显示课程入口 ，1，已经购买，2：没有免费音频 3:自己是工作室成员
            getView<LinearLayout>(R.id.room_main_course_free_listen).isGone = info.hasBuy || info.hasFreeAudition.not() || info.isGroupMember
            getView<TextView>(R.id.room_main_course_bought).isVisible = info.hasBuy

            setWholeItemClick(getView<LinearLayout>(R.id.room_main_course_free_listen).isGone && getView<TextView>(R.id.room_main_course_bought).isGone)
        }
    }

    private fun setWholeItemClick(clickable: Boolean) {
        if (clickable) {
            itemView.setOnClickNotDoubleListener(tag = "work_room_specific") { turn2SpecificList() }
        } else {
            itemView.setOnClickNotDoubleListener {}
        }
    }

    private fun turn2SpecificList(freePlay: Boolean = false) {
        val freePlayValue = if (freePlay) 1 else 0
        MeiWebCourseServiceActivityLauncher.startActivity(activity,
                MeiWebData(MeiUtil.appendParamsUrl(AmanLink.NetUrl.course_introduce,
                        "status_bar_height" to "${getStatusBarHeight()}",
                        "course_id" to "${info.courseId}",
                        "from" to "self_page",
                        "room_id" to getCurWorkRoomLivingRoomId(info.roomId, info.groupId),
                        "free_play" to "$freePlayValue"),
                        0))
    }
}
