package com.mei.short_video

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.listener.OnTabSelectListener
import com.mei.base.common.DRAWER_LAYOUT_STATE
import com.mei.base.common.SHORT_SWITCH_CHANGED
import com.mei.base.common.SHORT_VIDEO_LIST_TAB_CHANGE
import com.mei.base.common.SHORT_VIDEO_TAG_CLICK
import com.mei.base.weight.fragmentpager.CustomFragmentPagerAdapter
import com.mei.base.weight.tablayout.CustomTabLayout
import com.mei.login.checkLogin
import com.mei.login.toLogin
import com.mei.orc.Cxt
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.tab.TabItemContent
import com.mei.short_video.ext.getMentorVideos
import com.mei.short_video.ext.loadMoreVideos
import com.mei.short_video.ext.startShortVideoPreView
import com.mei.video.ShortVideoPlayerNewActivityLauncher
import com.mei.video.browser.adapter.MentorShortVideoAdapter
import com.mei.video.setDrawerRightEdgeSize
import com.mei.video.statLiftSlip
import com.mei.video.statShowRightView
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.ui.fragment.TabItemFragment
import com.net.model.chick.video.MentorShortVideo
import com.net.model.chick.video.ShortVideoInfo
import com.net.model.chick.video.ShortVideoTab
import com.net.network.chick.video.ShortVideoTabRequest
import kotlinx.android.synthetic.main.short_video_tab_fragment.*


/**
 *
 * @author Created by lenna on 2020/9/1
 * 短视频推荐tab页面
 */
