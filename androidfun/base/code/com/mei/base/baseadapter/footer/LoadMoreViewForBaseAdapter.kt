package com.mei.base.baseadapter.footer

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.layoutInflaterKt
import com.mei.wood.R

/**
 * Created by Ling on 2018/6/11.
 *
 * @ 描述：footer
 */
class LoadMoreViewForBaseAdapter : BaseLoadMoreView() {

    override fun getLoadComplete(holder: BaseViewHolder): View = holder.getView(R.id.load_more_load_complete_view)

    override fun getLoadEndView(holder: BaseViewHolder): View = holder.getView(R.id.load_more_load_end_view)

    override fun getLoadFailView(holder: BaseViewHolder): View = holder.getView(R.id.load_more_load_fail_view)

    override fun getLoadingView(holder: BaseViewHolder): View = holder.getView(R.id.load_more_loading_view)

    override fun getRootView(parent: ViewGroup): View = parent.layoutInflaterKt(R.layout.footer_no_more_layout_for_base_adapter)
}