package com.mei.base.baseadapter.multi

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/11/27
 */
abstract class MultiViewHolder(view: View) : BaseViewHolder(view) {

    abstract fun refresh(data: Any?)
}
