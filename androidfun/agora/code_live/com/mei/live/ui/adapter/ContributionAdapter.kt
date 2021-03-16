package com.mei.live.ui.adapter

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.net.model.room.ContributionsInfo

/**
 *  Created by zhanyinghui on 2020/4/9
 *  Des:
 */
class ContributionAdapter(layoutResId: Int, data: MutableList<ContributionsInfo.ContributionBean>) :
        BaseQuickAdapter<ContributionsInfo.ContributionBean, ContributionViewHolder>(layoutResId, data),
        LoadMoreModule {

    override fun convert(holder: ContributionViewHolder, item: ContributionsInfo.ContributionBean) {
        holder.contributorNameView.text = item.userInfo?.nickname
        holder.contributeCountView.text = item.coinCount.toString()
        holder.rankNumberView.text = (data.indexOf(item) + 4).toString()
        holder.itemAvatarView.glideDisplay(item.userInfo?.avatar.orEmpty(), item.userInfo.gender.genderAvatar(item.userInfo.isPublisher))

        if (item.userInfo.userLevel > 0) {
            holder.getView<View>(R.id.level_container).visibility = View.VISIBLE
            if (item.userInfo.userLevel > 9) {
                holder.getView<View>(R.id.level).setPadding(0, 0, context.dip(6), 0)
            } else {
                holder.getView<View>(R.id.level).setPadding(0, 0, context.dip(9), 0)
            }
            val resId = item.userInfo?.userLevel?.let { getBackGroundLevelResource(it, LevelSize.Normal) }
            if (resId != null) {
                holder.setImageResource(R.id.level_image, resId)
            }
            holder.setText(R.id.level, item.userInfo.userLevel.toString())
        } else {
            holder.getView<View>(R.id.level_container).visibility = View.GONE
        }
        holder.getView<TextView>(R.id.contributor_name).paint.isFakeBoldText = true

        holder.setText(R.id.level, item.userInfo?.userLevel.toString())

    }

    fun refresh(list: List<ContributionsInfo.ContributionBean>) {
        this.data.clear()
        this.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    fun add(bean: ContributionsInfo.ContributionBean) {
        data.add(bean)
        notifyDataSetChanged()
    }

    fun addAll(list: List<ContributionsInfo.ContributionBean>) {
        data.addAll(list)
        notifyDataSetChanged()
    }

}

class ContributionViewHolder(view: View) : BaseViewHolder(view) {
    val rankNumberView: TextView = itemView.findViewById(R.id.online_number)
    val contributorNameView: TextView = itemView.findViewById(R.id.contributor_name)
    val contributeCountView: TextView = itemView.findViewById(R.id.contribute_count)
    val itemAvatarView: RoundImageView = itemView.findViewById(R.id.image_avatar_item)

}