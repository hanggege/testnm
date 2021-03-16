package com.live.player.module

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/26
 */
interface ListenInfo {

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean

    /**
     * 获取音频url
     */
    fun url(): String

    /**
     * 获取播放音频的唯一标识
     */
    fun primaryKey(): String

    /**
     * 音频开始位置
     */
    fun startPosition(): Int = 0
}
