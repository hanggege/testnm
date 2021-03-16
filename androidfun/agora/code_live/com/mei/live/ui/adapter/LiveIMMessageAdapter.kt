package com.mei.live.ui.adapter

import android.view.ViewGroup
import android.widget.Space
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.message.CustomMessage
import com.mei.live.ui.adapter.item.LiveIMCourseServiceCardHolder
import com.mei.live.ui.adapter.item.LiveIMGeneralCardHolder
import com.mei.live.ui.adapter.item.LiveIMMessageTextHolder
import com.mei.live.ui.fragment.LiveIMSplitFragment
import com.mei.orc.ext.layoutInflaterKt
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-10
 */

private const val ITEM_CELL_TYPE = 111
private const val ITEM_NONE_TYPE = 112
private const val ITEM_SERVICE_EX_TYPE = 113

//课程卡片
private const val ITEM_COURSE_CARD_TYPE = 114
private const val ITEM_GENERAL_CARD_TYPE = 115

class LiveIMMessageAdapter(val fragment: LiveIMSplitFragment, val list: MutableList<CustomMessage>) :
        MeiMultiQuickAdapter<CustomMessage, BaseViewHolder>(list) {

    override fun getDefItemViewType(position: Int): Int {
        val msg = list.getOrNull(position)
        return when {
            msg?.customMsgType == CustomType.special_service_card -> ITEM_SERVICE_EX_TYPE
            msg?.customMsgType == CustomType.course_card -> ITEM_COURSE_CARD_TYPE
            msg?.customMsgType == CustomType.general_card -> ITEM_GENERAL_CARD_TYPE
            msg?.customInfo?.data is ChickCustomData
            -> ITEM_CELL_TYPE
            else -> ITEM_NONE_TYPE
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            ITEM_CELL_TYPE -> LiveIMMessageTextHolder(fragment, parent.layoutInflaterKt(R.layout.item_msg_text_item))
            ITEM_COURSE_CARD_TYPE,
            ITEM_SERVICE_EX_TYPE -> LiveIMCourseServiceCardHolder(fragment, parent.layoutInflaterKt(R.layout.item_msg_service_exclusive_text_item))
            ITEM_GENERAL_CARD_TYPE -> LiveIMGeneralCardHolder(fragment,parent.layoutInflaterKt(R.layout.item_msg_general_card))
            else -> BaseViewHolder(Space(parent.context))
        }
    }

    override fun convert(holder: BaseViewHolder, item: CustomMessage) {
        (holder as? LiveIMMessageTextHolder)?.refresh(item)
        (holder as? LiveIMCourseServiceCardHolder)?.refresh(item)
        (holder as? LiveIMGeneralCardHolder)?.refresh(item)
    }
}




