package com.live.player.presenter

import android.os.Handler
import android.os.Looper

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/27
 */
interface ListenView {

    /**
     * 音频准备完成,下一步则可以调用播放器的 start() 启动播放。
     * @param preparedTime prepared time: ms
     */
    fun onPrepared(preparedTime: Int)

    /**
     * 调用停止播放器
     */
    fun onStopPlayer()

    /**
     * 监听播放器的状态消息
     *
     * @param what [com.pili.pldroid.player.PLOnInfoListener]
     *
     * MEDIA_INFO_UNKNOWN	  	     	     1	             未知消息
     * MEDIA_INFO_VIDEO_RENDERING_START		 3	             第一帧视频已成功渲染
     * MEDIA_INFO_CONNECTED		     	     200             连接成功
     * MEDIA_INFO_METADATA		     	     340             读取到 metadata 信息
     * MEDIA_INFO_BUFFERING_START		     701             开始缓冲
     * MEDIA_INFO_BUFFERING_END		     	 702             停止缓冲
     * MEDIA_INFO_SWITCHING_SW_DECODE	     802             硬解失败，自动切换软解
     * MEDIA_INFO_CACHE_DOWN		     	 901             预加载完成
     * MEDIA_INFO_LOOP_DONE		     	     8088            loop 中的一次播放完成
     * MEDIA_INFO_VIDEO_ROTATION_CHANGED	 10001           获取到视频的播放角度
     * MEDIA_INFO_AUDIO_RENDERING_START	     10002           第一帧音频已成功播放
     * MEDIA_INFO_VIDEO_GOP_TIME		     10003           获取视频的I帧间隔
     * MEDIA_INFO_VIDEO_BITRATE		         20001           视频的码率统计结果
     * MEDIA_INFO_VIDEO_FPS		     	     20002           视频的帧率统计结果
     * MEDIA_INFO_AUDIO_BITRATE		     	 20003           音频的帧率统计结果
     * MEDIA_INFO_AUDIO_FPS		     	     20004           音频的帧率统计结果
     * MEDIA_INFO_VIDEO_FRAME_RENDERING	     10004           视频帧的时间戳
     * MEDIA_INFO_AUDIO_FRAME_RENDERING	     10005           音频帧的时间戳
     * MEDIA_INFO_CACHED_COMPLETE		     1345            离线缓存的部分播放完成
     * MEDIA_INFO_IS_SEEKING		     	 565             上一次 seekTo 操作尚未完成
     */
    fun onInfo(what: Int, extra: Int)

    /**
     * 听流发生错误回调
     * 对于直播应用而言，播放器本身是无法判断直播是否结束，这需要通过业务服务器来告知。当知心达人端停止推流后，播放器会因为读取不到新的数据而产生超时，从而触发 ERROR_CODE_IO_ERROR 回调。
     * 建议的处理方式是：在 ERROR_CODE_IO_ERROR 回调后，查询业务服务器，获知直播是否结束，如果已经结束，则关闭播放器，清理资源；如果直播没有结束，则等待 SDK 内部自动做重连。
     * @param code [com.pili.pldroid.player.PLOnErrorListener]
     *
     * MEDIA_ERROR_UNKNOWN		     	     -1		     未知错误
     * ERROR_CODE_OPEN_FAILED		     	 -2		     播放器打开失败
     * ERROR_CODE_IO_ERROR		     	     -3		     网络异常
     * ERROR_CODE_SEEK_FAILED		     	 -4		     拖动失败
     * ERROR_CODE_CACHE_FAILED		     	 -5		     预加载失败
     * ERROR_CODE_HW_DECODE_FAILURE		     -2003		 硬解失败
     * ERROR_CODE_PLAYER_DESTROYED		     -2008		 播放器已被销毁，需要再次 setVideoURL 或 prepareAsync
     * ERROR_CODE_PLAYER_VERSION_NOT_MATCH	 -9527		 so 库版本不匹配，需要升级
     * ERROR_CODE_PLAYER_CREATE_AUDIO_FAILED -4410		 AudioTrack 初始化失败，可能无法播放音频¬
     */
    fun onError(code: Int)


}

internal fun runOnUiThread(action: Runnable) = Handler(Looper.getMainLooper()).post(action)
internal fun runDelayedOnUiThread(delayMillis: Long, action: Runnable) = Handler(Looper.getMainLooper()).postDelayed(action, delayMillis)