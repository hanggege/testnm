package com.mei.base.weight.loading

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.loadmore.LoadMoreStatus
import com.chad.library.adapter.base.util.getItemView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.widget.holder.setVisibleAndGone
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/17
 */
class LoadFooterView(val visiableNumber: Int = 6) : BaseLoadMoreView() {


    override fun convert(holder: BaseViewHolder, position: Int, loadMoreStatus: LoadMoreStatus) {
        holder.itemView.isVisible = holder.bindingAdapterPosition >= visiableNumber
        when (loadMoreStatus) {
            LoadMoreStatus.Loading -> {
                holder.setVisibleAndGone(R.id.loading_container, true)
                        .setVisibleAndGone(R.id.loaded_container, false)
            }
            else -> {
                holder.setVisibleAndGone(R.id.loading_container, false)
                        .setVisibleAndGone(R.id.loaded_container, true)
            }

        }
    }

    override fun getRootView(parent: ViewGroup): View = parent.getItemView(R.layout.footer_no_more_layout)

    override fun getLoadComplete(holder: BaseViewHolder): View = holder.getView(View.NO_ID)

    override fun getLoadEndView(holder: BaseViewHolder): View = holder.getView(View.NO_ID)

    override fun getLoadFailView(holder: BaseViewHolder): View = holder.getView(View.NO_ID)

    override fun getLoadingView(holder: BaseViewHolder): View = holder.getView(View.NO_ID)
}