package com.mei.im.ui

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.core.view.isVisible
import com.mei.chat.ext.EXTRA_IMAGE_FILE_PATH
import com.mei.chat.ext.EXTRA_VIDEO_ASK_SEND
import com.mei.orc.ActivityLauncher
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import kotlinx.android.synthetic.main.activity_video.*
import launcher.Boom
import launcher.MakeResult
import launcher.MulField

@MakeResult(includeStartForResult = true)
class IMVideoActivity : MeiCustomActivity(), SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    @MulField
    @Boom
    var askSend: Boolean = false

    @Boom
    var path: String? = null

    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityLauncher.bind(this)
        setContentView(R.layout.activity_video)

        play_video_surfaceview.holder.addCallback(this)

        play_video_bottom_layout.isVisible = askSend
        play_video_cancel_send_video.setOnClickListener {
            result(false)
        }
        play_video_commit_send_video.setOnClickListener {
            result(true)
        }

        player = MediaPlayer().apply {
            try {
                setOnPreparedListener(this@IMVideoActivity)
                setOnCompletionListener(this@IMVideoActivity)
                isLooping = false
                setDataSource(path)
            } catch (e: Exception) {
                e.printStackTrace()
                finish()
            }
        }

        play_video_play_btn.setOnClickListener {
            play_video_play_btn.isVisible = false
            player?.start()
        }
    }

    private fun result(isSend: Boolean) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(EXTRA_IMAGE_FILE_PATH, path)
            putExtra(EXTRA_VIDEO_ASK_SEND, isSend)
        })
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }


    override fun surfaceCreated(helper: SurfaceHolder) {
        player?.apply {
            try {
                setDisplay(helper)
                prepareAsync()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer) {
        player?.start()
    }

    override fun onCompletion(mp: MediaPlayer) {
        play_video_play_btn.isVisible = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!askSend || TextUtils.isEmpty(path)) {
            finish()
        }
        return false
    }

    override fun surfaceChanged(helper: SurfaceHolder, format: Int, width: Int, height: Int) {

    }


    override fun surfaceDestroyed(helper: SurfaceHolder) {

    }

}
