package com.live.stream.module

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/26
 */
interface StreamInfo {

    /**
     * 是否正在推流
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
}
