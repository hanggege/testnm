package com.mei.work.adapter.item

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.mei.live.ext.parseColor
import com.mei.live.manager.genderAvatar
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.holder.addOnClickListener
import com.mei.widget.round.RoundImageView
import com.mei.widget.round.RoundView
import com.mei.wood.R
import com.net.model.chick.friends.UserHomePagerResult

/**
 * WorkRoomAvatarListHolder
 *
 * 工作室中知心人滚动列表
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-25
 */
class WorkRoomAvatarListHolder(itemView: View) : WorkRoomBaseHolder(itemView) {

    private val avatarList: MutableList<UserHomePagerResult.ConsultInfo> = arrayListOf()
    private val avatarListAdapter = AvatarListAdapter(avatarList)

    val recyclerView: RecyclerView by lazy { getView<RecyclerView>(R.id.recycler_view) }

    init {
        avatarListAdapter.setOnItemClickListener { _, _, _ ->
            getView<View>(R.id.item_mask_view).performClick()
        }
        recyclerView.adapter = avatarListAdapter
        GravitySnapHelper(Gravity.START).attachToRecyclerView(recyclerView)
    }

    override fun refresh(item: Any) {
        addOnClickListener(R.id.item_mask_view)
        avatarList.clear()
        if (item is WorkRoomAvatarListData) {
            item.list.forEach { avatarList.add(it) }
        }
        avatarListAdapter.notifyDataSetChanged()
    }

    inner class AvatarListAdapter(avatarList: MutableList<UserHomePagerResult.ConsultInfo>) : BaseQuickAdapter<UserHomePagerResult.ConsultInfo, BaseViewHolder>(R.layout.item_work_room_main_avatar_circle, avatarList) {
        override fun convert(holder: BaseViewHolder, item: UserHomePagerResult.ConsultInfo) {
            holder.getView<RoundImageView>(R.id.room_main_circle_avatar).glideDisplay(item.avatar.orEmpty(), item.gender.genderAvatar())
            holder.getView<RoundView>(R.id.room_main_circle_avatar_bg).setBackColor(item.backgroundColor.parseColor(Color.WHITE))
            holder.getView<TextView>(R.id.room_main_circle_avatar_text).text = item.nickname
        }
    }
}

class WorkRoomAvatarListData(val list: List<UserHomePagerResult.ConsultInfo>)