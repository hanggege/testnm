package com.mei.video.browser.viewholder

import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.core.view.updateLayoutParams
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.video.browser.adapter.ShortVideoRefresh
import com.net.model.chick.video.ShortVideoInfo

/**
 *
 * @author Created by lenna on 2020/8/17
 */
class ShortVideoPlayerViewHolder(itemView: View) : BaseViewHolder(itemView) {
    fun refreshItem(initPosition: Int, item: ShortVideoInfo.VideoInfo?) {
        (itemView as? ShortVideoRefresh)?.initPosition(initPosition)
        (itemView as? ShortVideoRefresh)?.refresh(bindingAdapterPosition, item)
    }

}