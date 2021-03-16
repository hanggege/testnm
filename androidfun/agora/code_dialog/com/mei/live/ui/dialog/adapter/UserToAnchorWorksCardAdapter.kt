package com.mei.live.ui.dialog.adapter

import android.view.ViewGroup
import android.widget.Space
import com.chad.library.adapter.base.module.LoadMoreModule
import com.mei.live.ui.dialog.adapter.item.*
import com.mei.live.ui.dialog.linstener.IOperatingListener
import com.mei.live.views.ExpandableTextView.CardExpandableTextView
import com.mei.live.views.ExpandableTextView.ExpandableStatusFix
import com.mei.live.views.ExpandableTextView.StatusType
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.chick.friends.ProductBean
import com.net.model.user.GroupInfo

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.adapter
 * @ClassName:      UserToAnchorWorksCardAdapter
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/6/10 17:20
 * @UpdateUser:
 * @UpdateDate:     2020/6/10 17:20
 * @UpdateRemark:
 * @Version:
 */
/**
 * 列表尾部item
 */
const val TYPE_LOAD_MORE = 4//加载更多显示

class UserToAnchorWorksCardAdapter(list: MutableList<Any>) : MeiMultiQuickAdapter<Any, CardBaseHolder>(list),
        LoadMoreModule {
    /**
     * 工作室
     */
    private val TYPE_STUDIO = 0

    /**
     * 操作行为
     */
    private val TYPE_OPERATING = 1

    /**
     * 主播简介
     */
    private val TYPE_INTRODUCTION = 2


    /**
     * 主播作品
     */
    private val TYPE_WORKS = 3

    private val TYPE_BILLBOARD_ANCHOR_OPERATING = 4

    /**
     * 礼物，@TA 操作回调
     */
    var iOperatingAdapterListener: IOperatingListener? = null

    /**
     * 展开回调
     */
    var mOnExpandOrContractClickListener: CardExpandableTextView.OnExpandOrContractClickListener? = null

    /**
     * 保存展开状态
     */
    var statusType = StatusType.STATUS_EXPAND
    private val expandableStatus by lazy {
        object : ExpandableStatusFix {
            override fun getStatus(): StatusType {
                return statusType
            }

            override fun setStatus(status: StatusType) {
                statusType = status
            }
        }
    }

    init {
        //分行
        setGridSpanSizeLookup { _, viewType, _ ->
            when (viewType) {
                TYPE_INTRODUCTION -> 3
                TYPE_WORKS -> 1
                else -> 3
            }
        }
    }

    override fun getDefItemViewType(position: Int): Int {
        return when (getItemOrNull(position)) {
            is BillboardOperateWrapper -> {
                TYPE_BILLBOARD_ANCHOR_OPERATING
            }
            is OperatingType -> {
                TYPE_OPERATING
            }
            is GroupInfo -> {
                TYPE_STUDIO
            }
            is ProductBean -> {
                TYPE_WORKS
            }
            is CardIntroductionHolder.CardIntroductionData -> {
                TYPE_INTRODUCTION
            }
            else -> {
                -1
            }
        }
    }

    override fun convert(holder: CardBaseHolder, item: Any) {
        if (holder is CardOperatingHolder) {
            holder.iOperatingListener = iOperatingAdapterListener
        }
        holder.refresh(item)
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): CardBaseHolder {
        return when (viewType) {
            TYPE_INTRODUCTION -> {
                val viewHolder = CardIntroductionHolder(parent.layoutInflaterKtToParent(R.layout.item_card_dialog_introduction_holder))
                viewHolder.etv?.bind(expandableStatus)
                viewHolder.etv?.expandOrContractClickListener = mOnExpandOrContractClickListener
                return viewHolder
            }
            TYPE_STUDIO -> {
                CardStudioHolder(parent.layoutInflaterKtToParent(R.layout.item_card_studio))
            }
            TYPE_WORKS -> {
                CardWorksHolder(parent.layoutInflaterKtToParent(R.layout.item_user_works_holder))
            }
            TYPE_BILLBOARD_ANCHOR_OPERATING -> {
                BillboardToAnchorOperateHolder(parent.layoutInflaterKtToParent(R.layout.item_billboard_anchor_operate))
            }
            TYPE_OPERATING -> {
                CardOperatingHolder(parent.layoutInflaterKtToParent(R.layout.item_operating_holder))
            }
            else -> CardEmptyHold(Space(parent.context))
        }
    }

}