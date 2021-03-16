package com.mei.course_service.ui

import android.os.Bundle
import android.util.Log
import com.live.player.presenter.ListenAudioView
import com.mei.base.common.COURSE_PAY_SUCCESS
import com.mei.base.common.MUTE_REMOTE_AUDIO_STREAMS
import com.mei.init.spiceHolder
import com.mei.live.ui.ext.getCurrLiveState
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.ext.lightMode
import com.mei.orc.ext.px2dip
import com.mei.orc.ext.transparentStatusBar
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.isNotOnDoubleClick
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.util.AppManager
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.parseValue
import com.mei.wood.web.MeiWebActivity
import com.mei.wood.web.MeiWebData
import com.net.model.broadcast.PlayType
import com.net.network.chick.course.AudioGetRequest
import com.pili.*
import com.pili.business.handleErrorCode
import kotlinx.android.synthetic.main.mei_web_layout.*
import launcher.Boom

/**
 *  继承公用的webActivity 对于课程的web页面进行特殊处理
 * @author Created by lenna on 2020/7/3
 */
class MeiWebCourseServiceActivity : MeiWebActivity(), ListenAudioView {
    @Boom
    override var webData: MeiWebData = MeiWebData()
    private var mAudioId = 0
    override fun customStatusBar(): Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        lightMode()
        bindAction<Boolean>(COURSE_PAY_SUCCESS) {
            if (it == true) {
                mei_web?.loadUrl("javaScript:reloadData()")
                paySuccess()
            }
        }
    }

    override fun subIntercept(url: String): Boolean {
        var subIntercept = true
        when {
            url.startsWith(AmanLink.URL.COURSE_SERVICE_PLAYER) -> {
                //播放课程操作处理
                val playerStatus = url.parseValue("playerStatus", 0)
                //控制播放器播放
                val audioId = url.parseValue("id", 0)
                if (playerStatus == 1) {
                    UIToast.toast(activity, when (getCurrLiveState()) {
                        1 -> "当前正在直播中，无法播放"
                        2 -> "当前正在连线中，无法播放"
                        else -> {
                            isNotOnDoubleClick(500, audioId.toString()) {
                                startPlayer(audioId)
                            }
                            ""
                        }
                    })
                } else {
                    if (PlayType.LISTENING_SERVICE.isPlaySelf(audioId)) {
                        player.pause()
                    }
                }
            }
            url.startsWith(AmanLink.URL.LOAD_FINISH) -> {
                val type = url.parseValue("type", 0)
                if (type == 2) {
                    //webView加载完数据会通知
                    setStatusBarHeight(px2dip(getStatusBarHeight()).toInt())
                    mei_web.post {
                        mAudioId = getPlayInfo().mAudioId
                        coursePlaying(if (player.isPlaying()) 1 else 0, mAudioId)
                    }
                }

            }
            else -> {
                subIntercept = false
            }
        }
        return subIntercept
    }

    /**
     * 开启播放器
     */
    private fun startPlayer(audioId: Int) {
        val isPlaySelf = PlayType.LISTENING_SERVICE.isPlaySelf(audioId)
        if (!isPlaySelf || playerIsCompletion() || player.getProgress() < 1000) {
            showLoading(true)
            spiceHolder().apiSpiceMgr.executeToastKt(AudioGetRequest(audioId), success = {
                it.data?.let { data ->
                    showLoading(true)
                    postAction(MUTE_REMOTE_AUDIO_STREAMS, true)
                    /** 停止播放且拉取当前id信息进行播放 **/
                    player.start(PlayInfo.Builder()
                            .url(data.audio.audioUrl)
                            .playType(PlayType.LISTENING_SERVICE)
                            .channelId(data.audio.courseId)
                            .audioId(data.audio.id)
                            .groupId(data.publisherInfo.groupId)
                            .title(data.audio.audioTitle)
                            .subTitle(data.course.courseName)
                            .mentorUserId(data.publisherInfo.userId)
                            .mentorAvatar(data.course.personalImage)
                            .setStartPosition(data.audio.startPosition)
                            .backgroundColor(data.course.backgroundColor)
                    )

                }
            }, finish = { showLoading(false) })
        } else if (isPlaySelf) {
            player.resume()
        }
    }

    override fun readWebData() {
        when {
            intent.dataString?.matches(AmanLink.URL.COURSE_SERVICE_JSON_WEBVIEW.toRegex()) == true -> {
                webData = MeiUtil.getJsonObject(intent.dataString.orEmpty(), AmanLink.URL.COURSE_SERVICE_JSON_WEBVIEW)
                        ?: MeiWebData()
            }
            intent.dataString?.matches(AmanLink.URL.COURSE_SERVICE_WEBVIEW.toRegex()) == true -> {
                val url = MeiUtil.getOneID(intent.dataString.orEmpty(), AmanLink.URL.COURSE_SERVICE_WEBVIEW)
                webData = MeiWebData(url, "")
            }
            else -> ActivityLauncher.bind(this)
        }

    }


    override fun onCompletion() {
        val audioId = getPlayInfo().mAudioId
        courseProgress(100, audioId)
    }

    override fun onSeekComplete() {
        val audioId = getPlayInfo().mAudioId
        if (player.getDuration() > 0) {
            val progress = player.getProgress() / player.getDuration() * 100
            courseProgress(progress, audioId)
        }
    }

    override fun onPrepared(preparedTime: Int) {
        showLoading(false)
        mAudioId = getPlayInfo().mAudioId
    }

    override fun onStopPlayer() {
        //播放停止
        showLoading(false)
        coursePlaying(0, mAudioId)

    }

    override fun onStateChange(isPlaying: Boolean) {
        super.onStateChange(isPlaying)
        //播放状态变化
        val audioId = getPlayInfo().mAudioId
        coursePlaying(if (isPlaying) 1 else 0, audioId)
    }

    override fun onInfo(what: Int, extra: Int) {
        //更新播放进度
        isNotOnDoubleClick(1000, "CoursePlayerProgress", isNotDoubleClick = {
            if (player.getDuration() > 0) {
                val progress = player.getProgress() * 1.0f / player.getDuration() * 100
                courseProgress(progress.toLong(), getPlayInfo().mAudioId)
            }
        })
    }

    override fun onError(code: Int) {
        showLoading(false)
        //播放错误
        if (AppManager.getInstance().currentActivity() == activity) {
            handleErrorCode(code)
        }
    }


    /**
     * 播放控制
     */
    private fun coursePlaying(isPlaying: Int, audioId: Int) {
        Log.i("--------->", "播放状态----->$isPlaying -----播放音频id----->$audioId")
        mei_web.loadUrl("javaScript:coursePlaying($isPlaying,$audioId)")
    }

    /**
     * 加载进度
     */
    private fun courseProgress(percentage: Long, audioId: Int) {
        mei_web.loadUrl("javaScript:courseProgress($percentage,$audioId)")
    }


    private fun paySuccess() {
        mei_web.loadUrl("javaScript:paySuccesses()")
    }

}