package com.mei.mentor_home_page.adapter.item

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.network.holder.SpiceHolder
import com.mei.init.spiceHolder
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.widget.holder.activity
import com.mei.wood.rx.MeiUiFrame

/**
 *
 * @author Created by zyh on 2020/5/20
 */
open class MentorBaseHolder(itemView: View) : BaseViewHolder(itemView) {
    var apiSpiceMgr: RetrofitClient
        get() = (activity as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
        set(_) {}

    open fun refresh(item: Any) {}

    fun showLoading(show: Boolean) {
        (activity as? MeiUiFrame)?.showLoading(show)
    }


}