package com.mei.video.browser.viewholder

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.ui.nav.Nav
import com.mei.wood.R
import com.net.model.chick.video.MentorShortVideo
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout

/**
 *
 * @author Created by lenna on 2020/8/20
 */
class MentorShortVideoTagViewHolder(itemView: View) : BaseViewHolder(itemView), IShortVideoViewHolder {
    override fun refreshItem(item: Any) {

        if (item is List<*>) {
            val list: List<MentorShortVideo.Tag>? = item.mapNotNull { (it as? MentorShortVideo.Tag) }
            getView<TagFlowLayout>(R.id.short_video_tag_tfl).adapter = getTagAdapter(list)
            getView<TagFlowLayout>(R.id.short_video_tag_tfl).setOnTagClickListener { _, position, _ ->
                Nav.toAmanLink(itemView.context, list?.let { it[position].action })

            }
        }

    }

    private fun getTagAdapter(info: List<MentorShortVideo.Tag>?): TagAdapter<MentorShortVideo.Tag> {
        return object : TagAdapter<MentorShortVideo.Tag>(info) {
            override fun getView(parent: FlowLayout?, position: Int, t: MentorShortVideo.Tag): TextView {
                val tagView = View.inflate(itemView.context, R.layout.item_short_video_tag, null) as TextView
                return tagView.also {
                    it.text = t.name
                }

            }
        }

    }
}