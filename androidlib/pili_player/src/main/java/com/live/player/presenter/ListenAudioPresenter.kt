package com.live.player.presenter

import com.live.player.module.ListenInfo

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/27
 */
interface ListenAudioPresenter<Info : ListenInfo> : ListenPresenter<Info> {
    /**
     * 暂停
     */
    fun pause()

    /**
     * 从暂停中恢复
     */
    fun resume()

    /**
     * 获取播放时长
     */
    fun getDuration(): Long

    /**
     * 获取当前播放进度
     */
    fun getProgress(): Long

    /**
     * 设置播放位置
     */
    fun seekTo(progress: Long)

}
