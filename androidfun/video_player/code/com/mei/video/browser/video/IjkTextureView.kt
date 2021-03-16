package com.mei.video.browser.video

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.*
import com.pili.pldroid.player.PLOnErrorListener
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/8/27
 */

class IjkTextureView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : IjkVideoView(context, attrs, defStyleAttr) {

    private var videoSurface: Surface? = null
    private val textureView: IjkRenderTextureView by lazy {
        IjkRenderTextureView(context).apply {
            surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                    videoSurface = Surface(surface)
                    if (!isPrepared && waitUrl.isNotEmpty()) {
                        realStartPlayer(waitUrl)
                    }
                }

                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {

                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                    videoSurface?.release()
                    videoSurface = null
                    return false
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

                }

            }
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    init {
        gravity = Gravity.CENTER
        addView(textureView, 0)
    }

    override var renderView: View
        get() = textureView
        set(_) {}

    override fun initView() {

    }

    private var waitUrl = ""

    override fun setVideoPath(url: String) {
        isPrepared = false
        mediaPlayer?.reset()
        textureView.rotation = 0f
        renderView.requestLayout()
        preparedTime = System.currentTimeMillis()
        waitUrl = url
        if (videoSurface != null) {
            realStartPlayer(url)
        }
    }

    private fun realStartPlayer(url: String) {
        try {
            if (mediaPlayer == null) mediaPlayer = createPlayer()
            mediaPlayer?.apply {
                setSurface(videoSurface)
                dataSource = if (checkProxyEnable()) httpProxyCacheServer.getProxyUrl(url) else url
                isLooping = this@IjkTextureView.isLooping
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            plOnErrorListener?.onError(PLOnErrorListener.ERROR_CODE_OPEN_FAILED)
        }
    }


    override fun onInfo(player: IMediaPlayer, what: Int, extra: Int): Boolean {
        if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
            textureView.rotation = extra.toFloat()
            renderView.requestLayout()
        }
        return super.onInfo(player, what, extra)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        renderView.requestLayout()
    }

    inner class IjkRenderTextureView(context: Context) : TextureView(context) {
        @SuppressLint("DrawAllocation")
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val needRotation = textureView.rotation % 360 == 90f || textureView.rotation % 360 == 270f
            val w = this@IjkTextureView.measuredWidth
            val h = this@IjkTextureView.measuredHeight

            val superWH = if (needRotation) Pair(h, w)
            else Pair(w, h)

            val videoWidth = mediaPlayer?.videoWidth ?: 0
            val videoHeight = mediaPlayer?.videoHeight ?: 0
            if (videoWidth <= 0 || videoHeight <= 0) {
                setMeasuredDimension(w, h)
            } else {
                val wh = when (aspectRatio) {
                    IjkAspectRatio.FIT_HEIGHT -> {
                        if (needRotation) Pair(h, h * videoHeight / videoWidth)
                        else Pair(h * videoWidth / videoHeight, h)
                    }
                    IjkAspectRatio.FIT_WIDTH -> {
                        if (needRotation) Pair(w * videoWidth / videoHeight, w)
                        else Pair(w, w * videoHeight / videoWidth)
                    }
                    else -> aspectRatio.doMeasure(superWH.first, superWH.second,
                            mediaPlayer?.videoWidth ?: 0, mediaPlayer?.videoHeight ?: 0)
                }
                setMeasuredDimension(wh.first, wh.second)
            }
        }
    }
}