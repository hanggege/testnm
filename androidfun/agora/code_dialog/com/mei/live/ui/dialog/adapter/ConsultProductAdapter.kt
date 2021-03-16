package com.mei.live.ui.dialog.adapter

import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.orc.ext.dp2px
import com.mei.widget.round.RoundTextView
import com.mei.wood.R
import com.net.model.room.ProductCategory

/**
 * ProductAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-10-14
 */
class ProductAdapter(list: MutableList<ProductCategory.ProductCategoryBean>) : BaseQuickAdapter<ProductCategory.ProductCategoryBean, BaseViewHolder>(R.layout.item_consult_product_label, list) {

    var selectedPos = -1

    override fun convert(holder: BaseViewHolder, item: ProductCategory.ProductCategoryBean) {
        val isSelected = selectedPos == holder.layoutPosition
        holder.itemView.isSelected = isSelected
        holder.getView<RoundTextView>(R.id.consult_product_label).apply {
            paint.isFakeBoldText = isSelected
            text = item.pro_cate_name
            radius = 15.dp
            if (isSelected) {
                strokeWidth = 0.25f.dp2px
                setStrokeColor(ContextCompat.getColor(context, R.color.color_FF3A3A))
                setBackColor(ContextCompat.getColor(context, R.color.color_FFF0F0))
                setTextColor(ContextCompat.getColor(context, R.color.color_FF3A3A))
            } else {
                strokeWidth = 0f
                setStrokeColor(Color.TRANSPARENT)
                setBackColor(ContextCompat.getColor(context, R.color.color_f8f8f8))
                setTextColor(ContextCompat.getColor(context, R.color.color_333333))
            }
        }
    }

}

class QuickConsultItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter
        if (adapter !is ProductAdapter) return
        val position = (view.layoutParams as? RecyclerView.LayoutParams)?.viewLayoutPosition ?: -1
        when (position % 3) {
            0 -> {
                outRect.left = view.dip(10)
                outRect.right = view.dip(3.33f)
            }
            2 -> {
                outRect.left = view.dip(3.33f)
                outRect.right = view.dip(10f)
            }
            1 -> {
                outRect.left = view.dip(6.67f)
                outRect.right = view.dip(6.67f)
            }
        }
        if (position > 2) {
            outRect.top = view.dip(10)
        }
    }
}