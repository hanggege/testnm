package com.mei.live.ui.dialog.adapter.item

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.live.ext.parseColor
import com.mei.orc.Cxt
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.net.model.room.RoomWeekRankList

/**
 *
 * @author Created by lenna on 2020/9/9
 */
class WeekRankListViewHolder(itemView: View) : BaseViewHolder(itemView) {
    @SuppressLint("SetTextI18n")
    fun refreshItem(data: RoomWeekRankList.WeekRank?) {
        getView<TextView>(R.id.list_ranking_number_tv).apply {
            text = "${data?.no}"
            setTextColor(data?.userInfo?.color.orEmpty().parseColor(Cxt.getColor(R.color.color_D8D8D8)))
        }
        getView<ImageView>(R.id.list_week_rank_crown_iv)
                .apply {
                    glideDisplay(data?.userInfo?.showImage.orEmpty())
                    isVisible = data?.userInfo?.showImage?.isNotEmpty() == true
                }
        getView<RoundImageView>(R.id.list_week_rank_avatar_riv).apply {
            glideDisplay(data?.userInfo?.avatar.orEmpty())
            viewStrokeColor = data?.userInfo?.color.orEmpty().parseColor(Cxt.getColor(android.R.color.white))
            viewStrokeWidth = if (data?.userInfo?.color?.isNotEmpty() == true) dip(2) * 1f else dip(0) * 1f
        }
        setText(R.id.list_week_rank_name_tv, data?.userInfo?.nickname.orEmpty())
        getView<TextView>(R.id.list_week_rank_name_tv).paint.isFakeBoldText = true
        setText(R.id.list_week_rank_receive_crown_tv, data?.coinCount.orEmpty())
        getView<ImageView>(R.id.list_week_rank_status_iv).apply {
            glideDisplay(when (data?.userInfo?.onlineStatus) {
                //在线
                2 -> {
                    context.getDrawable(R.drawable.icon_online_bg)
                }
                //直播
                1 -> {
                    context.getDrawable(R.drawable.icon_playing_status)
                }
                else -> {
                }
            })
            updateLayoutParams {
                when (data?.userInfo?.onlineStatus) {
                    1 -> {
                        width = dip(15)
                        height = dip(15)
                    }
                    2 -> {
                        width = dip(11)
                        height = dip(11)
                    }
                }
            }
            isVisible = data?.userInfo?.onlineStatus != 0
        }

    }
}