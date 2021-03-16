package com.mei.work.adapter.item

import android.view.View
import androidx.core.view.isGone
import com.flyco.roundview.RoundTextView
import com.mei.live.views.ExpandableTextView.CardExpandableTextView
import com.mei.live.views.ExpandableTextView.ExpandableStatusFix
import com.mei.live.views.ExpandableTextView.StatusType
import com.mei.wood.R
import com.net.model.chick.friends.UserHomePagerResult
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout

/**
 * WorkRoomSummaryHolder
 *
 * 工作室介绍
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-24
 */
class WorkRoomSummaryHolder(view: View) : WorkRoomBaseHolder(view) {
    var statusType = StatusType.STATUS_EXPAND

    init {
        getView<CardExpandableTextView>(R.id.room_main_summary_expand_text).apply {
            bind(object : ExpandableStatusFix {
                override fun getStatus(): StatusType {
                    return statusType
                }

                override fun setStatus(status: StatusType) {
                    statusType = status
                }

            })
            setExpandOrContractClickListener { }
        }
    }

    override fun refresh(item: Any) {
        if (item is UserHomePagerResult.InfoBean) {
            getView<CardExpandableTextView>(R.id.room_main_summary_expand_text).apply {
                content = item.introduction
                isGone = item.introduction.isNullOrEmpty()
            }
            val list = if (item.publisherTags.orEmpty().isNotEmpty()) item.publisherTags.orEmpty() else item.skills.orEmpty().mapNotNull { it.name }
            getView<TagFlowLayout>(R.id.room_main_summary_info_tag).adapter = getTagAdapter(list)
        }
    }

    private fun getTagAdapter(info: List<String>): TagAdapter<String> = object : TagAdapter<String>(info) {

        override fun getView(parent: FlowLayout, position: Int, t: String): View {
            val tagView = View.inflate(itemView.context, R.layout.item_user_skills_tag, null) as RoundTextView
            return tagView.also { it.text = t }
        }
    }
}