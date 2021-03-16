package com.mei.im.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.widget.holder.addOnClickListener
import com.mei.widget.holder.addOnLongClickListener
import com.mei.wood.R

class IMReplayRecleViewAdapter(data: MutableList<String>) :
        BaseQuickAdapter<String, BaseViewHolder>(R.layout.im_item_common_words, data),
        DraggableModule {
    @SuppressLint("ClickableViewAccessibility")
    override fun convert(holder: BaseViewHolder, item: String) {
        val rightImg = if (!draggableModule.isDragEnabled) {
            holder.addOnClickListener(R.id.im_reply_item_layout)
            holder.addOnLongClickListener(R.id.im_reply_item_layout)
            R.drawable.icon_add_phrase
        } else {
            holder.getView<View>(R.id.im_reply_item_layout).setOnTouchListener { _, event ->
                holder.itemView.onTouchEvent(event)
            }
            R.drawable.icon_sort_phrase
        }
        holder.setText(R.id.im_reply_item_txt, item)
        holder.setBackgroundResource(R.id.im_replay_item_right_img, rightImg)
    }

}
