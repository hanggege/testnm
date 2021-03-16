package com.mei.short_video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.viewpager.widget.ViewPager
import com.mei.GrowingUtil
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.common.SHORT_VIDEO_LIST_TAB_CHANGE
import com.mei.base.weight.fragmentpager.CustomFragmentPagerAdapter
import com.mei.intimate.ShortVideoListFragmentLauncher
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.util.json.toJson
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.ui.fragment.TabItemFragment
import com.net.model.chick.video.ShortVideoList
import com.net.network.chick.video.ShortVideoListRequest
import kotlinx.android.synthetic.main.square_fragment.*
import launcher.Boom
import launcher.MulField

/**
 *
 * @author Created by lenna on 2020/9/1
 * 广场tab
 */
class SquareTabFragment : TabItemFragment() {
    @Boom
    @MulField
    var tagId = 0

    fun getCurrentTag(): String? {
        return if (isAdded) {
            mData?.getOrNull(slide_tab_layout.currentTab)?.name?.toString().orEmpty()
        } else {
            ""
        }
    }

    private var mData: List<ShortVideoList.VideoLabel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.square_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ActivityLauncher.bind(this)

        slide_tab_layout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = getStatusBarHeight() + dip(44)
        }
        initListener()

        requestData()

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
                fragmentList.add(ShortVideoListFragmentLauncher.newInstance(cate.tagId, jsonStr, "广场"))
            } else {
                fragmentList.add(ShortVideoListFragmentLauncher.newInstance(cate.tagId, "", "广场"))
            }
            if (tagId == cate.tagId) {
                firstIndex = index
            }
        }
        view_pager.adapter = CustomFragmentPagerAdapter(childFragmentManager).apply {
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
                GrowingUtil.track("main_page_view",
                        "screen_name", "短视频页",
                        "main_app_gn_type", "短视频",
                        "main_app_gn_pro", view_pager.adapter?.getPageTitle(position).toString(),
                        "main_app_gn_label", "广场",
                        "time_stamp", (System.currentTimeMillis()/ com.mei.orc.unit.TimeUnit.SECOND).toString())
            }
        })
    }


}