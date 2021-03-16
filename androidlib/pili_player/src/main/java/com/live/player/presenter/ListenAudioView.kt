package com.live.player.presenter

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/27
 * 录音回调
 */
interface ListenAudioView : ListenView {

    /**
     * 录音播放结束
     */
    fun onCompletion()

    /**
     * 缓冲信息反馈
     *
     * @param percent 缓冲进度
     */
    fun onBufferingUpdate(percent: Int) {}

    /**
     * 进度条拖动完成
     */
    fun onSeekComplete()

    /**
     * 播放状态改变
     * @param isPlaying
     */
    fun onStateChange(isPlaying: Boolean) {}
}