package com.mei.video.browser.adapter

import android.view.ViewGroup
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.getIndexOrNull
import com.mei.video.browser.view.MultiMediaImageView
import com.mei.video.browser.view.MultiMediaVideoView
import com.mei.widget.holder.MeiMultiQuickAdapter

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/23
 */

/** 0:图片，1：视频 **/
data class MultiMediaData(val type: Int, val url: String, val mediaUrl: String = "", val extData: Any? = null)

interface MultiMediaRefresh {
    fun refresh(position: Int, data: MultiMediaData?)
    fun initPosition(initPosition: Int) {}
    fun performSelected(position: Int) {}
}

class MultiMediaAdapter(val list: MutableList<MultiMediaData>)
    : MeiMultiQuickAdapter<MultiMediaData, BaseViewHolder>(list) {

    var initPosition: Int = 0

    override fun getDefItemViewType(position: Int) = list.getIndexOrNull(position)?.type ?: 100

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            1 -> BaseViewHolder(MultiMediaVideoView(parent.context))
            else -> BaseViewHolder(MultiMediaImageView(parent.context))
        }
    }

    override fun convert(holder: BaseViewHolder, item: MultiMediaData) {
        holder.itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        (holder.itemView as? MultiMediaRefresh)?.apply {
            initPosition(initPosition)
            initPosition = -1
            refresh(holder.bindingAdapterPosition, item)
        }
    }
}