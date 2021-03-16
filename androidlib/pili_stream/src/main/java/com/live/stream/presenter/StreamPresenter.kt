package com.live.stream.presenter

import com.live.stream.module.StreamInfo

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/26
 */
interface StreamPresenter<Info : StreamInfo?> {

    /**
     * 绑定回调
     */
    fun bindView(view: StreamView)

    /**
     * 解除绑定
     */
    fun unBindView(view: StreamView)

    /**
     * 开启推流
     */
    fun start(info: Info)

    /**
     * 停止推流
     */
    fun stop()

    /**
     * 是否正在推流
     */
    fun isStreaming(): Boolean

    /**
     * 获取推流信息
     */
    fun getPlayInfo(): Info

    /**
     * 重置推流准备状态，下次连接需要重新Ready
     */
    fun resetReady()

}