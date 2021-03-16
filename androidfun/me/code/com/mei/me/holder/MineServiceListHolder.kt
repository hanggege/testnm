package com.mei.me.holder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.flyco.roundview.RoundTextView
import com.joker.im.custom.chick.ServiceInfo
import com.mei.base.baseadapter.multi.MultiViewHolder
import com.mei.base.ui.nav.Nav
import com.mei.chat.ui.view.startPrivateUpstream
import com.mei.login.checkLogin
import com.mei.me.PageSnapEdgeHelper
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.widget.holder.activity
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.user.MyPageInfo

/**
 * MineServiceListHolder
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-01
 */
class MineServiceListHolder(view: View) : MultiViewHolder(view) {

    private var noServiceData: MyPageInfo.NoServiceLabel? = null

    private val serviceList = arrayListOf<ServiceInfo>()
    private val adapter = MineServiceAdapter(serviceList)
    private val recyclerView: RecyclerView by lazy { getView<RecyclerView>(R.id.recycler_view) }

    private val emptyView by lazy {
        recyclerView.layoutInflaterKtToParent(R.layout.item_no_service)
    }

    private val footerView by lazy {
        recyclerView.layoutInflaterKtToParent(R.layout.item_service_show_more)
    }

    init {
        recyclerView.adapter = adapter
        recyclerView.onFlingListener = null
        PageSnapEdgeHelper().attachToRecyclerView(recyclerView)
    }

    override fun refresh(data: Any?) {
        if (data is MineServiceListData) {
            noServiceData = data.noServiceLabel
            serviceList.clear()
            serviceList.addAll(data.list.orEmpty())
            if (serviceList.isEmpty()) {
                adapter.setEmptyView(emptyView())
            } else {
                adapter.removeEmptyView()
                if (serviceList.size >= 10) {
                    adapter.setFooterView(footerView, orientation = LinearLayout.HORIZONTAL)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun emptyView(): View {
        if (noServiceData?.noServiceLabel.isNullOrEmpty().not()) {
            emptyView.findViewById<TextView>(R.id.item_service_consult).text = noServiceData!!.noServiceLabel
        }
        emptyView.findViewById<RoundTextView>(R.id.item_service_start_text).isVisible = noServiceData?.showButton
                ?: true
        return emptyView
    }

    inner class MineServiceAdapter(list: MutableList<ServiceInfo>) : BaseQuickAdapter<ServiceInfo, MineServiceAdapter.ServiceHolder>(R.layout.item_service, list) {
        override fun convert(holder: ServiceHolder, item: ServiceInfo) {
            holder.convert(item)
        }

        inner class ServiceHolder(view: View) : BaseViewHolder(view) {

            private var info: ServiceInfo = ServiceInfo()

            init {
                itemView.setOnClickListener {
                    if (context.checkLogin()) {
                        Nav.toAmanLink(itemView.context, noServiceData?.toWebServiceListAction)
                    }
                }
                getView<View>(R.id.item_service_start_text).setOnClickListener {
                    if (adapter.hasEmptyView() && context.checkLogin()) {
                        Nav.toAmanLink(itemView.context, noServiceData?.toAllServiceAction)
                        return@setOnClickListener
                    }
                    (activity as? MeiCustomActivity)?.startPrivateUpstream(info.publisherId, info.specialServiceOrderId, "mine")
                }
            }

            fun convert(data: ServiceInfo) {
                info = data
                setText(R.id.item_service_content_text, info.serviceName)
                setText(R.id.item_service_consult, info.publisherInfo?.nickname.orEmpty())
            }
        }
    }
}

class MineServiceListData(val list: MutableList<ServiceInfo>?, val noServiceLabel: MyPageInfo.NoServiceLabel? = null)