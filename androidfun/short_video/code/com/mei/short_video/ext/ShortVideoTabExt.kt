package com.mei.short_video.ext

import androidx.core.view.isVisible
import com.mei.GrowingUtil
import com.mei.orc.ext.getIndexOrNull
import com.mei.short_video.ShortTab
import com.mei.short_video.ShortVideoTabFragment
import com.mei.short_video.SquareTabFragment
import com.mei.wood.extensions.executeKt
import com.net.model.chick.video.ShortVideoInfo
import com.net.network.chick.video.MentorVideosRequest
import kotlinx.android.synthetic.main.short_video_tab_fragment.*

/**
 *
 * @author Created by lenna on 2020/9/2
 * 短视频推荐tab 扩展文件
 */

/**
 * 首次加载视频数据
 */
fun ShortVideoTabFragment.getMentorVideos(shortVideoInfo: ShortVideoInfo.VideoInfo?) {
    apiSpiceMgr.executeKt(MentorVideosRequest(shortVideoInfo?.userId
            ?: 0, 1, shortVideoInfo?.seqId.orEmpty()), success = {
        if (it.isSuccess) {
            mentorVideoAdapter.loadMoreModule.isEnableLoadMore = it.data.info.videos?.isNotEmpty() == true
            mentor_short_video_net_error_view.isVisible = false
            mMentorShortVideo = it.data
            mentorVideoData.clear()
            mentorVideoData.add("热门标签")
            mentorVideoData.add(it.data.info.tags.orEmpty())
            mentorVideoData.add("${shortVideoInfo?.user?.nickName}的视频")
            if (it.data.info.videos.isNullOrEmpty()) {
                mentorVideoData.add(1)
            } else {
                mentorVideoData.addAll(it.data.info.videos.orEmpty())
            }
            if (it.data.info.hasMore) {
                mentorVideoAdapter.loadMoreModule.loadMoreComplete()
            } else {
                mentorVideoAdapter.loadMoreModule.loadMoreEnd()
            }
            mentor_short_video_rlv.scrollToPosition(0)
            mentorVideoAdapter.notifyDataSetChanged()

        } else {
            mentorVideoAdapter.loadMoreModule.loadMoreFail()
            mentor_short_video_net_error_view.isVisible = true
        }
    }, failure = {
        mentorVideoAdapter.loadMoreModule.loadMoreFail()
        mentor_short_video_net_error_view.isVisible = true

    })
}

/**
 * 加载更多视频请求
 */
fun ShortVideoTabFragment.loadMoreVideos(shortVideoInfo: ShortVideoInfo.VideoInfo?) {
    apiSpiceMgr.executeKt(MentorVideosRequest(shortVideoInfo?.userId ?: 0
            , mMentorShortVideo.info.nextPageNo, shortVideoInfo?.seqId.orEmpty()), success = {
        if (it.isSuccess) {
            mentorVideoAdapter.loadMoreModule.isEnableLoadMore = it.data.info.videos?.isNotEmpty() == true
            mentorVideoData.addAll(it.data.info.videos.orEmpty())
            if (it.data.info.hasMore) {
                mMentorShortVideo.info.nextPageNo = it.data.info.nextPageNo
                mentorVideoAdapter.loadMoreModule.loadMoreComplete()
            } else {
                mentorVideoAdapter.loadMoreModule.loadMoreEnd()
            }
            mentorVideoAdapter.notifyDataSetChanged()
        } else {
            mentorVideoAdapter.loadMoreModule.loadMoreFail()
        }
    }, failure = {
        mentorVideoAdapter.loadMoreModule.loadMoreFail()
    })
}

/**
 * 预览
 */
fun ShortVideoTabFragment.startShortVideoPreView() {
    try {
        val currentTab = short_video_tab.currentTab
        val currTab = tabData?.tabs?.getIndexOrNull(currentTab)?.id
        var tagName = ""
        var pageName = ""
        if (currTab == ShortTab.RECOMMEND.name) {
            tagName = ""
            pageName = "推荐"
        } else if (currTab == ShortTab.SQUARE.name) {
            tagName = (fragmentList.getIndexOrNull(currentTab) as? SquareTabFragment)?.getCurrentTag().orEmpty()
            pageName = "广场"
        }
        GrowingUtil.track("main_page_view", "screen_name", "短视频页",
                "main_app_gn_type", "短视频",
                "main_app_gn_pro", tagName,
                "main_app_gn_label", pageName,
                "time_stamp", (System.currentTimeMillis()/ com.mei.orc.unit.TimeUnit.SECOND).toString())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}