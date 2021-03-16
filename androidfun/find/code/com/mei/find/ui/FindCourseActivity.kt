package com.mei.find.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.flyco.tablayout.listener.OnTabSelectListener
import com.mei.find.adapter.FindTabAdapter1
import com.mei.orc.ActivityLauncher
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.parseValue
import com.net.model.chick.find.FindCourseTab
import com.net.model.chick.tab.TabItem
import com.net.network.chick.find.FindCourseTabRequest
import kotlinx.android.synthetic.main.activity_find_course_tab.*
import launcher.Boom

/**
 * FindCourseActivity
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-23
 */
class FindCourseActivity : MeiCustomActivity() {

    @Boom
    var partitionId = 0

    var type = "advisory"

    var suffix = ""
    var curContentTabIndex = 0

    var tabIndex: Int = 0

    private var topBarTitles = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_course_tab)
        ActivityLauncher.bind(this)

        intent.data?.toString()?.apply {
            type = parseValue("type")
            curContentTabIndex = parseValue("tabIndex").toInt()
            suffix = "&tabIndex=" + parseValue("tabIndex")
            val tabIdValue = parseValue("partitionId")
            partitionId = if (tabIdValue.isNotEmpty()) tabIdValue.toInt() else tabIndex
        }

        initView()
        requestData()
    }

    private fun initView() {
        find_course_empty_layout.setEmptyText("网络开小差了")
                .setBtnClickListener(View.OnClickListener {
                    requestData()
                })
        find_course_arrow_back.setOnClickListener { finish() }
    }

    private fun requestData() {
        showLoading(true)
        apiSpiceMgr.executeKt(FindCourseTabRequest(type), success = {
            if (!it.data?.partitions.isNullOrEmpty()) {
                notifyTitleAndContent(it.data.partitions)
                find_course_empty_layout.isVisible = false
            } else {
                find_course_empty_layout.isVisible = true
            }
        }, failure = {
            find_course_empty_layout.isVisible = true
        }, finish = { showLoading(false) })
    }

    private fun notifyTitleAndContent(tabs: List<FindCourseTab.TabInfo>) {
        val fragmentList = arrayListOf<FindCourseFragment>()
        tabs.forEachIndexed { index, findTabInfo ->
            topBarTitles.add(findTabInfo.partitionName)
            val tabItem = TabItem(findTabInfo.partitionId.toString(), findTabInfo.partitionName, findTabInfo.url + suffix + "&partitionName=${findTabInfo.partitionName}", findTabInfo.partitionName)
            if (findTabInfo.partitionId == partitionId) {
                tabIndex = index
                tabItem.needLoadFirst = 1
            }
            fragmentList.add(FindCourseFragmentLauncher.newInstance(tabItem))
        }

        find_course_view_pager.adapter = FindTabAdapter1(this, fragmentList)
        find_course_view_pager.offscreenPageLimit = fragmentList.size

        find_course_tab_layout.apply {
            setViewPager(find_course_view_pager, topBarTitles.toTypedArray())
            currentTab = tabIndex
        }
        find_course_tab_layout.setOnTabSelectListener(object : OnTabSelectListener{
            override fun onTabSelect(position: Int) {
                fragmentList[position].eventTracking(topBarTitles[position])
            }

            override fun onTabReselect(position: Int) {

            }
        })
    }

    fun modifiedFragmentContentLink(indexStr: String) {
        curContentTabIndex = indexStr.toInt()
        val mFindTabAdapter = find_course_view_pager.adapter as FindTabAdapter1
        mFindTabAdapter.fragments.forEach {
            if (it is FindCourseFragment) {
                it.item?.let { item ->
                    val tabIndex = item.link.indexOf("&tabIndex=") + 10
                    if (indexStr != item.link[tabIndex].toString()) {
                        val builder = StringBuilder(item.link)
                        builder.replace(tabIndex, tabIndex + 1, indexStr)
                        item.link = builder.toString()
                    }
                }
                selectContentTab(it)
            }
        }
    }

    private fun selectContentTab(fragment: FindCourseFragment) {
        fragment.selectContentTab(curContentTabIndex)
    }

}