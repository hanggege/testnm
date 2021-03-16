package com.mei.video

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.webkit.URLUtil
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mei.GrowingUtil
import com.mei.base.network.netchange.isWIFIConnected
import com.mei.init.spiceHolder
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.date.formatToString
import com.mei.orc.util.date.isToday
import com.mei.orc.util.save.getNonValue
import com.mei.orc.util.save.putValue
import com.mei.video.browser.adapter.ShortVideoRefresh
import com.mei.video.browser.video.IjkAspectRatio
import com.mei.video.browser.video.doMeasure
import com.mei.wood.GlideApp
import com.mei.wood.dialog.DISSMISS_DO_CANCEL
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.net.model.chick.video.ShortVideoInfo
import com.net.network.chick.video.MentorVideosRequest
import com.net.network.chick.video.ShortVideoCompletionRequest
import com.net.network.chick.video.ShortVideoInfoRequest
import com.net.network.chick.video.ShortVideoReportRequest
import kotlinx.android.synthetic.main.short_video_player_new_layout.*
import java.util.*


/**
 *
 * @author Created by lenna on 2020/8/17
 */

fun ShortVideoPlayerNewActivity.requestVideoRequest() {
    showLoading(true)
    net_error_view.isVisible = false
    short_video_cover_img.isVisible = URLUtil.isNetworkUrl(videoCoverUrl)
//    short_video_cover_img.glideDisplay(videoCoverUrl.substringBefore("?"))
    completionTime = System.currentTimeMillis()
    GlideApp.with(this)
            .asBitmap()
            .load(videoCoverUrl.substringBefore("?"))
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    resource?.let { bitmap ->
                        val whPair = IjkAspectRatio.DOUYIN_TYPE.doMeasure(short_video_cover_img.measuredWidth, short_video_cover_img.measuredHeight, bitmap.width, bitmap.height)
                        if (whPair.first > short_video_cover_img.measuredWidth) {
                            short_video_cover_img.scaleX = whPair.first.toFloat() / short_video_cover_img.measuredWidth
                        } else {
                            short_video_cover_img.scaleX = 1f
                        }
                        short_video_cover_img.scaleY = short_video_cover_img.scaleX
                    }
                    return false
                }

            }).into(short_video_cover_img)
    apiSpiceMgr.executeToastKt(ShortVideoInfoRequest(videoId, publisherId, tagId, isMyLike, isAllTag), success = {
        if (it.isSuccess && it.data != null) {
            net_error_view.isVisible = false
            videoInfoList.clear()
            it.data?.last?.let { last -> videoInfoList.addAll(last) }
            it.data?.current?.let { current ->
                videoInfoList.add(current)
                getMentorVideos(current)
            }
            it.data?.next?.let { next -> videoInfoList.addAll(next) }
            resetBrowser(videoInfoList.indexOfFirst { indexData -> indexData.seqId == it.data?.current?.seqId })
            val isToday = "is_today_first_show".getNonValue("").isToday()
            if (!isWIFIConnected() && !isToday) {
                "is_today_first_show".putValue(Date().formatToString())
                UIToast.toast(this, "非wifi网络，请注意流量消耗")
            }
        } else {
            net_error_view.isVisible = true
            short_video_cover_img.isVisible = false
        }

    }, failure = {
        net_error_view.isVisible = true
        short_video_cover_img.isVisible = false
    }, finish = {
        showLoading(false)
        short_video_refresh.isRefreshing = false
    })
}


fun ShortVideoPlayerNewActivity.resetBrowser(position: Int = -1) {
    val index = when {
        position > -1 -> position
        short_video_browser.currentPosition > videoInfoList.size - 1 -> videoInfoList.size - 1
        short_video_browser.currentPosition in 0 until videoInfoList.size -> short_video_browser.currentPosition
        else -> 0
    }
    hasMoreData = true
    short_video_browser.resetVideoData(videoInfoList, index)
    short_video_browser.post {
        short_video_browser.pagerRecycler.apply {
            forEach {
                (it as? ShortVideoRefresh)?.performSelected(index)
            }
        }
    }
}


fun ShortVideoPlayerNewActivity.loadMoreShortVideoList() {
    isLoadingMore = true
    apiSpiceMgr.executeToastKt(ShortVideoInfoRequest(videoInfoList.lastOrNull()?.seqId.orEmpty(),
            publisherId, tagId, isMyLike, isAllTag), success = {
        if (it.isSuccess) {
            hasMoreData = it.data?.next?.size ?: 0 != 0
        }
        if (it.isSuccess && it.data != null) {
            it.data?.next?.let { next ->
                /**2.3.4 起不需要过滤数据，后端不会给相同的数据*/
                short_video_browser.addDataList(next)
                videoInfoList.addAll(next)
            }

        } else {
            UIToast.toast(it.errMsg)
        }
    }, failure = {
        UIToast.toast(it?.currMessage)
    }, finish = {
        isLoadingMore = false
    })

}

