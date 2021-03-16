package com.mei.me.adapter

import android.graphics.Rect
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.weight.custom.SpreadAvatarView
import com.mei.live.manager.genderAvatar
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.net.model.chick.friends.MyFollowListBean

/**
 * author : Song Jian
 * date   : 2020/2/26
 * desc   : 直播列表
 */
class MeFollowListAdapter(list: MutableList<MyFollowListBean>) :
        BaseQuickAdapter<MyFollowListBean, BaseViewHolder>(R.layout.item_message_header, list) {
    override fun convert(holder: BaseViewHolder, item: MyFollowListBean) {
        holder.setText(R.id.message_header_item_header_living, if (item.isGroup) "工作室直播中" else "直播中")

        holder.setVisible(R.id.message_header_item_online, item.online)

        val spreadAvatarView = holder.getView<SpreadAvatarView>(R.id.spread_avatar_view)
        if (item.userId == -99 || item.roomInfo?.roomId.isNullOrEmpty()) {
            spreadAvatarView.cancelAnim()

            holder.setVisible(R.id.shimmer_frame, false)
            holder.setVisible(R.id.message_header_item_header_living_bg, false)

            if (item.userId == -99) {//更多
                holder.getView<ImageView>(R.id.message_header_item_header).glideDisplay(R.drawable.icon_more)
                holder.setText(R.id.message_header_item_header_name, "更多")
            } else { //未相亲
                holder.getView<ImageView>(R.id.message_header_item_header).glideDisplay(item.avatar, item.gender.genderAvatar())
                holder.setText(R.id.message_header_item_header_name, item.nickname)
            }

        } else { //相亲中
            spreadAvatarView.startAnim(1200L)

            holder.setVisible(R.id.shimmer_frame, true)
            holder.setVisible(R.id.message_header_item_header_living_bg, true)


            holder.getView<RoundImageView>(R.id.message_header_item_header).glideDisplay(item.avatar.orEmpty(), item.gender.genderAvatar())
            holder.setText(R.id.message_header_item_header_name, item.nickname)
        }

    }
}

class MeFollowItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = (view.layoutParams as? RecyclerView.LayoutParams)?.viewLayoutPosition ?: -1
        if (position != 0) {
            outRect.left = view.dip(8)
        }
    }
}