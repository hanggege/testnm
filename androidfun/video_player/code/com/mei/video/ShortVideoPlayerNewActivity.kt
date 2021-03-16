package com.mei.video

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.postDelayed
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.mei.base.common.DRAWER_LAYOUT_STATE
import com.mei.base.common.FOLLOW_USER_STATE
import com.mei.base.common.SHORT_VIDEO_FIRST_EXIT_DETAIL
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.bindAction
import com.mei.video.browser.adapter.MentorShortVideoAdapter
import com.mei.video.browser.view.showGuideGesture
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.broadcast.PlayType
import com.net.model.chick.video.MentorShortVideo
import com.net.model.chick.video.ShortVideoInfo
import com.pili.getPlayInfo
import com.pili.player
import kotlinx.android.synthetic.main.short_video_player_new_layout.*
import launcher.Boom
import launcher.MakeResult
import launcher.MulField

/**
 *
 * @author Created by lenna on 2020/8/17
 */
@MakeResult(includeStartForResult = true)
class ShortVideoPlayerNewActivity : MeiCustomActivity() {

    @Boom
    var videoId: String = ""


    @Boom
    @MulField
    var tagId: String = ""

    @Boom
    @MulField
    var isMyLike: Boolean = false

    @Boom
    @MulField
    var isAllTag: Boolean = false

    @Boom
    @MulField
    var videoCoverUrl: String = ""

    @Boom
    @MulField
    var fromType: String = ""

    @Boom
    @MulField
    var publisherId: String = ""

    var resumeTime = 0L
    var completionTime = 0L
    var videoInfoList = arrayListOf<ShortVideoInfo.VideoInfo>()
    var mMentorShortVideo: MentorShortVideo = MentorShortVideo()
    var mentorVideoData = arrayListOf<Any>()
    var mIsExitDetail: Boolean = false
    var isLoadingMore = false
    var hasMoreData = true
    val mentorVideoAdapter by lazy {
        MentorShortVideoAdapter(mentorVideoData).apply {
            loadMoreModule.setOnLoadMoreListener {
                /**上滑知心人视频时加载更多数据*/
                val targetPosition = short_video_browser.currentPosition % short_video_browser.data.size
                if (targetPosition.checkPosition()) {
                    loadMoreVideos(short_video_browser.data[targetPosition])
                }
            }
            setOnItemClickListener { _, _, position ->
                if (mentorVideoData[position] is ShortVideoInfo.VideoInfo) {
                    /**切换视频播放*/
                    videoId = (mentorVideoData[position] as ShortVideoInfo.VideoInfo).seqId.orEmpty()
                    publisherId = (mentorVideoData[position] as ShortVideoInfo.VideoInfo).userId.toString()
                    videoCoverUrl = (mentorVideoData[position] as ShortVideoInfo.VideoInfo).videoCoverUrl
                    /**重置tagId，只请求当前用户的*/
                    tagId = ""
                    fromType = "detail"
                    requestVideoRequest()
                    container_view.closeDrawers()
                }
            }
        }
    }


    override fun customStatusBar(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.short_video_player_new_layout)
        ActivityLauncher.bind(this)
        initView()
        requestVideoRequest()
        if (getPlayInfo().mPlayType != PlayType.NONE)
            player.pause()
        initEvent()
    }

    override fun onBackPressed() {
        showExitShortVideoDetail()
    }

    override fun onResume() {
        super.onResume()
        resumeTime = System.currentTimeMillis()
    }

    private fun initEvent() {
        bindAction<Pair<Int, Boolean>>(FOLLOW_USER_STATE) {
            videoInfoList.filter { data -> data.user.userId == it?.first }
                    .forEach { info ->
                        info.subscribe = it?.second ?: true
                    }
        }
        bindAction<Boolean>(SHORT_VIDEO_FIRST_EXIT_DETAIL) {
            mIsExitDetail = true
            //播放下一条
            short_video_browser.scrollToPositionNextOrReplay()
        }
        bindAction<Boolean>(DRAWER_LAYOUT_STATE) {
            if (it == true) {
                container_view.openDrawer(navigation_view)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        reportShortVideo(short_video_browser.getCurrentItemData()?.seqId.toString())
    }


    private fun initView() {
        ImmersionBar.with(this).apply {
            statusBarDarkFont(false)
            statusBarColorInt(Color.BLACK)
            transparentStatusBar()
            statusBarView(status_bar_view)
        }.init()
        top_back.setOnClickListener { onBackPressed() }
        net_error_view.setOnClickListener { requestVideoRequest() }
        short_video_refresh.setOnRefreshListener { requestVideoRequest() }
        mentor_short_video_net_error_view.setOnClickListener {
            val targetPosition = short_video_browser.currentPosition % short_video_browser.data.size
            if (targetPosition.checkPosition()) {
                getMentorVideos(short_video_browser.data[targetPosition])
            }
        }
        short_video_browser.setOnViewPagerListener { oldPosition, newPosition, _ ->
            val targetPosition = newPosition % short_video_browser.data.size
            val targetOldPosition = oldPosition % short_video_browser.data.size
            if (targetPosition.checkPosition()) {
                getMentorVideos(short_video_browser.data[targetPosition])
            }
            statUpGlide("detail", if (targetPosition - targetOldPosition == 1) "上滑" else "下滑")
            if (targetPosition >= short_video_browser.data.size - 3 && !isLoadingMore) {
                if (hasMoreData) {
                    /**上滑视频时加载更多数据*/
                    loadMoreShortVideoList()
                } else {
                    isLoadingMore = true
                    /** 等待加循环数据 **/
                    short_video_browser.postDelayed(1500) {
                        short_video_browser.addDataList(videoInfoList)
                        isLoadingMore = false
                    }
                }
            }

            /**切换视频统计观看时长*/
            if (targetOldPosition.checkPosition()) {
                completionTime = System.currentTimeMillis() - 500
                reportShortVideo(short_video_browser.data[targetOldPosition].seqId.orEmpty())
            }
            (container_view.parent as? ViewGroup)?.showGuideGesture()
        }
        container_view.doOnPreDraw {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                it.systemGestureExclusionRects = listOf()
            }
        }
        container_view.doOnNextLayout {
            container_view.setDrawerRightEdgeSize(this@ShortVideoPlayerNewActivity, 0.4f)
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
                statLiftSlip("detail")
            }

        })
        mentor_short_video_rlv.layoutManager = GridLayoutManager(this, 6)
        mentor_short_video_rlv.adapter = mentorVideoAdapter

    }

    private fun Int.checkPosition(): Boolean = this in 0 until short_video_browser.data.size


}