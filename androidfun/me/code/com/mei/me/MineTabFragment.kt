package com.mei.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mei.GrowingUtil
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.common.CHANG_NICKNAME
import com.mei.base.common.HONOR_MEDAL_CHANGED
import com.mei.base.network.netchange.NetWorkListener
import com.mei.base.network.netchange.netChangeToAvailable
import com.mei.base.ui.nav.Nav
import com.mei.base.util.viewmodel.ViewModelFactory
import com.mei.login.checkLogin
import com.mei.me.adapter.MineTabAdapter
import com.mei.orc.event.bindAction
import com.mei.orc.net.NetInfo
import com.mei.orc.tab.TabItemContent
import com.mei.wood.R
import com.mei.wood.ui.fragment.TabItemFragment
import com.net.MeiUser
import com.net.model.user.MyPageInfo
import kotlinx.android.synthetic.main.fragment_me_tab.*

/**
 * Created by hang on 2020/7/9.
 */

class MineTabFragment : TabItemFragment(), NetWorkListener, TabItemContent {

    private val adapter by lazy { MineTabAdapter(arrayListOf()) }

    private val myPageViewModel by viewModels<MyPageViewModel> { ViewModelFactory(this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_me_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.adapter = adapter
        myPageViewModel.apply {
            myPageLiveData.observe(viewLifecycleOwner, Observer {
                adapter.setList(it)
            })
            isLoading.observe(viewLifecycleOwner, Observer {
                if (myPageLiveData.value.isNullOrEmpty()) {
                    showLoading(it)
                }
            })
            startLiveData.observe(viewLifecycleOwner, Observer {
                showBottomLiveLabel(it)
            })
            fetchMyPageInfo()
        }
        bindAction<Boolean>(CHANG_LOGIN) {
            if (it == false) {
                myPageViewModel.fetchMyPageInfo()
            }
        }
        bindAction<Boolean>(HONOR_MEDAL_CHANGED) {
            myPageViewModel.fetchMyPageInfo()
        }
        bindAction<String>(CHANG_NICKNAME) {
            if (!it.isNullOrEmpty()) {
                myPageViewModel.changeNickname(it)
                adapter.notifyItemChanged(0)
            }
        }
    }

    private fun showBottomLiveLabel(item: Any?) {
        val startLiveItem = item as? MyPageInfo.MeTabItem
        if (startLiveItem == null) {
            studio_live.isGone = true
            return
        }
        studio_live.isVisible = true
        studio_live_text.text = startLiveItem.name?.text
        studio_live.setOnClickListener {
            if (!startLiveItem.needLogin || context.checkLogin()) {
                Nav.toAmanLink(context, startLiveItem.action.orEmpty())
            }
        }
    }

    override fun willAppear() {
        myPageViewModel.fetchMyPageInfo()
    }


    override fun netChange(netInfo: NetInfo) {
        /** 如果网络从不可用变成可用 **/
        if (isAdded && netInfo.netChangeToAvailable()) {
            myPageViewModel.fetchMyPageInfo()
        }
    }

    override fun onSelect() {
        GrowingUtil.track("main_page_view",
                "screen_name", "我的",
                "main_app_gn_pro", MeiUser.getSharedUser().info?.interests.orEmpty().joinToString { it.name },
                "main_app_gn_type", "用户资料",
                "time_stamp", (System.currentTimeMillis()/ com.mei.orc.unit.TimeUnit.SECOND).toString())
    }

    override fun onDeSelect() {
    }

    override fun onReSelect() {
    }
}