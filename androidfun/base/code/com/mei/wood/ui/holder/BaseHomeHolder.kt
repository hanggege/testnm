package com.mei.wood.ui.holder

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/10
 */
abstract class BaseHomeHolder(view: View) : BaseViewHolder(view) {

    var proCateName = ""

    abstract fun refresh(data: Any)

}