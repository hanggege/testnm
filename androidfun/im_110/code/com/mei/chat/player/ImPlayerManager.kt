package com.mei.chat.player

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.mei.chat.player.control.IPlayerControl
import com.mei.chat.player.control.MediaPlayerControl

/**
 * Created by zzw on 2019/3/13
 * Des:
 */
class ImPlayerManager(activity: FragmentActivity) {

    var isPlaying = false
    var playPos: Int = -1

    // : zzw 2019/3/19 设置播放位置以及红点
    var onPlayStart: (pos: Int) -> Unit = { }

    //type -1异常结束 0手动结束  1播放完毕
    var onPlayFinish: (imPlayerManager: ImPlayerManager, type: Int, pos: Int) -> Unit = { _, _, _ -> }

    private val mPlayerProxy: PlayerProxy by lazy {
        PlayerProxy(MediaPlayerControl(object : IPlayerControl.OnPlayListener {
            override fun onPlayFinish(type: Int) {
                isPlaying = false
                onPlayFinish(this@ImPlayerManager, type, playPos)
            }

            override fun onPlayStart() {
                isPlaying = true
                onPlayStart(playPos)
            }
        }))
    }

    fun play(path: String, pos: Int) {
        mPlayerProxy.play(path)
        this.playPos = pos
    }

    fun stop() {
        mPlayerProxy.stopPlay()
    }

    init {
        activity.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                mPlayerProxy.stopPlay()
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                mPlayerProxy.release()
            }
        })
    }


    private inner class PlayerProxy internal constructor(private val mPlayerControl: IPlayerControl) : IPlayerControl {

        override fun play(path: String) {
            if (isPlaying) {
                stopPlay()
            }
            mPlayerControl.play(path)
        }

        override fun stopPlay() {
            mPlayerControl.stopPlay()
        }

        override fun release() {
            mPlayerControl.release()
        }
    }


}
