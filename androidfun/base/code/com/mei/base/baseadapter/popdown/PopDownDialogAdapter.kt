package com.mei.base.baseadapter.popdown

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.callback.Callback
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenWidth
import com.mei.wood.R

class PopDownDialogAdapter(selectedIndex: Int, list: MutableList<String>, var callback: Callback<Int>)
    : BaseQuickAdapter<String, BaseViewHolder>(R.layout.pop_title_grid_item, list) {

    private var newSelectedIndex = selectedIndex

    override fun convert(holder: BaseViewHolder, item: String) {
        val itemTitle = holder.getView<TextView>(R.id.item_title)
        itemTitle.isSelected = holder.layoutPosition == newSelectedIndex
        itemTitle.text = item
        val horizontalOffset = holder.itemView.dip(15f)
        val screenWidth = screenWidth
        val normalWidth = (screenWidth - horizontalOffset * 5) / 4

        if (item.length < 5) {
            itemTitle.width = normalWidth
        } else {
            itemTitle.width = (itemTitle.paint.measureText(item) + itemTitle.dip(10f) * 2).toInt()
        }
        itemTitle.setOnClickListener {
            newSelectedIndex = holder.layoutPosition
            notifyDataSetChanged()
            callback.onCallback(newSelectedIndex)
        }
    }
}