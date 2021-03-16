package com.mei.chat.player.control

import android.media.MediaPlayer
import android.text.TextUtils

/**
 * Created by zzw on 2019/3/13
 * Des:
 */
class MediaPlayerControl(private val mOnPlayListener: IPlayerControl.OnPlayListener) : IPlayerControl, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private val mediaPlayer: MediaPlayer by lazy { MediaPlayer() }

    override fun play(path: String) {
        try {
            if (!TextUtils.isEmpty(path)) {
                mediaPlayer.setDataSource(path)
                mediaPlayer.setOnPreparedListener(this)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnCompletionListener(this@MediaPlayerControl)
                mediaPlayer.setOnErrorListener(this@MediaPlayerControl)
            } else {
                mOnPlayListener.onPlayFinish(-1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stop(-1)
        }
    }

    override fun stopPlay() {
        stop(0)
    }


    override fun release() {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @param type -1异常结束 0手动结束  1播放完毕
     */
    private fun stop(type: Int) {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mOnPlayListener.onPlayFinish(type)
        }
    }

    override fun onCompletion(mp: MediaPlayer) {
        stop(1)
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        stop(-1)
        return false
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mOnPlayListener.onPlayStart()
        mediaPlayer.start()
    }
}
