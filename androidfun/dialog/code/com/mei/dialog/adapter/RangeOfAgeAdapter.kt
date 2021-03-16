package com.mei.dialog.adapter

import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.widget.gradient.GradientTextView
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R

/**
 * RangeOfAgeAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-18
 */
class RangeOfAgeAdapter(var list: MutableList<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_range_of_age, list) {

    var selectedPos = 0

    override fun convert(holder: BaseViewHolder, item: String) {
        val isSelected = selectedPos == holder.layoutPosition
        holder.itemView.isSelected = isSelected
        val textView = holder.getView<GradientTextView>(R.id.item_age_text)
        textView.setTextColor(ContextCompat.getColor(context, if (isSelected) android.R.color.white else R.color.color_11121C))
        textView.delegate.applyViewDelegate {
            radius = 5.dp
            if (isSelected) {
                startColor = ContextCompat.getColor(context, R.color.color_FF7F33)
                endColor = ContextCompat.getColor(context, R.color.color_FF3F36)
            } else {
                startColor = ContextCompat.getColor(context, R.color.color_f6f6f6)
            }
        }
        textView.text = item
    }
}

class RangeOfAgeItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter
        if (adapter !is RangeOfAgeAdapter) return
        val mData = adapter.data
        val position = (view.layoutParams as? RecyclerView.LayoutParams)?.viewLayoutPosition ?: -1
        val marginValue = view.dip(15)
        outRect.top = marginValue
        when (position % 3) {
            0 -> {
                (view as FrameLayout).findViewById<TextView>(R.id.item_age_text).updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.END }
            }
            2 -> {
                (view as FrameLayout).findViewById<TextView>(R.id.item_age_text).updateLayoutParams<FrameLayout.LayoutParams> { gravity = Gravity.START }
            }
        }
        if (mData.size - 1 - position < 3) { // 最后一行
            outRect.bottom = marginValue
        }
    }
}