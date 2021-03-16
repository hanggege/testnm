package com.mei.intimate

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.listener.OnTabSelectListener
import com.mei.GrowingUtil
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.common.SHORT_VIDEO_LIST_TAB_CHANGE
import com.mei.base.weight.fragmentpager.CustomFragmentPagerAdapter
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.bindAction
import com.mei.orc.util.json.toJson
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.chick.video.ShortVideoList
import com.net.network.chick.video.ShortVideoListRequest
import kotlinx.android.synthetic.main.activity_short_video_list.*
import launcher.Boom
import launcher.MulField

/**
 * Created by hang on 2020/7/14.
 */
class ShortVideoListActivity : MeiCustomActivity() {

    @Boom
    @MulField
    var tagId = 0

    private var mData: List<ShortVideoList.VideoLabel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_short_video_list)
        ActivityLauncher.bind(this)

        initListener()

        requestData()

        back_choice.setOnClickListener { finish() }
        empty_view.setEmptyImage(R.drawable.home_error_page)
                .setEmptyText("网络开小差了")
                .setBtnVisible(true)
                .setBtnClickListener(View.OnClickListener {
                    requestData()
                })

        bindAction<Boolean>(CHANG_LOGIN) {
            requestData()
        }
        bindAction<Int>(SHORT_VIDEO_LIST_TAB_CHANGE) { targetTagId ->
            var tabIndex = 0
            mData?.forEachIndexed { index, cate ->
                if (targetTagId == cate.tagId) {
                    tabIndex = index
                    return@forEachIndexed
                }
            }
            slide_tab_layout.currentTab = tabIndex
        }
    }

    private fun requestData() {
        showLoading(true)
        apiSpiceMgr.executeToastKt(ShortVideoListRequest(0, 1), success = {
            if (!it.data?.tags.isNullOrEmpty()) {
                notifyFragmentList(it.data.tags, it.data.videos.toJson().orEmpty())
                empty_view.isVisible = false
            } else {
                empty_view.isVisible = true
            }
        }, failure = {
            empty_view.isVisible = true
        }, finish = {
            showLoading(false)
        })
    }


    private fun notifyFragmentList(cateList: List<ShortVideoList.VideoLabel>, jsonStr: String) {
        mData = cateList
        var firstIndex = 0
        val fragmentList = arrayListOf<CustomSupportFragment>()
        cateList.forEachIndexed { index, cate ->
            if (index == 0) {
                fragmentList.add(ShortVideoListFragmentLauncher.newInstance(cate.tagId, jsonStr, "标签列表页"))
            } else {
                fragmentList.add(ShortVideoListFragmentLauncher.newInstance(cate.tagId, "", "标签列表页"))
            }
            if (tagId == cate.tagId) {
                firstIndex = index
            }
        }
        view_pager.adapter = CustomFragmentPagerAdapter(supportFragmentManager).apply {
            setFragmentsAndTitles(fragmentList, cateList.map { it.name })
        }
        view_pager.offscreenPageLimit = fragmentList.size
        slide_tab_layout.setViewPager(view_pager)
        slide_tab_layout.currentTab = firstIndex
    }

    private fun initListener() {
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                GrowingUtil.track("main_gn_entrance_view",
                        "screen_name", "标签列表页",
                        "main_app_gn_type", "短视频",
                        "position", "",
                        "main_app_gn_label", view_pager.adapter?.getPageTitle(position).toString())
            }
        })
        slide_tab_layout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabReselect(position: Int) {}

            override fun onTabSelect(position: Int) {
                GrowingUtil.track("main_gn_entrance_view",
                        "screen_name", "标签列表页",
                        "main_app_gn_type", "短视频",
                        "position", "",
                        "main_app_gn_label", view_pager.adapter?.getPageTitle(position).toString())
            }

        })
    }
}