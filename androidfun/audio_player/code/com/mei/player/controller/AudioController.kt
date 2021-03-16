package com.mei.player.controller

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import android.widget.TextView
import com.mei.orc.util.date.formatTimeToHMS
import com.mei.wood.extensions.subscribeBy
import com.pili.PlayInfo
import com.pili.getPlayInfo
import com.pili.player
import com.trello.rxlifecycle3.LifecycleProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/10
 */
open class AudioController(life: LifecycleProvider<*>,
                           val seekBar: SeekBar,
                           val playBtn: View,
                           val currTime: TextView?,
                           val endTime: TextView?) {


    /** 当前播放的key 对应[PlayInfo.mAudioId] **/
    var audio_id: Int = -1
        set(value) {
            field = value
            if (!isDragging && audio_id == getPlayInfo().mAudioId) {
                // 刷新 UI
                refreshUI()
            }
        }
    var isDragging = false
    var isPlaying: Boolean
        set(_) {}
        get() = player.isPlaying()

    private val rotateAnim: RotateAnimation by lazy {
        RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            interpolator = LinearInterpolator()
            duration = 1500
            repeatCount = Animation.INFINITE
            fillAfter = true
        }
    }

    init {
        Observable.interval(1, TimeUnit.SECONDS)
                .compose(life.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    if (!isDragging && audio_id == getPlayInfo().mAudioId) {
                        // 刷新 UI
                        refreshUI()
                    }
                }

        seekBar.max = 1000
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currTime?.text = (player.getDuration() * progress / seekBar.max / 1000).toInt().formatTimeToHMS()
                }
            }

            override fun onStartTrackingTouch(bar: SeekBar?) {
                isDragging = true
            }

            override fun onStopTrackingTouch(bar: SeekBar?) {
                isDragging = false
                seekTo(player.getDuration() * seekBar.progress / seekBar.max)
            }

        })
    }

    @Suppress("SENSELESS_COMPARISON")
    fun refreshUI() {
        if (seekBar != null && playBtn != null) {
            var position = player.getProgress()
            var duration = player.getDuration()

            if (position > duration || duration - position < 1000) {
                position = duration
            }
            if (duration <= 0) {
                duration = 1
            } else if (position <= 0) {
                position = 0
            }
            seekBar.progress = (seekBar.max * position / duration).toInt()
            playBtn.isActivated = player.isPlaying()
            playBtn.isSelected = player.isPlaying()
            currTime?.text = (position / 1000).toInt().formatTimeToHMS()
            endTime?.text = (duration / 1000).toInt().formatTimeToHMS()
        }

    }


    fun changePlay(goPlay: Boolean) {
        if (goPlay) player.resume()
        else player.pause()
    }

    fun seekTo(position: Long) {
        if (seekBar.isEnabled) {
            if (!player.isPlaying()) player.resume()
            player.seekTo(position)
            setSeekEnabled(false)
            refreshUI()

        }
    }

    open fun setSeekEnabled(enabled: Boolean) {
        seekBar.isEnabled = enabled
    }

    fun resetView() {
        seekBar.progress = 0
        playBtn.isActivated = false
        playBtn.isSelected = false
        currTime?.text = 0.formatTimeToHMS()
        endTime?.text = 0.formatTimeToHMS()
    }

}
