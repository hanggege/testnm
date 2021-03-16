package com.mei.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.common.CHANG_LOGIN
import com.mei.chat.toImChat
import com.mei.login.toLogin
import com.mei.message.adapter.VisitorsMessageAdapter
import com.mei.message.ext.loadVisitorsList
import com.mei.message.ext.refreshVisitorsList
import com.mei.orc.event.bindAction
import com.mei.orc.john.model.JohnUser
import com.mei.wood.R
import com.net.model.chick.message.VisitorsMessage
import kotlinx.android.synthetic.main.empty_notify_layout.*
import kotlinx.android.synthetic.main.empty_to_login_layout.*
import kotlinx.android.synthetic.main.fragment_visitors.*

/**
 *
 * @author Created by lenna on 2020/6/15
 * 访问记录页面
 */
class VisitorsFragment : BaseBannerFragment() {
    var mPageNo: Int = 1
    var mPageSize: Int = 20
    var timeResult = ""
    override val bannerAdapter: BaseQuickAdapter<Any, BaseViewHolder> by lazy { visitorsAdapter }
    override val dataChange: () -> Unit by lazy {
        {
            to_login_layout.isGone = JohnUser.getSharedUser().hasLogin()
            im_empty_layout.isVisible = mVisitorsList.isEmpty()
        }
    }

    val mVisitorsList: ArrayList<Any> = arrayListOf()
    val visitorsAdapter: VisitorsMessageAdapter by lazy {
        VisitorsMessageAdapter(mVisitorsList).apply {
            setOnItemClickListener { _, _, position ->
                val visitors: Any? = mVisitorsList[position]
                if (visitors is VisitorsMessage.Visitors) {
                    visitors.info?.apply {
                        activity?.toImChat("$userId")
                    }
                }
            }
            loadMoreModule.setOnLoadMoreListener {
                //加载更多
                loadVisitorsList()
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_visitors, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visitors_rv.adapter = visitorsAdapter
        goto_login_view.setOnClickListener { activity?.toLogin() }
        empty_icon_iv.setImageResource(R.drawable.icon_empty_visitors)
        empty_hint_tv.text = "暂无访客记录"
        bindAction<Boolean>(CHANG_LOGIN) {
            if (it == true) {
                refreshVisitorsList()
            } else {
                mVisitorsList.clear()
                visitorsAdapter.notifyDataSetChanged()
            }
        }
        visitors_refresh.setOnRefreshListener {
            //刷新访客数据
            refreshVisitorsList()
        }
        refreshVisitorsList()

    }

    override fun refreshIMList() {

    }
}