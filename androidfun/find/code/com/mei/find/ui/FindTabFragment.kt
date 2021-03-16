package com.mei.find.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.listener.OnTabSelectListener
import com.mei.GrowingUtil
import com.mei.find.adapter.FindTabAdapter1
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.tab.TabItemContent
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.ui.fragment.TabItemFragment
import com.net.MeiUser
import com.net.model.chick.find.FindTab
import com.net.model.chick.tab.TabItem
import com.net.network.chick.find.FindTabRequest
import kotlinx.android.synthetic.main.find_tab_layout.*

/**
 * FindFragment
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-21
 */
class FindTabFragment : TabItemFragment(), TabItemContent {

    private var topBarTitles = arrayListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.find_tab_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        requestData()
    }

    override fun willAppear() {
        super.willAppear()
        if (topBarTitles.isNotEmpty()) {
            GrowingUtil.track("find_page_view",
                    "page_type", "${topBarTitles[find_tab_layout.currentTab]}页",
                    "view_type", "二屏发现页",
                    "click_module", "",
                    "click_content", "")
        }
    }

    private fun requestData() {
        showLoading(true)
        apiSpiceMgr.executeKt(FindTabRequest(), success = {
            if (it.data?.tab.orEmpty().isNotEmpty()) {
                notifyTitleAndContent(it.data.tab)
                find_empty_view.isVisible = false
            } else {
                find_empty_view.isVisible = true
            }
        }, failure = {
            find_empty_view.isVisible = true
        }, finish = { showLoading(false) })
    }


    private fun notifyTitleAndContent(infoList: List<FindTab.TabInfo>) {
        val fragmentList = arrayListOf<CustomSupportFragment>()
        var selectTabIndex = 0
        infoList.forEachIndexed { index, findTabInfo ->
            topBarTitles.add(findTabInfo.name)
            val item = TabItem(findTabInfo.code, findTabInfo.name, findTabInfo.url, findTabInfo.name)
            if (index == 0) {
                fragmentList.add(FindServiceFragmentLauncher.newInstance(item))
            } else {
                fragmentList.add(FindAdvisoryFragmentLauncher.newInstance(item))
            }
            if (findTabInfo.active) {
                item.needLoadFirst = 1
                selectTabIndex = index
            }
        }

        find_view_pager.adapter = FindTabAdapter1(requireActivity(), fragmentList)
        find_view_pager.offscreenPageLimit = fragmentList.size
        find_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                val iFindTabContent: IFindTabContent = if (position==0) {
                    fragmentList[0] as FindServiceFragment
                } else {
                    fragmentList[position] as FindAdvisoryFragment
                }
                if (position == 0) {
                    GrowingUtil.track("find_page_view",
                            "page_type", "${topBarTitles[position]}页",
                            "main_app_gn_pro", iFindTabContent.getContentTab(),
                            "view_type", "二屏发现页",
                            "click_module", "",
                            "click_content", "")
                } else {
                    GrowingUtil.track("find_page_view",
                            "page_type", "${topBarTitles[position]}页",
                            "view_type", "二屏发现页",
                            "click_module", "",
                            "click_content", "")
                }
                iFindTabContent.tabChanged()
            }
        })
        if (fragmentList.size > 0) {
            (fragmentList[0] as FindServiceFragment).tabChanged()
        }

        find_tab_layout.apply {
            setViewPager(find_view_pager, topBarTitles.toTypedArray())
            currentTab = selectTabIndex
        }
        find_tab_layout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                GrowingUtil.track("consult_page_click",
                        "main_app_gn_pro", "${topBarTitles[position]}页",
                        "page_type", "服务页",
                        "view_type", "二屏发现页",
                        "click_module", "品类分区",
                        "click_content", "")
            }

            override fun onTabReselect(position: Int) {

            }
        })
    }

    private fun initView() {
        view?.updatePadding(top = getStatusBarHeight())
        find_empty_view.setEmptyText("网络开小差了")
                .setBtnClickListener(View.OnClickListener {
                    requestData()
                })
    }

    override fun onSelect() {
        GrowingUtil.track("main_page_view",
                "screen_name", "发现",
                "main_app_gn_pro", MeiUser.getSharedUser().info?.interests.orEmpty().joinToString { it.name },
                "main_app_gn_type", "发现",
                "time_stamp", (System.currentTimeMillis()/ com.mei.orc.unit.TimeUnit.SECOND).toString())
    }

    override fun onReSelect() {

    }

    override fun onDeSelect() {

    }

}