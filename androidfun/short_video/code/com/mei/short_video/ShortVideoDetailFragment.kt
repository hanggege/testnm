package com.mei.short_video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import com.mei.base.common.*
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.tab.TabItemContent
import com.mei.short_video.ext.*
import com.mei.video.browser.view.showGuideGesture
import com.mei.video.statUpGlide
import com.mei.wood.R
import com.mei.wood.ui.CustomSupportFragment
import com.net.model.broadcast.PlayType
import com.net.model.chick.video.ShortVideoInfo
import com.pili.getPlayInfo
import com.pili.player
import kotlinx.android.synthetic.main.short_video_detail_fragment.*

/**
 *
 * @author Created by lenna on 2020/9/1
 * 短视频详情页，主页运用于显示主播上传的短视频，可以无限循环滑动
 */
class ShortVideoDetailFragment : CustomSupportFragment(), TabItemContent {
    private var isLoginChange: Boolean = false
    private val refreshTag = "REFRESH"

    var nextStartKey: String? = null
    var resumeTime = 0L
    var completionTime = 0L
    var videoInfoList = arrayListOf<ShortVideoInfo.VideoInfo>()
    var isLoadingMore = false
    var hasMoreData = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.short_video_detail_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        requestVideoRequest()

    }

    private fun initEvent() {
        bindAction<Pair<Int, Boolean>>(FOLLOW_USER_STATE) {
            videoInfoList.filter { data -> data.user.userId == it?.first }
                    .forEach { info ->
                        info.subscribe = it?.second ?: true
                    }
        }
        bindAction<Boolean>(SHORT_VIDEO_FIRST_EXIT_DETAIL) {
            //播放下一条
            if (isAdded)
                short_video_browser.scrollToPositionNextOrReplay()
        }

        bindAction<Boolean>(CHANG_LOGIN) {
            /**登录状态发生变化时我们记住当前的状态，当回到这个页面时才做相应的请求*/
            isLoginChange = true
        }
        bindAction<String>(SHORT_VIDEO_COMPLETION) {
            shortVideoCompletion(it.orEmpty())
        }
        bindAction<Boolean>(VIDEO_RENDERING_START) {
            /**监听这个通知目的是，当我们退出页面视频还在加载中，当视频加载完成时我们已经不在当前页面了，
             * 应该立马把视频暂停，回到当前页面再进行播放*/
            if (!isAppear) {
                short_video_browser.pauseVideo()
            }
        }

    }

    private fun initView() {
        short_video_refresh_layout.setPadding(0, getStatusBarHeight(), 0, 0)
        net_error_view.setOnClickListener {
            nextStartKey = ""
            requestVideoRequest()
        }
        short_video_refresh_layout.setOnRefreshListener {
            if (isAdded) {
                refreshShortVideos()
            }
        }
        short_video_browser.setOnViewPagerListener { oldPosition, newPosition, _ ->
            val targetPosition = newPosition % short_video_browser.data.size
            val targetOldPosition = oldPosition % short_video_browser.data.size
            if (targetPosition.checkPosition()) {
                postAction(SHORT_SWITCH_CHANGED, short_video_browser.data[targetPosition])
                shortVideoPlaying(short_video_browser.data[targetPosition].seqId)
            }
            statUpGlide("recommend", if (targetPosition - targetOldPosition == 1) "上滑" else "下滑")
            if (targetPosition >= short_video_browser.data.size - 3 && !isLoadingMore) {
                if (hasMoreData) {
                    /**上滑视频时加载更多数据*/
                    loadMoreShortVideoList()
                } else {
                    isLoadingMore = true
                    /** 等待加循环数据 **/
                    short_video_browser.postDelayed(1500) {
                        if (isAdded) {
                            short_video_browser.addDataList(videoInfoList)
                            isLoadingMore = false
                        }
                    }
                }
            }

            /**切换视频统计观看时长*/
            if (targetOldPosition.checkPosition()) {
                completionTime = System.currentTimeMillis() - 500
                reportShortVideo(short_video_browser.data[targetOldPosition].seqId.orEmpty())
            }

            if (isAppear && newPosition != 0 && isAdded) {
                activity?.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)?.showGuideGesture()
            }

        }

    }

    private fun loginStateChange() {
        nextStartKey = refreshTag
        requestVideoRequest()
    }

    fun refreshShortVideos() {
        nextStartKey = refreshTag
        requestVideoRequest()
    }


    private fun Int.checkPosition(): Boolean = this in 0 until short_video_browser.data.size


    var isAppear = false
    fun willAppear() {
        if (isAdded) {
            isAppear = true
            activity?.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)?.showGuideGesture()
            //显示重新请求
            if (getPlayInfo().mPlayType != PlayType.NONE) {
                player.pause()
            }
            resumeTime = System.currentTimeMillis()
            if (short_video_browser.isVisible) {
                short_video_browser.playVideo()
            }
            if (isLoginChange) {
                loginStateChange()
                isLoginChange = false
            }
        }
    }

    fun willHidden() {
        if (isAdded) {
            isAppear = false
            short_video_browser.pauseVideo()
            reportShortVideo(short_video_browser.getCurrentItemData()?.seqId.orEmpty())
        }
    }

    override fun onSelect() {
    }

    override fun onReSelect() {
    }

    override fun onDeSelect() {
    }
}