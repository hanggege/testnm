package com.live.player.presenter

import com.live.player.module.ListenInfo

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/27
 *
 */
interface ListenPresenter<Info : ListenInfo?> {

    /**
     * 绑定回调
     */
    fun bindView(view: ListenView)

    /**
     * 解除绑定
     */
    fun unBindView(view: ListenView)

    /**
     * 开启播放
     */
    fun start(info: Info)

    /**
     * 停止播放
     */
    fun stop()

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean

    /**
     * 获取播放信息
     */
    fun getPlayInfo(): Info
}