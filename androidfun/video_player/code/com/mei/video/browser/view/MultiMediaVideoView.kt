package com.mei.video.browser.view

import android.content.Context
import android.content.ContextWrapper
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.mei.base.network.netchange.isOpenNetwork
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.ext.screenWidth
import com.mei.video.browser.adapter.MultiMediaData
import com.mei.video.browser.adapter.MultiMediaRefresh
import com.mei.video.browser.video.IjkAspectRatio
import com.mei.wood.R
import com.pili.business.toDivide
import com.pili.pldroid.player.*
import kotlinx.android.synthetic.main.multi_video_item_layout.view.*
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/23
 */
class MultiMediaVideoView(context: Context) : FrameLayout(context), MultiMediaRefresh,
        PLOnPreparedListener,
        PLOnErrorListener,
        PLOnInfoListener,
        PLOnSeekCompleteListener,
        LifecycleEventObserver {

    init {
        layoutInflaterKtToParentAttach(R.layout.multi_video_item_layout)
    }

    var mediaData: MultiMediaData? = null
    var position = 0

    private var initPosition: Int = 0
    private var isPrepared = false
    private var isPositionSelected = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        getLifecycleOwner()?.lifecycle?.addObserver(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        getLifecycleOwner()?.lifecycle?.removeObserver(this)
        isPrepared = false
        multi_video_img.isVisible = true
        multi_play_or_pause_iv.isVisible = true
//        multi_video_view.pause()
        multi_video_view.stopPlayback()
    }

    private fun getLifecycleOwner() = (context as? LifecycleOwner)
            ?: ((context as? ContextWrapper)?.baseContext as? LifecycleOwner)

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (isPrepared) {
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (isPositionSelected) multi_play_or_pause_iv.performClick()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    multi_play_or_pause_iv.isVisible = true
                    multi_video_view.pause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    multi_video_view.stopPlayback()
                }
                else -> {
                }
            }
        }
    }

    override fun initPosition(initPosition: Int) {
        super.initPosition(initPosition)
        this.initPosition = initPosition
    }

    override fun performSelected(position: Int) {
        isPositionSelected = this.position == position
        if (isPositionSelected) {
            if (isPrepared) {
                multi_play_or_pause_iv.performClick()
            } else if (this.mediaData != null) {
                refresh(this.position, this.mediaData)
                isPositionSelected = this.position == position
            }
        } else {
            multi_play_or_pause_iv.isVisible = true
            multi_video_img.isVisible = true
            if (multi_video_view.isPlaying) {
                multi_video_view.seekTo(0)
            }
        }
    }

    override fun refresh(position: Int, data: MultiMediaData?) {
        this.position = position
        this.mediaData = data
        this.isPrepared = false
        this.isPositionSelected = position == initPosition
        multi_browser_progress.isVisible = false
        multi_play_or_pause_iv.isVisible = true
        multi_video_error_root.isVisible = false
        multi_video_progress.progress = 0

        multi_video_img.isVisible = true
        multi_video_img.updateLayoutParams { width = screenWidth }
        Glide.with(multi_video_img)
                .load(data?.url.orEmpty())
                .optionalFitCenter()
                .into(multi_video_img)
        multi_video_view.apply {
            plOnPreparedListener = this@MultiMediaVideoView
            plOnInfoListener = this@MultiMediaVideoView
            plOnErrorListener = this@MultiMediaVideoView
            plOnSeekCompleteListener = this@MultiMediaVideoView
            isLooping = true
            aspectRatio = IjkAspectRatio.FIT_WIDTH
        }
        multi_video_view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                when (multi_video_view.playerState) {
                    PlayerState.PLAYING -> {
                        multi_video_view.pause()
                        multi_play_or_pause_iv.isVisible = true
                    }
                    PlayerState.PAUSED -> multi_play_or_pause_iv.performClick()
                    else -> {
                    }
                }
            }
            true
        }
        multi_video_error_root.setOnClickListener {
            multi_play_or_pause_iv.isVisible = false
            multi_video_error_root.isVisible = !context.isOpenNetwork()
            if (context.isOpenNetwork()) {
                multi_browser_progress.isVisible = true
                multi_video_view.setVideoPath(data?.mediaUrl.orEmpty())
            }
        }
        multi_video_error_root.performClick()
        multi_play_or_pause_iv.setOnClickListener {
            multi_video_view.start()
            it.isVisible = false
        }
    }

    override fun onPrepared(p0: Int) {
        isPrepared = true
        if (isPositionSelected) multi_play_or_pause_iv.performClick()
        if (initPosition == position) {
            multi_play_or_pause_iv.performClick()
            initPosition = -1
        }
        multi_video_error_root.isVisible = false
        multi_browser_progress.isVisible = false
    }

    override fun onError(what: Int): Boolean {
        if (what != PLOnErrorListener.ERROR_CODE_IO_ERROR) {
            multi_video_error_root.isVisible = true
            multi_play_or_pause_iv.isVisible = false
            multi_browser_progress.isVisible = false
        }
        return true
    }

    override fun onInfo(what: Int, p1: Int) {
        when (what) {
            IMediaPlayer.MEDIA_INFO_BUFFERING_START -> multi_browser_progress.isVisible = true
            IMediaPlayer.MEDIA_INFO_BUFFERING_END,
            IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> multi_browser_progress.isVisible = false
            10086 -> {
                multi_video_progress.progress = (multi_video_view.currentPosition * multi_video_progress.max / multi_video_view.duration.toDivide()).toInt()
                if (multi_video_view.currentPosition > 300 && multi_video_img.isVisible) {
                    multi_video_img.isVisible = false
                }
                if (!isPositionSelected) multi_video_view.pause()
                else if (!isAttachedToWindow) multi_video_view.stopPlayback()
            }
        }
    }

    override fun onSeekComplete() {
        if (!isPositionSelected) {
            multi_video_img.isVisible = true
            multi_video_view.pause()
        }
    }


}