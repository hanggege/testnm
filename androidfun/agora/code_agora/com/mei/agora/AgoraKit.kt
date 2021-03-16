package com.mei.agora

import android.content.Context
import android.view.SurfaceView
import androidx.annotation.IntRange
import androidx.fragment.app.FragmentActivity
import com.faceunity.FURenderer
import com.mei.agora.config.AgoraConfig
import com.mei.init.meiApplication
import com.mei.init.spiceHolder
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.extensions.executeKt
import com.net.network.room.UserStreamReportRequest
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import io.agora.rtc.mediaio.AgoraTextureView
import io.agora.rtc.mediaio.MediaIO
import io.agora.rtc.video.VideoCanvas

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-11-25
 */
val agoraConfig: AgoraConfig by lazy { AgoraConfig() }

private var isInit = false
fun initAgora() {
    if (!isInit) {
        isInit = true
        agoraConfig.initAgora()
        FURenderer.initFURenderer(meiApplication())
    }
}

/**
 * 加入直播间
 */
fun FragmentActivity.joinLiveRoom(roomName: String, token: String? = null) {
    agoraConfig.workThread.joinChannel(roomName, token)
}

/**
 * 离开频道
 */
fun FragmentActivity.leaveLiveRoom() {
    agoraConfig.workThread.leaveChannel()
}

/**
 * 开/关本地视频采集。
 */
fun enableLocalVideo(enableVideo: Boolean) {
    agoraConfig.workThread.enableLocalVideo(enableVideo)
}

/**
 * 是否关闭远端音频流的采集
 * @param mute 禁止音频
 */
fun FragmentActivity.muteAllRemoteAudioStreams(mute: Boolean) {
    val rtcEngine = agoraConfig.rtcEngine()
    if (rtcEngine == null) {
        finish()
        return
    }
    var isSuccess: Int
    var count = 0
    do {
        isSuccess = rtcEngine.muteAllRemoteAudioStreams(mute)
        /**华为漏音频的情况,直接关闭音频模块**/
        if (mute) rtcEngine.disableAudio()
        else rtcEngine.enableAudio()
        if (isSuccess < 0 && count == 0) {
            val videoRoomId = (this as? VideoLiveRoomActivity)?.roomId.orEmpty()
            spiceHolder().apiSpiceMgr.executeKt(UserStreamReportRequest().apply {
                roomId = videoRoomId
                mode = 1
                status = if (mute) 1 else 0
            })
        }
        count++
    } while (isSuccess < 0 && count < 3)
    if (isSuccess < 0) {
        UIToast.toast("网络异常,请重新进入直播间")
        finish()
    }
}

/**
 * 设置直播间角色
 */
fun AgoraConfig.setClientRole(@IntRange(from = 1, to = 2) role: Int = Constants.CLIENT_ROLE_AUDIENCE) {
    var isSuccess: Int
    var count = 0
    do {
        isSuccess = rtcEngine()?.setClientRole(role) ?: -1
        count++
    } while (isSuccess < 0 && count < 3)
    if (isSuccess < 0) {
        UIToast.toast("网络异常,请重新进入直播间")
    }
}

/**
 * 设置音频应用场景
 */
fun AgoraConfig.setAudioProfile(profile: Int, scenario: Int) {
    rtcEngine()?.setAudioProfile(profile, scenario)
}

/**
 * 刷新token
 */
fun AgoraConfig.renewToken(token: String) {
    rtcEngine()?.renewToken(token)
}

/**
 * 切换房间
 */
fun AgoraConfig.switchChannel(roomName: String, token: String? = null) {
    rtcEngine()?.switchChannel(token, roomName)
}

/**
 * 创建视频直播的[SurfaceView]
 * @param uid 等于0时为自己
 */
fun AgoraConfig.prepareRtcVideo(uid: Int = 0): SurfaceView {
    val surface = RtcEngine.CreateRendererView(meiApplication().applicationContext)
    if (uid == 0 || uid == JohnUser.getSharedUser().userID) {
        rtcEngine()?.setupLocalVideo(VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, uid, 1))
    } else {
        rtcEngine()?.setupRemoteVideo(VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, uid, 1))
    }
    return surface
}

fun AgoraConfig.prepareRtcTextureVideo(context: Context, uid: Int = 0): AgoraTextureView {
    val textureView = AgoraTextureView(context).apply {
        setBufferType(MediaIO.BufferType.BYTE_ARRAY)
        setPixelFormat(MediaIO.PixelFormat.I420)
        setMirror(true)
    }
    rtcEngine()?.setRemoteVideoRenderer(uid, textureView)
    return textureView
}

/**
 * 删除直播视频
 */
fun AgoraConfig.removeRtcVideo(uid: Int = 0) {
    if (uid == 0) {
        rtcEngine()?.setupLocalVideo(null)
    } else {
        rtcEngine()?.setupRemoteVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid))
    }
}

fun AgoraConfig.removeRtcTextureVideo(uid: Int = 0) {
    if (uid == 0) {
        rtcEngine()?.setupLocalVideo(null)
    } else {
        rtcEngine()?.setRemoteVideoRenderer(uid, null)
    }
}

