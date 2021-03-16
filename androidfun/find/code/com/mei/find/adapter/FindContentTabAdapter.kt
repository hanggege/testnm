package com.mei.find.adapter

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.gradient.GradientConstraintLayout
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.net.model.chick.find.FindCourseTab


/**
 * FindContentTabAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-05
 */
class FindContentTabAdapter(var list: MutableList<FindCourseTab.TabInfo>) : BaseQuickAdapter<FindCourseTab.TabInfo, BaseViewHolder>(R.layout.item_find_content_tab, list) {

    var selectedPos = 0

    var percent: Float = 0f

    override fun convert(holder: BaseViewHolder, item: FindCourseTab.TabInfo) {
        val isSelected = selectedPos == holder.layoutPosition
        holder.itemView.isSelected = isSelected
        holder.getView<GradientConstraintLayout>(R.id.item_find_content_tab_layout).delegate.applyViewDelegate {
            if (isSelected) {
                startColor = ContextCompat.getColor(context, R.color.color_FC5143)
                centerColor = ContextCompat.getColor(context, R.color.color_FF4030)
                endColor = ContextCompat.getColor(context, R.color.color_FF973B)
            } else {
                startColor = ContextCompat.getColor(context, android.R.color.white)
            }
            radius = 5.dp
        }
        holder.getView<TextView>(R.id.item_find_content_tab_text).apply {
            text = item.partitionName
            setTextColor(ContextCompat.getColor(context, if (isSelected) android.R.color.white else R.color.color_262626))
        }
        notifyItem(holder)
        holder.getView<ImageView>(R.id.item_find_content_tab_img).glideDisplay(if (isSelected) item.backgroundImageHover else item.backgroundImage)
    }

    override fun convert(holder: BaseViewHolder, item: FindCourseTab.TabInfo, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        convert(holder, item)

        notifyItem(holder)
    }

    private fun notifyItem(holder: BaseViewHolder) {
        holder.itemView.updateLayoutParams { height = (context.dip(55) - percent * context.dip(14)).toInt() }
        holder.getView<TextView>(R.id.item_find_content_tab_text).updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = (context.dip(5) * (1 - percent)).toInt()
        }
        holder.getView<ImageView>(R.id.item_find_content_tab_img).apply {
            alpha = 1 - percent
            scaleX = 1 - percent
            scaleY = 1 - percent
            pivotX = width.toFloat()
            pivotY = height.toFloat()
        }
    }

}