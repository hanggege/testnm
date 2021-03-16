package com.mei.video.browser.adapter

import android.view.ViewGroup
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.layoutInflaterKt
import com.mei.video.browser.adapter.MentorShortVideItemType.TYPE_EMPTY
import com.mei.video.browser.adapter.MentorShortVideItemType.TYPE_TAG
import com.mei.video.browser.adapter.MentorShortVideItemType.TYPE_TITLE
import com.mei.video.browser.adapter.MentorShortVideItemType.TYPE_VIDEO
import com.mei.video.browser.viewholder.*
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.chick.video.ShortVideoInfo

/**
 *
 * @author Created by lenna on 2020/8/20
 */
object MentorShortVideItemType {
    const val TYPE_TITLE = 0
    const val TYPE_TAG = 1
    const val TYPE_VIDEO = 2
    const val TYPE_EMPTY = 3
}

class MentorShortVideoAdapter(list: MutableList<Any>)
    : MeiMultiQuickAdapter<Any, BaseViewHolder>(list), LoadMoreModule {

    init {
        setGridSpanSizeLookup { _, viewType, _ ->
            when (viewType) {
                TYPE_VIDEO -> 3
                else -> 6
            }
        }
    }

    override fun getDefItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ArrayList<*> -> TYPE_TAG
            is ShortVideoInfo.VideoInfo -> TYPE_VIDEO
            is String -> TYPE_TITLE
            is Int -> TYPE_EMPTY
            else -> -1
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_TITLE -> MentorShortVideoTitleViewHolder(parent.layoutInflaterKt(R.layout.item_mentor_short_video_title))
            TYPE_TAG -> MentorShortVideoTagViewHolder(parent.layoutInflaterKt(R.layout.item_mentor_short_video_tag))
            TYPE_VIDEO -> MentorShortVideoViewHolder(parent.layoutInflaterKt(R.layout.item_mentor_short_video))
            else -> {
                MentorShortVideoEmptyViewHolder(parent.layoutInflaterKt(R.layout.item_mentor_short_video_empty))
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: Any) {
        (holder as? IShortVideoViewHolder)?.refreshItem(item)
    }
}