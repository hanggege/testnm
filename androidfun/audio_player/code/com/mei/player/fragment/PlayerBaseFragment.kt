package com.mei.player.fragment

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.live.player.presenter.ListenAudioView
import com.mei.player.controller.AudioController
import com.mei.wood.ui.fragment.TabItemFragment
import com.mei.wood.util.AppManager
import com.net.model.broadcast.PlayType
import com.pili.business.handleErrorCode
import com.pili.isPlaySelf
import com.pili.pldroid.player.PLOnInfoListener

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/3
 *
 * update 2020/09/24 version 2.4.0 为了电台页面也能使用，更改PlayerBaseFragment 所继承的父类为TabItemFragment
 * 因为电台页面可能会单独的tab页面存在
 */
abstract class PlayerBaseFragment : TabItemFragment(), ListenAudioView {
    /** 以下为控制器传入的控件，基本是用来展示用的 **/
    abstract val currentTime: TextView
    abstract val totalTime: TextView
    abstract val audioProgress: SeekBar
    abstract val playBtn: View

    @PlayType
    abstract val playType: String
    abstract var playAudioId: Int

    /** 是否是当前播放id **/
    var isPlaySelf: Boolean
        set(_) {}
        get() = playType.isPlaySelf(playAudioId)

    val controller: AudioController by lazy {
        object : AudioController(this, audioProgress, playBtn, currentTime, totalTime) {
            override fun setSeekEnabled(enabled: Boolean) {
                super.setSeekEnabled(enabled)
                showLoading(!enabled)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller.resetView()

    }

    /****************************[ListenAudioView]************************************/
    override fun onSeekComplete() {
        controller.setSeekEnabled(true)
    }

    override fun onPrepared(preparedTime: Int) {
        if (isPlaySelf) {
            controller.setSeekEnabled(true)
            controller.refreshUI()
            showLoading(false)
        } else {
            controller.resetView()
        }
    }

    override fun onStopPlayer() {
        controller.resetView()
    }

    override fun onInfo(what: Int, extra: Int) {
        if (what == PLOnInfoListener.MEDIA_INFO_BUFFERING_START) {
            /** 音频加载中 **/
            showLoading(true)
            controller.setSeekEnabled(false)
        } else if (what == PLOnInfoListener.MEDIA_INFO_BUFFERING_END || what == PLOnInfoListener.MEDIA_INFO_AUDIO_RENDERING_START) {
            /** 音频加载完成 **/
            showLoading(false)
            controller.setSeekEnabled(true)
        }
    }

    override fun onError(code: Int) {
        /** 播放出错弹错误提示 **/
        if (AppManager.getInstance().currentActivity() == activity) {
            handleErrorCode(code)
        }
    }


}