fun ShortVideoPlayerNewActivity.getMentorVideos(info: ShortVideoInfo.VideoInfo?) {
    apiSpiceMgr.executeKt(MentorVideosRequest(info?.userId
            ?: 0, 1, info?.seqId.orEmpty()), success = {
        if (it.isSuccess) {
            mentorVideoAdapter.loadMoreModule.isEnableLoadMore = it.data.info.videos?.isNotEmpty() == true
            mentor_short_video_net_error_view.isVisible = false
            mMentorShortVideo = it.data
            mentorVideoData.clear()
            mentorVideoData.add("热门标签")
            mentorVideoData.add(it.data.info.tags.orEmpty())
            mentorVideoData.add("${info?.user?.nickName}的视频")
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

fun ShortVideoPlayerNewActivity.loadMoreVideos(info: ShortVideoInfo.VideoInfo?) {
    apiSpiceMgr.executeKt(MentorVideosRequest(info?.userId
            ?: 0, mMentorShortVideo.info.nextPageNo, info?.seqId.orEmpty()), success = {
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
 * 统计进入当前页面停留时长
 */
@Suppress("UNUSED_PARAMETER")
fun ShortVideoPlayerNewActivity.reportShortVideo(seqId: String) {
    val request = ShortVideoReportRequest(resumeTime, System.currentTimeMillis() - resumeTime, seqId, fromType)
    spiceHolder().apiSpiceMgr.executeKt(request)
    GrowingUtil.track("video_page_view", "video_id", seqId,
            "stay_time", "${(System.currentTimeMillis() - resumeTime) / 1000}",
            "start_time", "${resumeTime / 1000}",
            "function_page", "detail",
            "time_stamp", "${System.currentTimeMillis() / 1000}")
    resumeTime = System.currentTimeMillis()
}

fun ShortVideoPlayerNewActivity.shortVideoCompletion(seqId: String) {
    val duration = System.currentTimeMillis() - completionTime
    if (duration > 1000) {
        spiceHolder().apiSpiceMgr.executeKt(ShortVideoCompletionRequest(seqId, System.currentTimeMillis() - completionTime), success = {
            if (it.isSuccess && it.data.toast?.isNotEmpty() == true) {
                /**播放完视频后送心币需要弹提示*/
                UIToast.toast(it.data.toast)
            }
        })
        completionTime = System.currentTimeMillis()
    }
}

fun DrawerLayout.setDrawerRightEdgeSize(activity: Activity?, displayWidthPercentage: Float) {
    if (activity == null) return
    try {
        // 找到 ViewDragHelper 并设置 Accessible 为true
        val leftDraggerField = javaClass.getDeclaredField("mRightDragger") //Right
        leftDraggerField.isAccessible = true
        val leftDragger = leftDraggerField.get(this) as ViewDragHelper

        // 找到 edgeSizeField 并设置 Accessible 为true
        // 设置新的边缘大小
        val displaySize = Point()
        activity.windowManager.defaultDisplay.getSize(displaySize)
        val size = (displaySize.x * displayWidthPercentage).toInt()
        if (Build.VERSION.SDK_INT >= 29) {
            val edgeSizeField = leftDragger.javaClass.getDeclaredField("mDefaultEdgeSize")
            edgeSizeField.isAccessible = true
            val edgeSize: Int = edgeSizeField.getInt(leftDragger)
            edgeSizeField.setInt(leftDragger, edgeSize.coerceAtLeast(size))
        } else {
            leftDragger.edgeSize = leftDragger.edgeSize.coerceAtLeast(size)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 退出短视频详情
 */
fun ShortVideoPlayerNewActivity.showExitShortVideoDetail() {
    val msg: String? = short_video_browser.getCurrentItemData()?.closeConfirmTips.orEmpty()
    val isExit = "is_exit_short_video_detail".getNonValue(false)
    if (msg?.isNotEmpty() == true) {
        if (!isExit && !mIsExitDetail) {
            "is_exit_short_video_detail".putValue(true)
            NormalDialogLauncher.newInstance().showDialog(this, DialogData(message = msg
                    , cancelStr = "退出", okStr = "继续观看")
                    , back = {
                if (it == DISSMISS_DO_CANCEL) {
                    statClickExitShortVideo(true)
                    finish()
                } else if (it == DISSMISS_DO_OK) {
                    statClickExitShortVideo(false)
                }
            })
            statExitShortVideo()
        } else {
            "is_exit_short_video_detail".putValue(true)
            finish()
        }
    } else {
        finish()
    }
}

/**
 * 侧滑浮层统计
 * */
fun statShowRightView() {
    try {
        GrowingUtil.track("main_gn_entrance_view",
                "screen_name", "侧滑浮层页",
                "main_app_gn_type", "短视频")
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun statLiftSlip(page: String) {
    try {
        GrowingUtil.track("left_slip", "function_page", page)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun statUpGlide(page: String, action: String) {
    try {
        GrowingUtil.track("up_glide", "function_page", page, "action", action)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 退出短视频弹框统计
 */
fun statExitShortVideo() = try {
    GrowingUtil.track("push_popup_view",
            "popup_type", "退短提醒送心币弹窗",
            "view_type", "",
            "popup_click_type", "",
            "room_type", "",
            "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
} catch (e: Exception) {
    e.printStackTrace()
}

/**
 * 退出短视频点击埋点
 */
fun statClickExitShortVideo(isExit: Boolean) = try {
    GrowingUtil.track("push_popup_click", "popup_type", "退短提醒送心币弹窗",
            "popup_click_type", if (isExit) "退出" else "继续观看",
            "view_type", "",
            "room_type", "",
            "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
} catch (e: Exception) {
    e.printStackTrace()
}

