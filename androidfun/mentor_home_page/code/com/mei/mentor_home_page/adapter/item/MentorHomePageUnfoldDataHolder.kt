package com.mei.mentor_home_page.adapter.item

import android.view.View
import androidx.core.view.updatePadding
import com.mei.mentor_home_page.adapter.ITEM_TYPE_WORK_UNFOLD
import com.mei.widget.holder.addOnClickListener
import com.mei.wood.R

/**
 *
 * @author Created by lenna on 2020/5/21
 * 展开item
 */
class MentorHomePageUnfoldDataHolder(var view: View) : MentorBaseHolder(view) {

    override fun refresh(item: Any) {
        if (item is Int && item == ITEM_TYPE_WORK_UNFOLD) {
            view.updatePadding(bottom = 0)
        }
        addOnClickListener(R.id.mentor_home_page_load_more_rrl)
    }


}