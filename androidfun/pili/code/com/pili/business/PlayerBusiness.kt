package com.pili.business

import com.live.player.presenter.ListenAudioView
import com.mei.init.spiceHolder
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.isOnDoubleClick
import com.mei.player.PlayerHandleActivity
import com.mei.wood.extensions.executeKt
import com.mei.wood.util.AppManager
import com.net.model.broadcast.PlayType
import com.net.network.chick.course.AudioGetRequest
import com.net.network.chick.course.AudioProgressRequest
import com.pili.PlayInfo
import com.pili.audioProgress
import com.pili.getPlayInfo
import com.pili.player
import kotlin.math.abs

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/9
 */
fun Long.toDivide(): Long = if (this != 0L) this else 1

class PlayerBusiness : ListenAudioView {

    var lastPlayInfo = PlayInfo()
    var lastReportPercent = 0//最新上报的百分比
    var currentPercent = 0

    override fun onCompletion() {
        currentPercent = 100
        reportServiceAudioPercent()
        playNextAudio()
    }

    override fun onSeekComplete() {
        currentPercent = audioProgress
        reportServiceAudioPercent()
    }

    override fun onPrepared(preparedTime: Int) {
        lastPlayInfo = getPlayInfo()
//        if (getPlayInfo().mPlayType == PlayType.LISTENING_SERVICE) {
//            val percent = getPlayInfo().startPercent
//            if (percent in 1..99) {
//                player.seekTo(percent * player.getDuration() / 100)
//            }
//        }
    }

    override fun onStopPlayer() {
        reportServiceAudioPercent()
        lastPlayInfo = PlayInfo()
    }

    override fun onInfo(what: Int, extra: Int) {
        isOnDoubleClick("playerProgress")
        currentPercent = audioProgress
        if (abs(lastReportPercent - currentPercent) >= 3) {
            /** 每隔3个百分点上报一次 **/
            reportServiceAudioPercent()
        }
    }

    override fun onError(code: Int) {
    }


    private fun reportServiceAudioPercent() {
        if (lastPlayInfo.mPlayType != PlayType.NONE) {
            lastReportPercent = currentPercent
            val request = AudioProgressRequest(lastPlayInfo.channelId, lastPlayInfo.mAudioId, currentPercent)
            spiceHolder().apiSpiceMgr.executeKt(request)
        }
    }

    private fun hasHandleAudioComplete(): Boolean {
        return AppManager.getInstance().getTargetActivity(PlayerHandleActivity::class.java) != null
    }

    private fun playNextAudio() {
        if (getPlayInfo().mPlayType == PlayType.LISTENING_SERVICE && !hasHandleAudioComplete()) {
            val request = AudioGetRequest(getPlayInfo().channelId, getPlayInfo().mAudioId, 1)
            spiceHolder().apiSpiceMgr.executeKt(request, success = {
                val data = it.data
                if (data != null) {
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
                            .backgroundColor(data.course.backgroundColor)
                    )
                } else if (it.rtn == 450) {
                    /** 当前没有其它音频了，重新播放当前音频 **/
                    player.start(getPlayInfo().setStartPosition(0))
                } else {
                    UIToast.toast(if (it.errMsg.isEmpty()) "自动播放下一首失败" else it.errMsg)
                }
            }, failure = {
                UIToast.toast("自动播放下一首失败")
            })
        }
    }
}