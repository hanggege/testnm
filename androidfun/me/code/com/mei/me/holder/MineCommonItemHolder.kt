package com.mei.me.holder

import android.view.View
import android.widget.FrameLayout
import androidx.core.text.buildSpannedString
import androidx.core.view.updateLayoutParams
import com.mei.GrowingUtil
import com.mei.base.baseadapter.multi.MultiViewHolder
import com.mei.base.ui.nav.Nav
import com.mei.live.ext.parseColor
import com.mei.login.checkLogin
import com.mei.me.view.EditItemView
import com.mei.orc.Cxt
import com.mei.orc.ext.dip
import com.mei.wood.R
import com.mei.wood.extensions.appendLink
import com.net.model.user.MyPageInfo
import com.net.model.user.MyPageInfo.MeTabItemPosition
import kotlinx.android.synthetic.main.item_edit_layout.view.*

/**
 * Created by hang on 2020/7/24.
 */
class MineCommonItemHolder(view: View) : MultiViewHolder(view) {

    private var tabItem: MyPageInfo.MeTabItem? = null

    private val editItemView by lazy { getView<EditItemView>(R.id.edit_item_view) }

    init {
        itemView.setOnClickListener {
            if (tabItem?.needLogin != true || itemView.context.checkLogin()) {
                Nav.toAmanLink(itemView.context, tabItem?.action.orEmpty())
            }
            if (tabItem?.type == MyPageInfo.MeTabItemType.HONOR_MEDAL.name) {
                statHonorMedalClick()
            }
        }
    }

    override fun refresh(data: Any?) {
        (data as? MyPageInfo.MeTabItem)?.let { item ->
            tabItem = item
            when (item.position) {
                MeTabItemPosition.AREA_FIRST -> {
                    setItemMargin(dip(55), topMargin = dip(5))
                    setBackgroundResource(R.id.mine_common_item, R.drawable.bg_mine_item_first)
                }
                MeTabItemPosition.AREA_MID -> {
                    setItemMargin(dip(50))
                    setBackgroundResource(R.id.mine_common_item, R.drawable.bg_mine_item_mid)
                }
                MeTabItemPosition.AREA_LAST -> {
                    setItemMargin(dip(55), bottomMargin = dip(5))
                    setBackgroundResource(R.id.mine_common_item, R.drawable.bg_mine_item_last)
                }
                MeTabItemPosition.SINGLE -> {
                    setItemMargin(dip(65))
                    setBackgroundResource(R.id.mine_common_item, R.drawable.bg_mine_item_single)
                }
                MeTabItemPosition.SINGLE_TOP -> {
                    setItemMargin(dip(65), bottomMargin = dip(5))
                    setBackgroundResource(R.id.mine_common_item, R.drawable.bg_mine_item_single_top)
                }
                MeTabItemPosition.SINGLE_BOTTOM -> {
                    setItemMargin(dip(65), topMargin = dip(5))
                    setBackgroundResource(R.id.mine_common_item, R.drawable.bg_mine_item_single_bottom)
                }
                else -> {
                    setItemMargin(dip(50))
                    setBackgroundResource(R.id.mine_common_item, R.drawable.bg_mine_item_single)
                }
            }
            editItemView.apply {
                edit_container.background = null
                displayEditIcon(item.icon, R.color.color_e8e8e8)
                displayTipsIcon(item.tipsIcon)
                editTitle = buildSpannedString {
                    appendLink(item.name?.text.orEmpty(), item.name?.color.parseColor(Cxt.getColor(R.color.color_333333)))
                }
                editText = buildSpannedString {
                    appendLink(item.tips?.text.orEmpty(), item.tips?.color.parseColor(Cxt.getColor(R.color.color_333333)))
                }

            }
            if (item.type == MyPageInfo.MeTabItemType.HONOR_MEDAL.name) {
                statHonorMedalBrowse()
            }
        }
    }

    private fun setItemMargin(height: Int, topMargin: Int = 0, bottomMargin: Int = 0) {
        itemView.updateLayoutParams { this.height = height }
        editItemView.updateLayoutParams<FrameLayout.LayoutParams> {
            this.topMargin = topMargin
            this.bottomMargin = bottomMargin
        }
    }

    private fun dip(value: Int) = itemView.dip(value)

    /**荣誉勋章浏览*/
    private fun statHonorMedalBrowse() = try {
        GrowingUtil.track("function_view",
                "function_name", "荣誉勋章入口",
                "function_page", "我的页",
                "click_type", "")
    } catch (e: Exception) {
        e.printStackTrace()
    }

    /**荣誉勋章点击入口*/
    private fun statHonorMedalClick() = try {
        GrowingUtil.track("function_view",
                "function_name", "荣誉勋章入口",
                "function_page", "我的页",
                "click_type", "入口点击")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}