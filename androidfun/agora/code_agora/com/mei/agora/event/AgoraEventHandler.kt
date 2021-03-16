package com.mei.agora.event

import io.agora.rtc.IRtcEngineEventHandler

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-11-25
 */
interface AgoraEventHandler {

    fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {}

    fun onRejoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {}

    fun onLeaveChannel(stats: IRtcEngineEventHandler.RtcStats?) {}

    /**
     * @param state 远端视频流状态:
    REMOTE_VIDEO_STATE_STOPPED(0)：远端视频默认初始状态。在 REMOTE_VIDEO_STATE_REASON_LOCAL_MUTED(3)、REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED(5) 或 REMOTE_VIDEO_STATE_REASON_REMOTE_OFFLINE(7) 的情况下，会报告该状态。
    REMOTE_VIDEO_STATE_STARTING(1)：本地用户已接收远端视频首包
    REMOTE_VIDEO_STATE_DECODING(2)：远端视频流正在解码，正常播放。在 REMOTE_VIDEO_STATE_REASON_NETWORK_RECOVERY(2)、REMOTE_VIDEO_STATE_REASON_LOCAL_UNMUTED(4)、REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED(6) 或 REMOTE_VIDEO_STATE_REASON_AUDIO_FALLBACK_RECOVERY(9) 的情况下，会报告该状态
    REMOTE_VIDEO_STATE_FROZEN(3)：远端视频流卡顿。在 REMOTE_VIDEO_STATE_REASON_NETWORK_CONGESTION(1) 或 REMOTE_VIDEO_STATE_REASON_AUDIO_FALLBACK(8) 的情况下，会报告该状态
    REMOTE_VIDEO_STATE_FAILED(4)：远端视频流播放失败。在 REMOTE_VIDEO_STATE_REASON_INTERNAL(0) 的情况下，会报告该状态。
     *
     * @param reason 远端视频流状态改变的具体原因：
    REMOTE_VIDEO_STATE_REASON_INTERNAL(0)：内部原因
    REMOTE_VIDEO_STATE_REASON_NETWORK_CONGESTION(1)：网络阻塞
    REMOTE_VIDEO_STATE_REASON_NETWORK_RECOVERY(2)：网络恢复正常
    REMOTE_VIDEO_STATE_REASON_LOCAL_MUTED(3)：本地用户停止接收远端视频流或本地用户禁用视频模块
    REMOTE_VIDEO_STATE_REASON_LOCAL_UNMUTED(4)：本地用户恢复接收远端视频流或本地用户启动视频模块
    REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED(5)：远端用户停止发送视频流或远端用户禁用视频模块
    REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED(6)：远端用户恢复发送视频流或远端用户启用视频模块
    REMOTE_VIDEO_STATE_REASON_REMOTE_OFFLINE(7)：远端用户离开频道
    REMOTE_VIDEO_STATE_REASON_AUDIO_FALLBACK(8)：远端视频流已回退为音频流
    REMOTE_VIDEO_STATE_REASON_AUDIO_FALLBACK_RECOVERY(9)：回退的远端音频流恢复为视频流
     *
     */
    fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {}

    fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {}

    fun onUserJoined(uid: Int, elapsed: Int) {}

    fun onUserOffline(uid: Int, reason: Int) {}

    /**
     * @param uid 用户 ID。表示该回调报告的是持有该 ID 的用户的网络质量。当 uid 为 0 时，返回的是本地用户的网络质量。
     * @param txQuality 该用户的上行网络质量，基于上行视频的发送码率、上行丢包率、平均往返时延和网络抖动计算。该值代表当前的上行网络质量，帮助判断是否可以支持当前设置的视频编码属性。假设直播模式下上行码率是 1000 Kbps，那么支持 640 × 480 的分辨率、30 fps 的帧率没有问题，但是支持 1280 x 720 的分辨率就会有困难
    QUALITY_UNKNOWN(0)：质量未知
    QUALITY_EXCELLENT(1)：质量极好
    QUALITY_GOOD(2)：用户主观感觉和极好差不多，但码率可能略低于极好
    QUALITY_POOR(3)：用户主观感受有瑕疵但不影响沟通
    QUALITY_BAD(4)：勉强能沟通但不顺畅
    QUALITY_VBAD(5)：网络质量非常差，基本不能沟通
    QUALITY_DOWN(6)：网络连接断开，完全无法沟通
    QUALITY_DETECTING(8)：SDK 正在探测网络质量
     *
     * @param rxQuality 该用户的下行网络质量，基于下行网络的丢包率、平均往返延时和网络抖动计算
    QUALITY_UNKNOWN(0)：质量未知
    QUALITY_EXCELLENT(1)：质量极好
    QUALITY_GOOD(2)：用户主观感觉和极好差不多，但码率可能略低于极好
    QUALITY_POOR(3)：用户主观感受有瑕疵但不影响沟通
    QUALITY_BAD(4)：勉强能沟通但不顺畅
    QUALITY_VBAD(5)：网络质量非常差，基本不能沟通
    QUALITY_DOWN(6)：网络连接断开，完全无法沟通
    QUALITY_DETECTING(8)：SDK 正在探测网络质量
     */
    fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {}

    /**
     * 远端用户停止/恢复发送音频流回调。
     * 提示有其他用户将他的音频流静音/取消静音。
     * 该回调是由远端用户调用 muteLocalAudioStream 方法关闭或开启音频发送触发的。
     * @param 该用户是否静音：true: 该用户已静音音频  false: 该用户已取消音频静音
     */
    fun onUserMuteAudio(uid: Int, muted: Boolean) {}

