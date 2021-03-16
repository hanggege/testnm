package com.mei.live.ui.dialog.adapter.item

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.dialog.adapter.item
 * @ClassName:      CardBaseHolder
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/6/10 18:04
 * @UpdateUser:
 * @UpdateDate:     2020/6/10 18:04
 * @UpdateRemark:
 * @Version:
 */

abstract class CardBaseHolder(itemView: View) : BaseViewHolder(itemView) {

    abstract fun refresh(item: Any)
}