package com.mei.me.adapter;

import android.graphics.Rect
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.net.model.chick.room.RoomList

/**
 * MyLikeListAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-17
 */
class MyLikeListAdapter(var list: MutableList<RoomList.ShortVideoItemBean>) : BaseQuickAdapter<RoomList.ShortVideoItemBean, BaseViewHolder>(R.layout.item_my_like_video, list), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: RoomList.ShortVideoItemBean) {
        holder.getView<ImageView>(R.id.my_like_img).glideDisplay(item.videoCoverUrl.orEmpty())
    }
}

class MyLikeListDecorator : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter
        if (adapter !is MyLikeListAdapter) return
        val position = (view.layoutParams as? RecyclerView.LayoutParams)?.viewLayoutPosition ?: -1
        when (position % 3) {
            0 -> outRect.right = view.dip(1)
            2 -> outRect.left = view.dip(1)
            1 -> {
                outRect.left = view.dip(0.5f)
                outRect.right = view.dip(0.5f)
            }
        }
        if (position > 2) {
            outRect.top = view.dip(1.5f)
        }
    }
}