package com.mei.intimate.adapter

import android.graphics.Rect
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mei.intimate.IntimateHomeFragment
import com.mei.intimate.view.*
import com.mei.orc.ext.dp
import com.net.model.chick.room.RoomItemGridType
import com.net.model.chick.room.RoomItemType
import com.net.model.chick.room.RoomList

/**
 * IntimateGridDecoration
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-03
 *
 */
class IntimateHomeDecoration : RecyclerView.ItemDecoration() {

    private val margin30dp = 30.dp.toInt()
    private val margin25dp = 25.dp.toInt()
    private val margin20dp = 20.dp.toInt()
    private val margin15dp = 15.dp.toInt()
    private val margin13dp = 13.dp.toInt()
    private val margin10dp = 10.dp.toInt()
    private val margin5dp = 5.dp.toInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = (view.layoutParams as? RecyclerView.LayoutParams)?.viewLayoutPosition ?: -1

        when (view) {
            is IntimateShufflingNoticeView -> {
                outRect.top = if (position == 0) 0 else margin13dp
                outRect.left = margin15dp
                outRect.right = margin15dp
                return
            }
            is IntimateSeparationTitleView,
            is TextView -> {
                outRect.top = if (position == 0) 0 else margin5dp
                outRect.left = margin15dp
                outRect.right = margin15dp
                return
            }
            is IntimateSelectCateView -> {
                outRect.left = margin15dp
                outRect.right = margin15dp
                return
            }
            is IntimateFollowListView -> {
                outRect.top = if (position == 0) 0 else margin20dp
                return
            }
            is IntimateNavigateBarAppView -> {
                return
            }
            else -> super.getItemOffsets(outRect, view, parent, state)
        }

        if (parent.adapter !is IntimateHomeAdapter) return
        val item = (parent.adapter as IntimateHomeAdapter).data[position]

        when (item.gridType) {
            RoomItemGridType.LEFT_TOP -> when (item.type) {
                RoomItemType.ONLINE_PUBLISHER -> {
                    outRect.top = if (position == 0) 0 else margin20dp
                    outRect.left = margin15dp
                    outRect.right = margin10dp
                }
                RoomItemType.PUBLIC_BROADCAST -> {
                    outRect.top = margin10dp
                    outRect.left = margin5dp
                }
                RoomItemType.BANNER_FILL -> {
                    outRect.left = margin5dp
                }
                else -> {
                    outRect.top = if (position == 0) 0 else margin10dp
                    outRect.left = margin5dp
                }
            }
            RoomItemGridType.RIGHT_TOP -> when (item.type) {
                RoomItemType.ONLINE_PUBLISHER -> {
                    outRect.top = if (position == 1) 0 else margin20dp
                    outRect.left = margin10dp
                    outRect.right = margin15dp
                }
                RoomItemType.PUBLIC_BROADCAST -> {
                    outRect.top = margin10dp
                    outRect.right = margin5dp
                }
                RoomItemType.BANNER_FILL -> {
                    outRect.top = margin5dp
                    outRect.right = margin5dp
                }
                else -> {
                    outRect.top = if (position == 1) 0 else margin10dp
                    outRect.right = margin5dp
                }
            }
            RoomItemGridType.LEFT -> when (item.type) {
                RoomItemType.ONLINE_PUBLISHER -> {
                    outRect.left = margin15dp
                    outRect.right = margin10dp
                    outRect.top = margin20dp
                }
                RoomItemType.PUBLIC_BROADCAST -> {
                    outRect.top = margin5dp
                    outRect.left = margin5dp
                }
                else -> {
                    outRect.left = margin5dp
                }
            }
            RoomItemGridType.RIGHT -> when (item.type) {
                RoomItemType.ONLINE_PUBLISHER -> {
                    outRect.left = margin10dp
                    outRect.right = margin15dp
                    outRect.top = margin20dp
                }
                RoomItemType.PUBLIC_BROADCAST -> {
                    outRect.top = margin5dp
                    outRect.right = margin5dp
                }
                else -> {
                    outRect.right = margin5dp
                }
            }
            else -> {
                super.getItemOffsets(outRect, view, parent, state)
            }
        }
    }


}

fun IntimateHomeFragment.setItemLocation(list: MutableList<RoomList.BaseItem<*>>) {
    if (list.isNullOrEmpty()) return
    setItemGridType(RoomItemGridType.WHOLE, list[0])
    var previousType = list[0].gridType
    for (index in 1 until list.size) {
        setItemGridType(previousType, list[index])
        previousType = list[index].gridType
    }
}

//目前只考虑spanCount == 2的情况
private fun setItemGridType(previousType: RoomItemGridType, item: RoomList.BaseItem<*>) {
    item.gridType = when (item.content) {
        is RoomList.HomePageNavigateBar,
        is RoomList.ProduceCateItemBean,
        is RoomList.SeparationTitle,
        is RoomList.RecommendPublisher,
        is RoomList.FollowedPublisher,
        is RoomList.UserGuide,
        is RoomList.ScrollingMessage,
        is RoomList.NavigateBarApp,
        is RoomList.FooterLabel -> RoomItemGridType.WHOLE
        else -> when (previousType) {
            RoomItemGridType.WHOLE -> RoomItemGridType.LEFT_TOP
            RoomItemGridType.LEFT_TOP -> RoomItemGridType.RIGHT_TOP
            RoomItemGridType.RIGHT_TOP -> RoomItemGridType.LEFT
            RoomItemGridType.LEFT -> RoomItemGridType.RIGHT
            RoomItemGridType.RIGHT -> RoomItemGridType.LEFT
        }
    }
}