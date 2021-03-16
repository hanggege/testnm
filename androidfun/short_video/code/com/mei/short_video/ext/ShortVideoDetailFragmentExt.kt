package com.mei.short_video.ext

import androidx.core.view.isVisible
import com.mei.GrowingUtil
import com.mei.base.common.SHORT_SWITCH_CHANGED
import com.mei.base.network.netchange.isWIFIConnected
import com.mei.init.spiceHolder
import com.mei.orc.event.postAction
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.date.formatToString
import com.mei.orc.util.date.isToday
import com.mei.orc.util.save.getNonValue
import com.mei.orc.util.save.putValue
import com.mei.short_video.ShortVideoDetailFragment
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.net.network.chick.video.RecommendShortVideoPlayingRequest
import com.net.network.chick.video.ShortVideoCompletionRequest
import com.net.network.chick.video.ShortVideoRecommendRequest
import com.net.network.chick.video.ShortVideoReportRequest
import kotlinx.android.synthetic.main.short_video_detail_fragment.*
import java.util.*

/**
 *
 * @author Created by lenna on 2020/9/1
 * 短视频详情扩展文件
 */
fun ShortVideoDetailFragment.requestVideoRequest() {
    showLoading(true)
    net_error_view.isVisible = false
    /**重置播放时间统计*/
    completionTime = System.currentTimeMillis()
    apiSpiceMgr.executeToastKt(ShortVideoRecommendRequest(nextStartKey.orEmpty()), success = {
        if (it.isSuccess && it.data != null) {
            net_error_view.isVisible = false
            short_video_browser.isVisible = true
            videoInfoList.clear()
            it.data?.current?.let { current ->
                videoInfoList.add(current)
                postAction(SHORT_SWITCH_CHANGED, current)
                shortVideoPlaying(current.seqId)
            }
            it.data?.next?.let { next -> videoInfoList.addAll(next) }

            resetBrowser()
            val isToday = "is_today_first_show".getNonValue("").isToday()
            if (activity?.isWIFIConnected() != true && !isToday) {
                "is_today_first_show".putValue(Date().formatToString())
                UIToast.toast(activity, "非wifi网络，请注意流量消耗")
            }
            nextStartKey = it.data.nextStartKey.orEmpty()
            /**后端有可能回无短视频数据的情况，视频没有数据就显示下面错误页面*/
            if (videoInfoList.size == 0) {
                short_video_browser.isVisible = false
                net_error_view.isVisible = true
                short_video_browser.pauseVideo()

            }

        } else {
            if (videoInfoList.size == 0) {
                short_video_browser.isVisible = false
                net_error_view.isVisible = true
                short_video_browser.pauseVideo()

            }
            UIToast.toast(activity, it?.msg)
        }
    }, failure = {
        if (videoInfoList.size == 0) {
            short_video_browser.isVisible = false
            net_error_view.isVisible = true
            short_video_browser.pauseVideo()
        }
        UIToast.toast(activity, it?.currMessage)
    }, finish = {
        showLoading(false)
        short_video_refresh_layout.isRefreshing = false
    })

}


fun ShortVideoDetailFragment.resetBrowser() {
    if (isAdded) {
        hasMoreData = true
        short_video_browser.resetVideoData(videoInfoList, 0)

    }

}


fun ShortVideoDetailFragment.loadMoreShortVideoList() {
    isLoadingMore = true
    apiSpiceMgr.executeToastKt(ShortVideoRecommendRequest(nextStartKey.orEmpty()), success = {
        if (it.isSuccess) {
            hasMoreData = it.data?.next?.size ?: 0 != 0
        }
        if (it.isSuccess && it.data != null) {
            it.data?.next?.let { next ->
                /**2.3.4 起后端给什么数据就加载什么数据，不需要过滤*/
                short_video_browser.addDataList(next)
                videoInfoList.addAll(next)
            }
            nextStartKey = it.data.nextStartKey.orEmpty()
        }
    }, finish = {
        isLoadingMore = false
    })

}


/**
 * 上报当前播放的视频
 */
fun shortVideoPlaying(seqId: String?) {
    spiceHolder().apiSpiceMgr.executeKt(RecommendShortVideoPlayingRequest(seqId.orEmpty()))
}

/**
 * 统计进入当前页面停留时长
 */
@Suppress("UNUSED_PARAMETER")
fun ShortVideoDetailFragment.reportShortVideo(seqId: String) {
    val request = ShortVideoReportRequest(resumeTime, System.currentTimeMillis() - resumeTime, seqId, "recommend")
    spiceHolder().apiSpiceMgr.executeKt(request)
    GrowingUtil.track("video_page_view", "video_id", seqId,
            "stay_time", "${(System.currentTimeMillis() - resumeTime) / 1000}",
            "start_time", "${resumeTime / 1000}",
            "function_page", "recommend",
            "time_stamp", "${System.currentTimeMillis() / 1000}")

    resumeTime = System.currentTimeMillis()
}

/**
 * 短视频播放完成送心币逻辑，后端那边判断了最小视频时3秒，走这个接口调用有两个途径:
 * 1，当视频时循环播放模式时 走视频播放信息回调 ShortVideoView 回调 onInfo 中的 10086 码
 * 使用播放时长和当前播放进度来进行判断是否播放完成
 * 2. 当视频不是循环播放时，会走onCompletion 回调，我们会在这个时机调播放完成接口
 */
fun ShortVideoDetailFragment.shortVideoCompletion(seqId: String) {
    val duration = System.currentTimeMillis() - completionTime
    if (duration > 1000) {
        spiceHolder().apiSpiceMgr.executeKt(ShortVideoCompletionRequest(seqId, duration), success = {
            if (it.isSuccess && it.data.toast?.isNotEmpty() == true) {
                /**播放完视频后送心币需要弹提示*/
                UIToast.toast(it.data.toast)
            }
        })
        completionTime = System.currentTimeMillis()
    }
}



