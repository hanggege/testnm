package com.mei.live.ui.dialog.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.custom.chick.EntryItemBean
import com.mei.wood.R

/**
 * QuickConsultAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-10-21
 */
class QuickConsultAdapter(list: MutableList<EntryItemBean>) : BaseQuickAdapter<EntryItemBean, BaseViewHolder>(R.layout.item_quick_consult, list) {
    override fun convert(holder: BaseViewHolder, item: EntryItemBean) {
        holder.setText(R.id.quick_consult_desc, item.desc)
                .getView<TextView>(R.id.quick_consult_valStr).apply {
                    paint.isFakeBoldText = true
                    text = item.valStr
                }
    }
}