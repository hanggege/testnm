package com.mei.video.browser.adapter

import android.util.SparseBooleanArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.screenHeight
import com.mei.orc.util.string.getInt
import com.mei.video.browser.view.ShortVideoView
import com.mei.video.browser.viewholder.ShortVideoPlayerViewHolder
import com.net.model.chick.video.ShortVideoInfo

/**
 *
 * @author Created by lenna on 2020/8/17
 */

interface ShortVideoRefresh {
    fun refresh(position: Int, data: ShortVideoInfo.VideoInfo?)
    fun initPosition(initPosition: Int) {}
    fun performSelected(position: Int) {}
    fun pauseVideo() {}
    fun playVideo() {}
}

class ShortVideoPlayerAdapter(val list: MutableList<ShortVideoInfo.VideoInfo>)
    : RecyclerView.Adapter<BaseViewHolder>() {
    var initPosition: Int = 0
    var preHeight: Int = screenHeight
    var praiseArray: SparseBooleanArray? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ShortVideoPlayerViewHolder(ShortVideoView(parent.context))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        updateItem(holder, list[position % list.size])
    }

    private fun updateItem(holder: BaseViewHolder, item: ShortVideoInfo.VideoInfo) {
        val index = praiseArray?.indexOfKey(item.seqId.getInt()) ?: -1
        if (index > -1) {
            item.like = praiseArray?.get(item.seqId.getInt()) ?: false
        }
        (holder as? ShortVideoPlayerViewHolder)?.refreshItem(initPosition, item)
        initPosition = -1
        holder.itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, preHeight)
    }

}