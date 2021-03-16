package com.mei.video.browser.video

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import com.danikula.videocache.HttpProxyCacheServer
import com.mei.init.meiApplication
import com.mei.wood.BuildConfig
import com.mei.wood.TimingRunnable
import com.pili.pldroid.player.*
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/8/27
 */
val httpProxyCacheServer by lazy {
    HttpProxyCacheServer.Builder(meiApplication().applicationContext)
            .maxCacheSize(1024 * 1024 * 1024)
            .build()
}

open class IjkVideoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr),
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnVideoSizeChangedListener {

    private val surfaceView: IjkSurfaceView by lazy {
        IjkSurfaceView(context).apply {
            holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder?) {
                }

                override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                    mediaPlayer?.setDisplay(holder)
                }

                override fun surfaceDestroyed(holder: SurfaceHolder?) {
                }

            })
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    init {
        initView()
    }

    open fun initView() {
        gravity = Gravity.CENTER
        addView(surfaceView, 0)
    }

    val timerProgress by lazy { TimingRunnable(0.2f) { plOnInfoListener?.onInfo(10086, 0) } }
    var mediaPlayer: IMediaPlayer? = null
    var isPrepared = false
    var preparedTime = 0L

    var plOnPreparedListener: PLOnPreparedListener? = null
    var plOnErrorListener: PLOnErrorListener? = null
    var plOnInfoListener: PLOnInfoListener? = null
    var plOnCompletionListener: PLOnCompletionListener? = null
    var plOnSeekCompleteListener: PLOnSeekCompleteListener? = null

    open var renderView: View = surfaceView
    open var aspectRatio = IjkAspectRatio.FIT_PARENT
        set(value) {
            field = value
            renderView.requestLayout()
        }
    var isPlaying
        set(_) {}
        get() = mediaPlayer?.isPlaying ?: false
    var currentPosition
        set(_) {}
        get() = mediaPlayer?.currentPosition ?: 0
    var duration
        set(_) {}
        get() = mediaPlayer?.duration ?: 0
    var playerState: PlayerState
        set(_) {}
        get() =
            when {
                mediaPlayer == null -> PlayerState.DESTROYED
                mediaPlayer?.isPlaying == true -> PlayerState.PLAYING
                !isPrepared -> PlayerState.PREPARING
                isPrepared -> PlayerState.PAUSED
                else -> PlayerState.IDLE
            }
    var isLooping = false
        set(value) {
            field = value
            try {
                mediaPlayer?.isLooping = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    open fun setVideoPath(url: String) {
        try {
            isPrepared = false
            mediaPlayer?.reset()
            preparedTime = System.currentTimeMillis()
            if (mediaPlayer == null) mediaPlayer = createPlayer()
            mediaPlayer?.apply {
                setDisplay(surfaceView.holder)
                dataSource = if (checkProxyEnable()) httpProxyCacheServer.getProxyUrl(url) else url
                prepareAsync()
                isLooping = this@IjkVideoView.isLooping
            }
        } catch (e: Exception) {
            e.printStackTrace()
            plOnErrorListener?.onError(PLOnErrorListener.ERROR_CODE_OPEN_FAILED)
        }
    }

    fun start() {
        mediaPlayer?.start()
        timerProgress.start(0.2f)
    }

    fun pause() {
        mediaPlayer?.pause()
        timerProgress.cancel()
    }

    fun stopPlayback() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        timerProgress.cancel()
    }

    fun seekTo(time: Long) {
        mediaPlayer?.seekTo(time)
    }

    fun createPlayer(): IMediaPlayer {
        return IjkMediaPlayer().apply {
            IjkMediaPlayer.native_setLogLevel(if (BuildConfig.IS_TEST) IjkMediaPlayer.IJK_LOG_DEBUG
            else IjkMediaPlayer.IJK_LOG_ERROR)

            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)
            setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 20 * 1024)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "reconnect", 5)
            // max-buffer-size这里设置了会出现部分视频没图像
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 30)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek")
            setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5)

            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_mpeg4", 0)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0)

            setOnPreparedListener(this@IjkVideoView)
            setOnErrorListener(this@IjkVideoView)
            setOnCompletionListener(this@IjkVideoView)
            setOnInfoListener(this@IjkVideoView)
            setOnSeekCompleteListener(this@IjkVideoView)
            setOnVideoSizeChangedListener(this@IjkVideoView)
        }
    }

    override fun onPrepared(player: IMediaPlayer?) {
        isPrepared = true
        preparedTime = System.currentTimeMillis() - preparedTime
        plOnPreparedListener?.onPrepared(preparedTime.toInt())
    }

    override fun onError(player: IMediaPlayer?, what: Int, extra: Int): Boolean {
        plOnErrorListener?.onError(what)
        return true
    }

    override fun onCompletion(player: IMediaPlayer?) {
        plOnCompletionListener?.onCompletion()
    }

    override fun onInfo(player: IMediaPlayer, what: Int, extra: Int): Boolean {
        if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            timerProgress.start()
        }
        plOnInfoListener?.onInfo(what, extra)
        return true
    }

    override fun onSeekComplete(player: IMediaPlayer?) {
        plOnSeekCompleteListener?.onSeekComplete()
    }

    override fun onVideoSizeChanged(player: IMediaPlayer, p1: Int, p2: Int, p3: Int, p4: Int) {
        renderView.requestLayout()
    }

    /**
     * 提前检查是否可用
     */
    fun checkProxyEnable(): Boolean {
        val cacheRoot = StorageUtils.getIndividualCacheDirectory(context)
        cacheRoot.mkdirs()
        return cacheRoot.exists() || cacheRoot.mkdirs()
    }

    inner class IjkSurfaceView(context: Context) : SurfaceView(context) {
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//            val wSpecSize = MeasureSpec.getSize(widthMeasureSpec)
//            val hSpecSize = MeasureSpec.getSize(heightMeasureSpec)
            val wh = aspectRatio.doMeasure(this@IjkVideoView.measuredWidth, this@IjkVideoView.measuredHeight,
                    mediaPlayer?.videoWidth ?: 0, mediaPlayer?.videoHeight ?: 0)
            setMeasuredDimension(wh.first, wh.second)

        }
    }


}