class ShortVideoTabFragment : TabItemFragment(), TabItemContent {
    private val topBarTitles = arrayListOf<String>()
    val fragmentList = arrayListOf<CustomSupportFragment>()
    var mMentorShortVideo: MentorShortVideo = MentorShortVideo()
    var mentorVideoData = arrayListOf<Any>()
    var shortVideoInfo: ShortVideoInfo.VideoInfo? = null
    var tabData: ShortVideoTab? = null
    var tabStrId: String = ""
    var tagsId: Int = 0
    val mentorVideoAdapter by lazy {
        MentorShortVideoAdapter(mentorVideoData).apply {
            loadMoreModule.setOnLoadMoreListener {
                /**上滑知心人视频时加载更多数据*/
                loadMoreVideos(shortVideoInfo)
            }
            setOnItemClickListener { _, _, position ->
                if (!context.checkLogin()) {
                    context.toLogin()
                } else {
                    if (mentorVideoData[position] is ShortVideoInfo.VideoInfo) {
                        /**切换视频播放*/
                        val info = (mentorVideoData[position] as ShortVideoInfo.VideoInfo)
                        val videoId = info.seqId.orEmpty()
                        val publisherId = info.userId.toString()

                        /**重置tagId，只请求当前用户的*/
                        val fromType = "detail"
                        ShortVideoPlayerNewActivityLauncher.startActivity(activity
                                , videoId
                                , ""
                                , false
                                , false
                                , info.videoCoverUrl
                                , fromType
                                , publisherId)
                        container_view.closeDrawers()
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.short_video_tab_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isAppear = true
        initView()
        requestData()
        bindAction<Boolean>(DRAWER_LAYOUT_STATE) {
            if (it == true && isAdded) {
                container_view.openDrawer(navigation_view)
            }
        }
        bindAction<ShortVideoInfo.VideoInfo>(SHORT_SWITCH_CHANGED) {
            shortVideoInfo = it
            getMentorVideos(shortVideoInfo)
        }
        bindAction<Int>(SHORT_VIDEO_TAG_CLICK) {
            short_video_tab_vp.setCurrentItem(fragmentList.indexOfFirst { fragment -> fragment is SquareTabFragment }, true)
            postAction(SHORT_VIDEO_LIST_TAB_CHANGE, it)
        }
    }

    private fun initView() {
        square_tab_back.isVisible = activity is SquareOrRecommendActivity
        square_tab_back.setOnClickListener {
            activity?.finish()
        }
        short_video_tab_empty_view.setBtnClickListener(View.OnClickListener {
            requestData()
        })
        mentor_short_video_net_error_view.setOnClickListener {
            getMentorVideos(shortVideoInfo)
        }
        container_view.doOnNextLayout {
            container_view.setDrawerRightEdgeSize(activity, 0.4f)
        }
        container_view.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerOpened(drawerView: View) {
                /**打开侧边栏停止引导动画并回收*/
                statShowRightView()
                statLiftSlip("recommend")
            }

        })
        mentor_short_video_rlv.layoutManager = GridLayoutManager(activity, 6)
        mentor_short_video_rlv.adapter = mentorVideoAdapter
    }

    private fun requestData() {
        showLoading(true)
        apiSpiceMgr.executeKt(ShortVideoTabRequest(), success = {
            if (isAdded) {
                if (it.data?.tabs.orEmpty().isNotEmpty()) {
                    tabData = it.data
                    loadTabData(it.data)
                    short_video_tab_empty_view.isVisible = false
                } else {
                    short_video_tab_empty_view.isVisible = true
                }
            }
        }, failure = {
            short_video_tab_empty_view.isVisible = true
        }, finish = { showLoading(false) })
    }

    /**
     * 推荐的index
     */
    private var indexOfRecommend = -1

    private fun loadTabData(data: ShortVideoTab?) {
        var selectTabIndex = 0
        fragmentList.clear()
        topBarTitles.clear()
        data?.tabs?.forEachIndexed { index, tabInfo ->
            topBarTitles.add(tabInfo.name)
            val defaultTab = if (tabStrId.isNotEmpty()) tabStrId else data.defaultTab.orEmpty()
            if (defaultTab == tabInfo.id) {
                selectTabIndex = index
            }
            if (tabInfo.id == ShortTab.SQUARE.name) {
                //广场
                val squareTabFragment = SquareTabFragment().apply { tagId = tagsId }
                fragmentList.add(squareTabFragment)
            } else if (tabInfo.id == ShortTab.RECOMMEND.name) {
                //推荐
                fragmentList.add(ShortVideoDetailFragment())
            }

        }

        short_video_tab_vp.adapter = CustomFragmentPagerAdapter(childFragmentManager).apply {
            setFragmentsAndTitles(fragmentList, topBarTitles)
        }
        short_video_tab_vp.offscreenPageLimit = fragmentList.size
        short_video_tab.apply {
            updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = getStatusBarHeight()
            }
            setViewPager(short_video_tab_vp)
            currentTab = selectTabIndex
            setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                }

                override fun onTabReselect(position: Int) {
                    /**重复点击推荐刷新短视频数据*/
                    if (position == indexOfRecommend) {
                        val shortVideoDetailFragment = fragmentList.getOrNull(indexOfRecommend) as? ShortVideoDetailFragment
                        shortVideoDetailFragment?.refreshShortVideos()
                    }
                }
            })
            if (data?.tabs?.getIndexOrNull(selectTabIndex)?.id == ShortTab.RECOMMEND.name) {
                setTabWhiteTheme(currentTab)
            } else {
                setTabDarkFontTheme(currentTab)
            }
        }

        indexOfRecommend = data?.tabs.orEmpty().indexOfFirst { it.id == ShortTab.RECOMMEND.name }


        navigation_view.updateLayoutParams<ViewGroup.LayoutParams> {
            width = dip(if (selectTabIndex == 0) 0 else 265)
        }

        intercept_layout.setIsIntercept(selectTabIndex == short_video_tab_vp.childCount - 1)

        if (isAppear) {
            if (selectTabIndex == 1) {
                (fragmentList.getOrNull(1) as? ShortVideoDetailFragment)?.willAppear()
            }
            startShortVideoPreView()
        }

        short_video_tab_vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                intercept_layout.setIsIntercept(position == short_video_tab_vp.childCount - 1)

                val shortVideoDetailFragment = fragmentList.getOrNull(indexOfRecommend) as? ShortVideoDetailFragment
                if (indexOfRecommend == position) {
                    shortVideoDetailFragment?.willAppear()

                    short_video_tab.setTabWhiteTheme(position)

                    navigation_view.updateLayoutParams<ViewGroup.LayoutParams> {
                        width = dip(265)
                    }
                } else {
                    shortVideoDetailFragment?.willHidden()

                    short_video_tab.setTabDarkFontTheme(position)
                    navigation_view.updateLayoutParams<ViewGroup.LayoutParams> {
                        width = dip(0)
                    }
                }
                startShortVideoPreView()
            }
        })

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun CustomTabLayout.setTabDarkFontTheme(position: Int) {
        currentTab = position
        setTextSelectSize(21f)
        textsize = 16f
        indicatorColor = Cxt.getColor(R.color.color_333333)
        textSelectColor = Cxt.getColor(R.color.color_333333)
        textUnselectColor = Cxt.getColor(R.color.color_333333)
        square_tab_back.setImageDrawable(context.getDrawable(R.drawable.bg_black_back_arrow))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun CustomTabLayout.setTabWhiteTheme(position: Int) {
        currentTab = position
        setTextSelectSize(21f)
        textsize = 16f
        indicatorColor = Cxt.getColor(android.R.color.white)
        textUnselectColor = Cxt.getColor(android.R.color.white)
        textSelectColor = Cxt.getColor(android.R.color.white)
        square_tab_back.setImageDrawable(context.getDrawable(R.drawable.bg_white_back_arrow))
    }

    private var isAppear = false

    override fun willAppear() {
        super.willAppear()
        isAppear = true
        if (short_video_tab_vp.currentItem == indexOfRecommend) {
            (fragmentList.getOrNull(indexOfRecommend) as? ShortVideoDetailFragment)?.willAppear()
        }
        startShortVideoPreView()
    }

    override fun willHidden() {
        super.willHidden()
        isAppear = false
        if (short_video_tab_vp.currentItem == indexOfRecommend) {
            (fragmentList.getOrNull(indexOfRecommend) as? ShortVideoDetailFragment)?.willHidden()
        }
    }

    override fun onSelect() {

    }

    override fun onReSelect() {

    }

    override fun onDeSelect() {

    }
}