    /**
     * 已发送本地音频首帧回调。
     */
    fun onFirstLocalAudioFrame(elapsed: Int) {}

    /**
     * 通话中本地视频流的统计信息回调。该回调描述本地设备发送视频流的统计信息，每 2 秒触发一次。
     */
    fun onLocalVideoStats(stats: IRtcEngineEventHandler.LocalVideoStats?) {}

    fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {}

    /**
     * 直播场景下用户角色已切换回调。如从观众切换为主播，反之亦然。
    该回调由本地用户在加入频道后调用 setClientRole 改变用户角色触发的。
     */
    fun onClientRoleChanged(oldRole: Int, newRole: Int) {}

    /**
     * state	当前的网络连接状态：
    CONNECTION_STATE_DISCONNECTED(1)：网络连接断开
    CONNECTION_STATE_CONNECTING(2)：建立网络连接中
    CONNECTION_STATE_CONNECTED(3)：网络已连接
    CONNECTION_STATE_RECONNECTING(4)：重新建立网络连接中
    CONNECTION_STATE_FAILED(5)：网络连接失败
    reason	引起当前网络连接状态发生改变的原因：
    CONNECTION_CHANGED_CONNECTING(0)：建立网络连接中
    CONNECTION_CHANGED_JOIN_SUCCESS(1)：成功加入频道
    CONNECTION_CHANGED_INTERRUPTED(2)：网络连接中断
    CONNECTION_CHANGED_BANNED_BY_SERVER(3)：网络连接被服务器禁止
    CONNECTION_CHANGED_JOIN_FAILED(4)：加入频道失败
    CONNECTION_CHANGED_LEAVE_CHANNEL(5)：离开频道
    CONNECTION_CHANGED_INVALID_APP_ID(6)：不是有效的 APP ID。请更换有效的 APP ID 重新加入频道
    CONNECTION_CHANGED_INVALID_CHANNEL_NAME(7)：不是有效的频道名。请更换有效的频道名重新加入频道
    CONNECTION_CHANGED_INVALID_TOKEN(8)：生成的 Token 无效。一般有以下原因：
    在控制台上启用了 App Certificate，但加入频道未使用 Token。当启用了 App Certificate，必须使用 Token
    在调用 joinChannel 加入频道时指定的 uid 与生成 Token 时传入的 uid 不一致
    CONNECTION_CHANGED_TOKEN_EXPIRED(9)：当前使用的 Token 过期，不再有效，需要重新在你的服务端申请生成 Token
    CONNECTION_CHANGED_REJECTED_BY_SERVER(10)：此用户被服务器禁止
    CONNECTION_CHANGED_SETTING_PROXY_SERVER(11)：由于设置了代理服务器，SDK 尝试重连
    CONNECTION_CHANGED_RENEW_TOKEN(12)：更新 Token 引起网络连接状态改变
    CONNECTION_CHANGED_CLIENT_IP_ADDRESS_CHANGED(13)：客户端 IP 地址变更，可能是由于网络类型，或网络运营商的 IP 或端口发生改变引起
    CONNECTION_CHANGED_KEEP_ALIVE_TIMEOUT(14)：SDK 和服务器连接保活超时，进入自动重连状态
     */
    fun onConnectionStateChanged(state: Int, reason: Int) {}

    /**
     * 网络连接中断，且 SDK 无法在 10 秒内连接服务器回调。
    SDK 在调用 joinChannel() 后，无论是否加入成功，只要 10 秒和服务器无法连接就会触发该回调。 与 onConnectionInterrupted 回调的区别是：
    onConnectionInterrupted 回调一定是发生在 joinChannel() 成功后，且 SDK 刚失去和服务器连接 4 秒时触发
    onConnectionLost 回调是无论之前 joinChannel() 是否连接成功，只要 10 秒内和服务器无法建立连接都会触发
    如果 SDK 在断开连接后，20 分钟内还是没能重新加入频道，SDK 会停止尝试重连。
     */
    fun onConnectionLost() {}

    /**
     * 提示频道内谁正在说话、说话者音量及本地用户是否在说话的回调。
    该回调提示频道内瞬时音量最高的几个用户（最多 3 个）的用户 ID、他们的音量及本地用户是否在说话。
    该回调默认禁用。可以通过启用说话者音量提示 enableAudioVolumeIndication 方法开启； 开启后，无论频道内是否有人说话，都会按方法中设置的时间间隔返回提示音量。
    每次触发，用户会收到两个独立的 onAudioVolumeIndication 回调，其中一个包含本地用户的音量信息，另一个包含远端所有用户的音量信息，详见下方参数描述。
     */
    fun onAudioVolumeIndication(speakers: Array<IRtcEngineEventHandler.AudioVolumeInfo>?, totalVolume: Int) {}

    /**
     * 当前通话统计回调。 该回调在通话中每两秒触发一次
     */
    fun onRtcStats(stats: IRtcEngineEventHandler.RtcStats?) {}

    /**
     * 远端用户开/关本地视频采集回调。
     */
    fun onUserEnableLocalVideo(uid: Int, enabled: Boolean) {}

    /**
     * 表示 SDK 运行时出现了（网络或媒体相关的）错误。通常情况下，SDK 上报的错误意味着 SDK 无法自动恢复，需要 App 干预或提示用户。
     */
    fun onError(err: Int) {}
}