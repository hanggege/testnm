package com.mei.work.adapter

import android.view.ViewGroup
import android.widget.Space
import com.joker.im.custom.chick.CourseInfo
import com.joker.im.custom.chick.ServiceInfo
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.mei.work.adapter.ItemType.*
import com.mei.work.adapter.item.*
import com.net.model.chick.friends.UserHomePagerResult

/**
 * WorkRoomUserAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-24
 */
enum class ItemType {
    HEADER, // 工作室封面+直播标签
    ROOM_SUMMARY, // 工作室主页的基本信息
    TITLE, // 标题
    SQUARE_AVATAR, // 圆角方图头像
    AVATAR_LIST, // 原图的头像横向列表
    LIVE_INFO, // 直播的一些信息
    PERSONAL_SERVICE, // 专属服务
    COURSE, // 课程
    GIFT, // 礼物Item
    FOOTER, // 列表尾部item
    SPACE // 未识别的空视图
}

class WorkRoomAdapter(list: MutableList<Any>) : MeiMultiQuickAdapter<Any, WorkRoomBaseHolder>(list) {

    private val roomTypeArr by lazy { values() }

    init {
        setGridSpanSizeLookup { _, viewType, _ ->
            when (roomTypeArr[viewType]) {
                SQUARE_AVATAR -> 3
                GIFT -> 4
                else -> 12
            }
        }
    }

    override fun getDefItemViewType(position: Int) = when (getItemOrNull(position)) {
        is WorkRoomHeaderData -> HEADER
        is UserHomePagerResult.InfoBean -> ROOM_SUMMARY
        is WorkRoomTitleData -> TITLE
        is WorkRoomAvatarData -> SQUARE_AVATAR
        is WorkRoomAvatarListData -> AVATAR_LIST
        is ServiceInfo -> PERSONAL_SERVICE
        is CourseInfo -> COURSE
        is WorkRoomLiveInfo -> LIVE_INFO
        is WorkRoomGiftData -> GIFT
        is Footer -> FOOTER
        else -> SPACE
    }.ordinal

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): WorkRoomBaseHolder {
        return when (roomTypeArr[viewType]) {
            HEADER -> WorkRoomHeaderHolder(parent.layoutInflaterKtToParent(R.layout.item_work_room_main_header))
            ROOM_SUMMARY -> WorkRoomSummaryHolder(parent.layoutInflaterKtToParent(R.layout.item_work_room_main_summary))
            TITLE -> WorkRoomTitleHolder(parent.layoutInflaterKtToParent(R.layout.item_work_room_main_title))
            SQUARE_AVATAR -> WorkRoomAvatarHolder(parent.layoutInflaterKtToParent(R.layout.item_work_room_main_avatar_square))
            AVATAR_LIST -> WorkRoomAvatarListHolder(parent.layoutInflaterKtToParent(R.layout.item_work_room_main_avatar_list))
            LIVE_INFO -> WorkRoomLiveInfoHolder(parent.layoutInflaterKtToParent(R.layout.item_work_room_main_live_info))
            PERSONAL_SERVICE -> WorkRoomServiceHolder(parent.layoutInflaterKtToParent(R.layout.item_work_room_main_personal_service))
            COURSE -> WorkRoomCourseHolder(parent.layoutInflaterKtToParent(R.layout.item_work_room_main_course))
            GIFT -> WorkRoomGiftHolder(parent.layoutInflaterKtToParent(R.layout.item_work_room_main_gift))
            FOOTER -> WorkRoomBaseHolder(parent.layoutInflaterKtToParent(R.layout.item_work_room_main_footer))
            else -> WorkRoomBaseHolder(Space(parent.context).apply { layoutParams = ViewGroup.LayoutParams(1, 1) })
        }
    }

    override fun getItemCount(): Int = data.size

    override fun convert(holder: WorkRoomBaseHolder, item: Any) {
        holder.refresh(item)
    }
}
