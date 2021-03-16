package com.mei.agora.config

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.mei.agora.event.AgoraEventHandler
import com.mei.agora.work.WorkerThread
import com.mei.init.meiApplication
import com.mei.orc.util.json.toJson
import com.mei.wood.BuildConfig
import com.mei.wood.util.logDebug
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-11-25
 */
class AgoraConfig : IRtcEngineEventHandler() {

    private val viewSet: CopyOnWriteArraySet<AgoraEventHandler> by lazy { CopyOnWriteArraySet<AgoraEventHandler>() }
    private val lifecycle: AgoraLifecycle by lazy { AgoraLifecycle() }
    private val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    val workThread: WorkerThread by lazy { WorkerThread(this) }

    fun rtcEngine(): RtcEngine? {
        return workThread.initAgoraEngine()
    }

    fun initAgora() {
        meiApplication().registerActivityLifecycleCallbacks(lifecycle)
        workThread.start()
    }

    fun bindView(view: AgoraEventHandler) {
        viewSet.add(view)
    }

    fun unBindView(view: AgoraEventHandler) {
        viewSet.remove(view)
    }

    @Synchronized
    private fun looperView(action: (AgoraEventHandler) -> Unit) {
        mainHandler.post {
            viewSet.forEach { action(it) }
        }
    }

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        logDebug("Agora", "onJoinChannelSuccess:$channel   $uid   $elapsed")
        looperView { it.onJoinChannelSuccess(channel, uid, elapsed) }
    }

    override fun onRejoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        logDebug("Agora", "onRejoinChannelSuccess:$channel   $uid   $elapsed")
        looperView { it.onRejoinChannelSuccess(channel, uid, elapsed) }
    }

    override fun onLeaveChannel(stats: RtcStats?) {
        logDebug("Agora", "onLeaveChannel: ${stats?.users}")
        looperView { it.onLeaveChannel(stats) }
    }

    override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        logDebug("Agora", "onRemoteVideoStateChanged: $uid $state $reason $elapsed")
        looperView { it.onRemoteVideoStateChanged(uid, state, reason, elapsed) }
    }

    override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        logDebug("Agora", "onRemoteAudioStateChanged:$uid $state $reason $elapsed")
        looperView { it.onRemoteAudioStateChanged(uid, state, reason, elapsed) }
    }

    override fun onLocalVideoStats(stats: LocalVideoStats?) {
        logDebug("Agora", "onLocalVideoStats:${stats.toJson()}")
        looperView { it.onLocalVideoStats(stats) }
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        logDebug("Agora", "onUserJoined:$uid  $elapsed")
        looperView { it.onUserJoined(uid, elapsed) }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        logDebug("Agora", "onUserOffline:$uid   $reason")
        looperView { it.onUserOffline(uid, reason) }
    }

    override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
        if (BuildConfig.IS_TEST) Log.d("Agora", "onNetworkQuality:$uid $txQuality $rxQuality")
        looperView { it.onNetworkQuality(uid, txQuality, rxQuality) }
    }

    override fun onUserMuteAudio(uid: Int, muted: Boolean) {
        logDebug("Agora", "onUserMuteAudio:$uid   $muted")
        looperView { it.onUserMuteAudio(uid, muted) }
    }

    override fun onError(err: Int) {
        logDebug("Agora", "onError:$err")
        looperView { it.onError(err) }
    }

    override fun onConnectionStateChanged(state: Int, reason: Int) {
        logDebug("Agora", "onConnectionStateChanged:$state  $reason")
        looperView { it.onConnectionStateChanged(state, reason) }
    }

    override fun onConnectionLost() {
        logDebug("Agora", "onConnectionLost:")
        looperView { it.onConnectionLost() }
    }

    override fun onApiCallExecuted(error: Int, api: String?, result: String?) {
        logDebug("Agora", "onApiCallExecuted:$error $api $result")
    }

    override fun onTokenPrivilegeWillExpire(token: String?) {
        logDebug("Agora", "onTokenPrivilegeWillExpire:$token")
    }

    override fun onRequestToken() {
        logDebug("Agora", "onRequestToken:Token 过期回调 ")
    }

    override fun onAudioVolumeIndication(speakers: Array<AudioVolumeInfo>?, totalVolume: Int) {
//        logDebug("Agora", "onAudioVolumeIndication:$totalVolume   提示频道内谁正在说话、说话者音量及本地用户是否在说话的回调")
        looperView { it.onAudioVolumeIndication(speakers, totalVolume) }
    }

    override fun onActiveSpeaker(uid: Int) {
        logDebug("Agora", "onActiveSpeaker:$uid")
    }

    override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
        logDebug("Agora", "onFirstLocalVideoFrame: $width  $height  $elapsed")
        looperView { it.onFirstLocalVideoFrame(width, height, elapsed) }
    }

    override fun onFirstLocalAudioFrame(elapsed: Int) {
        logDebug("Agora", "onFirstLocalAudioFrame:$elapsed")
        looperView { it.onFirstLocalAudioFrame(elapsed) }
    }

    override fun onFirstRemoteAudioFrame(uid: Int, elapsed: Int) {
        logDebug("Agora", "onFirstRemoteAudioFrame:$uid $elapsed")
    }

    override fun onFirstRemoteAudioDecoded(uid: Int, elapsed: Int) {
        logDebug("Agora", "onFirstRemoteAudioDecoded:$uid $elapsed")
    }

    override fun onRtcStats(stats: RtcStats?) {
        logDebug("Agora", "onRtcStats:${stats?.toJson()}")
        looperView { it.onRtcStats(stats) }
    }

    override fun onUserEnableLocalVideo(uid: Int, enabled: Boolean) {
        logDebug("Agora", "onUserEnableLocalVideo:$uid $enabled")
        looperView { it.onUserEnableLocalVideo(uid, enabled) }
    }

    override fun onLocalVideoStateChanged(localVideoState: Int, error: Int) {
        logDebug("Agora", "onActiveSpeaker:$localVideoState $error")
    }

    override fun onLocalPublishFallbackToAudioOnly(isFallbackOrRecover: Boolean) {
        logDebug("Agora", "onLocalPublishFallbackToAudioOnly:$isFallbackOrRecover")
    }

    override fun onLocalAudioStateChanged(state: Int, error: Int) {
        logDebug("Agora", "onLocalAudioStateChanged:$state   $error")
    }

    override fun onLocalUserRegistered(uid: Int, userAccount: String?) {
        logDebug("Agora", "onLocalUserRegistered:$uid  $userAccount")
    }

    override fun onStreamPublished(url: String?, error: Int) {
        logDebug("Agora", "onStreamPublished:$url $error")
    }

    override fun onClientRoleChanged(oldRole: Int, newRole: Int) {
        logDebug("Agora", "onClientRoleChanged:$oldRole $newRole")
        looperView { it.onClientRoleChanged(oldRole, newRole) }
    }


}