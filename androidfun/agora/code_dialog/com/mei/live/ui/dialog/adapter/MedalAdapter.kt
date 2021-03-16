package com.mei.live.ui.dialog.adapter

import android.view.ViewGroup
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.custom.chick.HonorMedal
import com.mei.live.ui.dialog.adapter.item.HonorMedalViewHolder
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R

/**
 *
 * @author Created by lenna on 2020/10/13
 */
class MedalAdapter(list: MutableList<HonorMedal>) : MeiMultiQuickAdapter<HonorMedal, BaseViewHolder>(list) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return HonorMedalViewHolder(parent.layoutInflaterKtToParent(R.layout.item_honor_medal))
    }

    override fun convert(holder: BaseViewHolder, item: HonorMedal) {
        (holder as? HonorMedalViewHolder)?.refreshItem(item)

    }
}