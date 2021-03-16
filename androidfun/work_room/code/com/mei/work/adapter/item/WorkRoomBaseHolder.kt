package com.mei.work.adapter.item

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.network.holder.SpiceHolder
import com.mei.init.spiceHolder
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.widget.holder.activity
import com.mei.wood.rx.MeiUiFrame

/**
 * WorkRoomBashHolder
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-24
 */
open class WorkRoomBaseHolder(itemView: View) : BaseViewHolder(itemView) {
    var apiSpiceMgr: RetrofitClient
        get() = (activity as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
        set(_) {}

    open fun refresh(item: Any) {}

    fun showLoading(show: Boolean) {
        (activity as? MeiUiFrame)?.showLoading(show)
    }
}

class Footer