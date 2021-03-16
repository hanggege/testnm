package com.pili

import android.text.TextUtils
import com.net.model.broadcast.PlayType
import com.pili.player.ListenController
import com.pili.player.bindPlayerService
import com.pili.stream.bindStreamService
import kotlin.math.abs

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/30
 */
fun getPlayInfo(): PlayInfo {
    val listenInfo = player.getPlayInfo()
    val streamInfo = bindStreamService().getPlayInfo()
    return if (!TextUtils.equals(listenInfo.mPlayType, PlayType.NONE)) listenInfo
    else if (!TextUtils.equals(streamInfo.mPlayType, PlayType.NONE)) streamInfo
    else PlayInfo.NONE
}

fun stopPlayer() {
    player.stop()
}

fun stopStream() {
    bindStreamService().stop()
}

fun stopAudio() {
    stopPlayer()
    stopStream()
}

/**
 * 是否播放完成
 */
fun playerIsCompletion(): Boolean {
    return !player.isPlaying() && player.getProgress() > 0
            && abs(player.getDuration() - player.getProgress()) < 1000
}

/**
 * 是否是当前播放器的id与类型
 */
fun String.isPlaySelf(id: Number) = getPlayInfo().mAudioId == id && getPlayInfo().mPlayType == this

/**
 * 播放器
 */
inline var player: ListenController
    set(_) {}
    get() = bindPlayerService()

inline var audioProgress: Int
    set(_) {}
    get() {
        val duration = bindPlayerService().getDuration()
        return if (duration > 0) {
            (((bindPlayerService().getProgress().toDouble()) / duration) * 100).toInt()
        } else {
            0
        }
    }
