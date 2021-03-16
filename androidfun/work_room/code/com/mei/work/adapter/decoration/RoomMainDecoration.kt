package com.mei.work.adapter.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mei.orc.ext.dip
import com.mei.work.adapter.WorkRoomAdapter
import com.mei.work.adapter.item.AvatarBg
import com.mei.work.adapter.item.GiftPosition
import com.mei.work.adapter.item.WorkRoomAvatarData
import com.mei.work.adapter.item.WorkRoomGiftData

/**
 * RoomMainDecoration
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-04
 */
class RoomMainDecoration(context : Context) : RecyclerView.ItemDecoration() {

    private val avatarMidOffset : Float

    init {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val holderWidth = screenWidth / 4
        val avatarItemWidth = holderWidth - context.dip(15)
        avatarMidOffset = (holderWidth - avatarItemWidth) * 2f / 3
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter
        if (adapter !is WorkRoomAdapter) return
        val mData = adapter.data
        val position = (view.layoutParams as? RecyclerView.LayoutParams)?.viewLayoutPosition ?: -1
        val item = mData.getOrNull(position)
        if (item is WorkRoomGiftData) {
            when (item.position){
                GiftPosition.LEFT -> {
                    outRect.left = view.dip(7.5f)
                }
                GiftPosition.MID -> {
                    outRect.left = view.dip(3.75f)
                    outRect.right = view.dip(3.75f)
                }
                GiftPosition.RIGHT -> outRect.right = view.dip(7.5f)
            }
        }
        if (item is WorkRoomAvatarData) {
            when (item.color) {
                AvatarBg.BROWN -> outRect.left = view.dip(15)
                AvatarBg.DARK_BLUE -> {
                    outRect.left = avatarMidOffset.toInt()
                    outRect.right = (avatarMidOffset / 2).toInt()
                }
                AvatarBg.PURPLE -> {
                    outRect.left = (avatarMidOffset / 2).toInt()
                    outRect.right = avatarMidOffset.toInt()
                }
                AvatarBg.LIGHT_BLUE -> outRect.right = view.dip(15)
            }
        }
    }


}