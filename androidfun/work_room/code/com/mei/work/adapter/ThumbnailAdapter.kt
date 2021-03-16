package com.mei.work.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.chick.friends.ProductBean

/**
 * ThumbnailAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-28
 */
class ThumbnailAdapter(list: MutableList<ProductBean>)
    : MeiMultiQuickAdapter<ProductBean, BaseViewHolder>(list) {

    override fun getDefItemViewType(position: Int): Int {
        //未设置URL为空界面
        return if (getItem(position).url.orEmpty().isEmpty() && getItem(position).cover.orEmpty().isEmpty()) 100
        else 0
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val resId = if (viewType == 100) R.layout.item_work_room_main_thumbnail_empty else R.layout.item_work_room_main_thumbnail
        return BaseViewHolder(parent.layoutInflaterKt(resId))
    }

    override fun convert(holder: BaseViewHolder, item: ProductBean) {
        if (holder.itemViewType == 0) {
            holder.setVisible(R.id.room_main_thumbnail_top, item.isTop == 1)
                    .setVisible(R.id.room_main_thumbnail_works_size,
                            item.countAll > 0 && holder.bindingAdapterPosition == 0)
                    .setText(R.id.room_main_thumbnail_works_size, "${item.countAll}个作品")
                    .setVisible(R.id.room_main_thumbnail_play, item.worksType == 1)
                    .setVisible(R.id.room_main_thumbnail_top, item.isTop == 1)
            holder.getView<ImageView>(R.id.room_main_thumbnail).apply {
                if (layoutParams != null) {
                    updateLayoutParams { width = if (holder.bindingAdapterPosition == 0) height else dip(128) }
                }
                Glide.with(this)
                        .asBitmap()
                        .load(item.cover)
                        .placeholder(R.color.color_e8e8e8)
                        .error(R.color.color_e8e8e8)
                        .into(this)
            }
        }

    }

}