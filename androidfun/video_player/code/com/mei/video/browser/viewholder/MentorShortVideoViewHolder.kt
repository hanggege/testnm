package com.mei.video.browser.viewholder

import android.view.View
import androidx.core.view.updatePadding
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.live.manager.genderAvatar
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.net.model.chick.video.ShortVideoInfo

/**
 *
 * @author Created by lenna on 2020/8/20
 */
class MentorShortVideoViewHolder(itemView: View) : BaseViewHolder(itemView), IShortVideoViewHolder {
    override fun refreshItem(item: Any) {
        getView<RoundImageView>(R.id.short_video_riv).glideDisplay((item as? ShortVideoInfo.VideoInfo)?.videoCoverUrl)
        getView<RoundImageView>(R.id.mentor_avatar_riv)
                .glideDisplay((item as? ShortVideoInfo.VideoInfo)?.user?.avatar
                        , (item as? ShortVideoInfo.VideoInfo)?.user?.gender.genderAvatar())
        setText(R.id.mentor_name_tv, (item as? ShortVideoInfo.VideoInfo)?.user?.nickName)
        itemView.updatePadding(itemView.dip(5), itemView.dip(5), itemView.dip(5), itemView.dip(5))
    }
}