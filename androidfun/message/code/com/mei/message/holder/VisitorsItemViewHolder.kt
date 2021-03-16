package com.mei.message.holder

import android.view.View
import androidx.core.view.isGone
import com.flyco.roundview.RoundTextView
import com.mei.live.ext.createSplitSpannable
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.net.model.chick.message.VisitorsMessage
import com.net.model.chick.tab.tabbarConfig
import com.net.model.user.UserInfo
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout

/**
 *
 * @author Created by lenna on 2020/6/15
 * 访问记录显示的viewHolder
 */
class VisitorsItemViewHolder(itemView: View) : VisitorsBaseViewHolder(itemView) {
    override fun refreshItem(data: Any?, isLastPosition: Boolean) {
        if (data is VisitorsMessage.Visitors) {
            val userInfo: UserInfo? = data.info
            userInfo?.apply {
                getView<RoundImageView>(R.id.visitors_avatar_iv).glideDisplay(userInfo.avatar.orEmpty(), userInfo.gender.genderAvatar(userInfo.isPublisher))
                setText(R.id.service_icon_iv, tabbarConfig.diamondEmoji.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_333333)))
                setGone(R.id.service_icon_iv, !specialFlag)
                setGone(R.id.connect_icon_iv, !upFlag)
                setText(R.id.visitors_name_tv, nickname)
                val resId: Int? = getBackGroundLevelResource(userLevel, LevelSize.Normal)
                if (resId != null) {
                    setImageResource(R.id.level_image, resId)
                }
                setText(R.id.level, "$userLevel")
                setGone(R.id.level_container, userLevel == 0)
                setGone(R.id.visitors_online_iv, !online)
                setGone(R.id.is_publisher_iv, !isPublisher)
            }
            data.apply {
                if (tags != null && tags.isNotEmpty()) {
                    getView<TagFlowLayout>(R.id.visitors_tag).adapter = getTagAdapter(tags)
                } else {
                    getView<TagFlowLayout>(R.id.visitors_tag).isGone = true
                }
                setText(R.id.visitors_time_tv, timeStr ?: "")
                setText(R.id.title_time_tv, titleTimeStr ?: "")
                setGone(R.id.title_time_tv, !isVisibleTitle)
                if (!isLastPosition) {
                    setGone(R.id.visitors_line_v, !isVisibleLine)
                } else {
                    setGone(R.id.visitors_line_v, true)
                }
            }

        }
    }

    /**获取显示标签*/
    private fun getTagAdapter(info: List<String>): TagAdapter<String> {
        return object : TagAdapter<String>(info) {
            override fun getView(parent: FlowLayout?, position: Int, t: String): RoundTextView {
                val tagView = View.inflate(itemView.context
                        , R.layout.item_user_visitors_tag, null) as RoundTextView
                return tagView.also {
                    it.text = t
                    if (position == 0) {
                        it.setBackgroundColor(Cxt.getColor(R.color.color_fcf4f2))
                        it.setTextColor(Cxt.getColor(R.color.color_f38765))
                    } else {
                        it.setBackgroundColor(Cxt.getColor(R.color.color_f3f3f3))
                        it.setTextColor(Cxt.getColor(R.color.color_666666))
                    }
                }
            }
        }

    }
}