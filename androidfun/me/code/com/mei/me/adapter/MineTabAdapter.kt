package com.mei.me.adapter

import android.view.ViewGroup
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.baseadapter.multi.MultiViewHolder
import com.mei.me.holder.*
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.user.MyPageInfo

/**
 * Created by hang on 2020/7/9.
 */
private const val MINE_TAB_USER_HEAD = 100
private const val MINE_TAB_WALLET = 101
private const val MINE_TAB_COMMON_ITEM = 102
private const val MINE_TAB_FOLLOW_ANCHOR = 103
private const val MINE_TAB_SERVICE_LABEL = 104
private const val MINE_TAB_THREE_ITEM = 105
private const val MINE_TAB_TWO_ITEM = 106

class MineTabAdapter(list: MutableList<Any>) : MeiMultiQuickAdapter<Any, BaseViewHolder>(list) {

    override fun getDefItemViewType(position: Int): Int {
        return when (val item = getItemOrNull(position)) {
            is MyPageInfo.Info -> MINE_TAB_USER_HEAD
            is MyPageInfo.Finance -> MINE_TAB_WALLET
            is MineFollowAnchorListData -> MINE_TAB_FOLLOW_ANCHOR
            is MineServiceListData -> MINE_TAB_SERVICE_LABEL
            is MineMutableData -> {
                val size = item.list?.size ?: 0
                if (item.forceGrid) return MINE_TAB_THREE_ITEM
                when (size) {
                    1 -> MINE_TAB_COMMON_ITEM
                    2 -> MINE_TAB_TWO_ITEM
                    else -> MINE_TAB_THREE_ITEM
                }
            }
            else -> MINE_TAB_COMMON_ITEM
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            MINE_TAB_USER_HEAD -> MineTabHeadHolder(parent.layoutInflaterKtToParent(R.layout.view_mine_tab_head))
            MINE_TAB_WALLET -> MineWalletHolder(parent.layoutInflaterKtToParent(R.layout.view_mine_wallet))
            MINE_TAB_FOLLOW_ANCHOR -> MineFollowAnchorListHolder(parent.layoutInflaterKtToParent(R.layout.view_mine_follow_anchor_list))
            MINE_TAB_SERVICE_LABEL -> MineServiceListHolder(parent.layoutInflaterKtToParent(R.layout.view_mine_service_list))
            MINE_TAB_TWO_ITEM -> MineTwoItemHolder(parent.layoutInflaterKtToParent(R.layout.view_mine_two_item))
            MINE_TAB_THREE_ITEM -> MineGridHolder(parent.layoutInflaterKtToParent(R.layout.view_mine_grid))
            else -> MineCommonItemHolder(parent.layoutInflaterKtToParent(R.layout.view_mine_common_item))
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun convert(holder: BaseViewHolder, item: Any) {
        (holder as? MultiViewHolder)?.refresh(item)
    }

}