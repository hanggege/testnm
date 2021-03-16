package com.mei.me.adapter

import android.view.View
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.chat.toImChat
import com.mei.live.LiveEnterType
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.net.MeiUser
import com.net.model.chick.friends.SubListBean

/**
 * author : Song Jian
 * date   : 2020/1/18
 * desc   : 我的关注列表
 */
class MyFollowListAdapter(val list: ArrayList<SubListBean>) :
        BaseQuickAdapter<SubListBean, BaseViewHolder>(R.layout.item_my_follow_list, list),
        LoadMoreModule {


    override fun convert(holder: BaseViewHolder, item: SubListBean) {
        holder.setText(R.id.my_follow_list_name, item.nickname)
        //头像
        holder.getView<RoundImageView>(R.id.my_follow_list_avatar).glideDisplay(item.avatar.orEmpty(), item.gender.genderAvatar(item.isPublisher))

        holder.setText(R.id.my_follow_list_live_status, if (item.groupInfo != null) "工作室直播中" else "直播中")

        //直播状态
        holder.getView<View>(R.id.my_follow_list_live_status_v).apply {
            isVisible = item.isLiving
            setOnClickListener {
                context?.let {
                    VideoLiveRoomActivityLauncher.startActivity(it, item.roomId, LiveEnterType.follow_list)
                }
            }
        }
        holder.getView<View>(R.id.goto_im_tv).apply {
            isVisible = MeiUser.getSharedUser().info?.isPublisher == false
            setOnClickListener { context.toImChat(item.userId.toString()) }
        }
    }

}