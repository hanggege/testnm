package com.mei.articles.adapter.holder

import android.view.View
import android.widget.TextView
import com.mei.wood.ui.holder.BaseHomeHolder

/**
 *  Created by zzw on 2019-12-20
 *  Des:
 */

class ArticlesTitleHolder(view: View) : BaseHomeHolder(view) {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun refresh(data: Any) {
        (itemView as? TextView)?.text = data as? String
    }

}