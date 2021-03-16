package com.mei.chat.player.control

/**
 * Created by zzw on 2019/3/13
 * Des:
 */
interface IPlayerControl {
    fun play(path: String)

    fun stopPlay()

    fun release()


    interface OnPlayListener {
        /**
         * @param type -1 播放异常 1正常播放结束 0手动点击播放停止
         */
        fun onPlayFinish(type: Int)

        fun onPlayStart()
    }
}
