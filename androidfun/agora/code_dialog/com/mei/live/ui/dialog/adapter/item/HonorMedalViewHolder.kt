package com.mei.live.ui.dialog.adapter.item

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.custom.chick.HonorMedal
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R

/**
 *
 * @author Created by lenna on 2020/10/13
 */
class HonorMedalViewHolder(itemView: View) : BaseViewHolder(itemView) {
    fun refreshItem(honorMedal: HonorMedal?) {
        getView<ImageView>(R.id.medal_image_iv).glideDisplay(honorMedal?.shellImg.orEmpty())
        getView<ImageView>(R.id.medal_inner_image_iv).glideDisplay(honorMedal?.innerImg.orEmpty())
        setText(R.id.medal_name_tv, honorMedal?.title.orEmpty())
    }

}