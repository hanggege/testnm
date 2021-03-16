package com.mei.me.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.baseadapter.multi.MultiViewHolder
import com.mei.base.ui.nav.Nav
import com.mei.live.ext.parseColor
import com.mei.login.checkLogin
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.mei.wood.extensions.appendLink
import com.net.model.user.MyPageInfo

/**
 * MineNewGridHolder
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-28
 */
class MineGridHolder(view: View) : MultiViewHolder(view) {

    private val layout by lazy { getView<RecyclerView>(R.id.mine_grid) }
    private val list = arrayListOf<MyPageInfo.MeTabItem>()
    private val adapter by lazy {
        MineGridAdapter(list).apply {
            setOnItemClickListener { _, _, position ->
                turnTo(list[position])
            }
        }
    }

    init {
        layout.layoutManager = GridLayoutManager(view.context, 3)
        layout.adapter = adapter
    }

    inner class MineGridAdapter(list: MutableList<MyPageInfo.MeTabItem>) : BaseQuickAdapter<MyPageInfo.MeTabItem, BaseViewHolder>(R.layout.item_user_wallet, list) {
        override fun convert(holder: BaseViewHolder, item: MyPageInfo.MeTabItem) {
            holder.getView<ImageView>(R.id.user_img).glideDisplay(item.icon.orEmpty())
            holder.getView<TextView>(R.id.user_text).apply {
                text = buildSpannedString {
                    appendLink(item.name?.text.orEmpty(), item.name?.color.parseColor(Cxt.getColor(R.color.color_333333)))
                }
                paint.isFakeBoldText = true
            }
            holder.getView<TextView>(R.id.user_hint).apply {
                if (item.tips == null) {
                    isVisible = false
                } else {
                    isVisible = true
                    text = buildSpannedString {
                        appendLink(item.tips?.text.orEmpty(), item.tips?.color.parseColor(Cxt.getColor(R.color.color_333333)))
                    }
                }
            }
        }
    }

    override fun refresh(data: Any?) {
        if (data is MineMutableData && !data.list.isNullOrEmpty()) {
            setGridHolderStyle(data.position)
            list.clear()
            list.addAll(data.list)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setGridHolderStyle(position: MyPageInfo.MeTabItemPosition) {
        when (position) {
            MyPageInfo.MeTabItemPosition.AREA_FIRST -> {
                setBackgroundResource(R.id.mine_grid, R.drawable.bg_mine_item_first)
            }
            MyPageInfo.MeTabItemPosition.AREA_MID -> {
                setBackgroundResource(R.id.mine_grid, R.drawable.bg_mine_item_mid)
            }
            MyPageInfo.MeTabItemPosition.AREA_LAST -> {
                setBackgroundResource(R.id.mine_grid, R.drawable.bg_mine_item_last)
            }
            MyPageInfo.MeTabItemPosition.SINGLE_TOP -> {
                setBackgroundResource(R.id.mine_grid, R.drawable.bg_mine_item_single_top)
            }
            MyPageInfo.MeTabItemPosition.SINGLE_BOTTOM -> {
                setBackgroundResource(R.id.mine_grid, R.drawable.bg_mine_item_single_bottom)
            }
            MyPageInfo.MeTabItemPosition.SINGLE -> {
                setBackgroundResource(R.id.mine_grid, R.drawable.bg_mine_item_single)
            }
            else -> setBackgroundResource(R.id.mine_grid, R.drawable.bg_mine_item_single)
        }
    }

    private fun turnTo(tabItem: MyPageInfo.MeTabItem) {
        if (!tabItem.needLogin || itemView.context.checkLogin()) {
            Nav.toAmanLink(itemView.context, tabItem.action.orEmpty())
        }
    }
}

class MineMutableData(val list: MutableList<MyPageInfo.MeTabItem>?, val forceGrid: Boolean = false, val position: MyPageInfo.MeTabItemPosition = MyPageInfo.MeTabItemPosition.SINGLE)