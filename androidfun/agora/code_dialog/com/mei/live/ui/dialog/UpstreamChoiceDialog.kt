package com.mei.live.ui.dialog;

import android.graphics.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.common.APPLY_UPSTREAM_STATE
import com.mei.live.ext.createSplitSpannable
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.fragment.applyUpStream
import com.mei.live.ui.fragment.startApplyUpstream
import com.mei.orc.Cxt
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.widget.choince.ChoiceView
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeToastKt
import com.net.model.room.RoomType
import com.net.model.room.UpstreamTypeList
import com.net.network.room.UpstreamTypeListRequest

/**
 * UpstreamChoiceDialog
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-17
 */
fun FragmentActivity.showUpstreamChoiceDialog(roomId: String) {
    val activity = this as? VideoLiveRoomActivity

    val dialog = NormalDialogLauncher.newInstance()
    val content = layoutInflaterKt(R.layout.dialog_select_service)
    content.findViewById<ChoiceView>(R.id.back_choice).setOnClickListener {
        dialog.dismissAllowingStateLoss()
    }
    val serviceList = content.findViewById<RecyclerView>(R.id.service_list)

    var data: UpstreamTypeList? = null

    fun applyExclusive() {
        data?.option?.also {
            val couponNum = if (it.extra == null) 0 else -1
            if (data?.isApplyOtherRoom == true) {
                /** 如果已经在其他直播间申请过连线*/
                showDoubleBtnCommonView("连线申请提示", "您目前正在其他知心达人房间排队\n确定申请将从其他队列退出", "", "取消", "确定", false) { dialog, back ->
                    dialog.dismissAllowingStateLoss()
                    if (back == 1) {
                        activity?.startApplyUpstream(it, couponNum)
                    }
                }
            } else {
                activity?.startApplyUpstream(it, couponNum)
            }
        }
    }

    val adapter = object : BaseQuickAdapter<UpstreamTypeList.UpstreamTypeItem, BaseViewHolder>(R.layout.item_apply_upstream_choice) {
        override fun convert(holder: BaseViewHolder, item: UpstreamTypeList.UpstreamTypeItem) {
            holder.getView<TextView>(R.id.upstream_choice_service_title).apply {
                text = item.subTitle
            }

            holder.getView<TextView>(R.id.upstream_choice_service_time).apply {
                text = item.text.createSplitSpannable(Cxt.getColor(R.color.color_ff8200))
            }
            val rootLayout = holder.getView<LinearLayout>(R.id.root_layout)

            if (item.linkType == 1) {
                rootLayout.setBackgroundResource(R.drawable.bg_special_upstream_option)
            } else {
                rootLayout.setBackgroundResource(R.drawable.bg_exclusive_upstream_option)
            }

            holder.itemView.setOnClickListener {
                dialog.dismissAllowingStateLoss()
                if (item.linkType == 1) {
                    activity?.applyUpStream(roomId, RoomType.EXCLUSIVE, item.serviceOrderId, couponNum = if (data?.option?.extra == null) 0 else -1) { info ->
                        if (info != null) {
                            activity.pendingUpStream = true
                            postAction(APPLY_UPSTREAM_STATE, true)
                            showSingleBtnCommonDialog("知心达人已收到申请", "当前共${info.applyCount}人排麦,请耐心等待", "注：退出APP将自动下麦") {
                                it.dismissAllowingStateLoss()
                            }
                        }
                    }
                } else {
                    applyExclusive()
                }
            }
        }
    }
    serviceList.adapter = adapter

    activity?.showLoading(true)
    activity?.apiSpiceMgr?.executeToastKt(UpstreamTypeListRequest(roomId), success = {
        if (it.isSuccess) {
            data = it.data
            val list = data?.list.orEmpty()
            val index = list.indexOfFirst { item -> item.linkType == 1 }
            if (index < 0) {
                applyExclusive()
            } else {
                adapter.setList(it.data?.list)
                dialog.showDialog(this, DialogData(customView = content), okBack = {})
            }
        }

    }, finish = { activity.showLoading(false) })
}