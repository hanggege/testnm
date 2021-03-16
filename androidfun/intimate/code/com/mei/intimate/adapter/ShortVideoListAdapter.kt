package com.mei.intimate.adapter

import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.intimate.view.IntimateShortVideoView
import com.mei.wood.R
import com.net.model.chick.room.RoomList.ShortVideoItemBean

/**
 * Created by hang on 2020/7/14.
 */
class ShortVideoListAdapter(list: MutableList<ShortVideoItemBean>) :
        BaseQuickAdapter<ShortVideoItemBean, BaseViewHolder>(R.layout.view_intimate_short_video, list), LoadMoreModule {

    var tagId = 0
    var statFromType: String = "广场"

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(IntimateShortVideoView(parent.context))
    }

    override fun convert(holder: BaseViewHolder, item: ShortVideoItemBean) {
        (holder.itemView as? IntimateShortVideoView)?.apply {
            currTagId = tagId
            convertData(item)
            fromType = statFromType
            currentPosition = getItemPosition(item)
        }
    }
}
