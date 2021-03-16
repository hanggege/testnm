package com.mei.im.videorecoder

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import com.joker.im.utils.SaveTYPE
import com.joker.im.utils.getSaveFilePath
import com.mei.im.videorecoder.preview.SurfaceViewPreview
import com.mei.im.videorecoder.recode.VideoRecoderCamera
import com.mei.im.videorecoder.recode.VideoRecoderImpl
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 *  Created by zzw on 2019-07-04
 *  Des:
 */

class VideoRecodeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), VideoRecoderImpl.Callback {


    private val TAG = "VideoRecodeView"

    //外部标记位
    var isRecoding = false

    //停止标记 防止多次调用stop
    private var mStoping = false

    private var recodePath: String? = null

    private var mStartTime: Long = 0

    private var mOnRecodeListener: OnRecodeListener? = null

    private val mVideoRecoderImpl: VideoRecoderImpl


    init {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mVideoRecoderImpl = VideoRecoderCamera2(this@VideoRecodeView, TextureViewPreview(context, this))
//        } else {
//            mVideoRecoderImpl = VideoRecoderCamera(this@VideoRecodeView, SurfaceViewPreview(context, this))
//        }
        //Camera2兼容问题很多  用camera吧
        mVideoRecoderImpl = VideoRecoderCamera(this@VideoRecodeView, SurfaceViewPreview(context, this))
    }

    override fun onDetachedFromWindow() {
        mVideoRecoderImpl.release()
        super.onDetachedFromWindow()
    }

    fun stop() {
        if (mStoping) return
        mStoping = true
        mVideoRecoderImpl.stop()
    }

    fun start() {
        if (!isRecoding) {
            recodePath = getOutputMediaFile().absolutePath
            recodePath?.let {
                isRecoding = true
                mVideoRecoderImpl.start(it)
            }
        }
    }

    fun setRecodeListener(listener: OnRecodeListener) {
        this.mOnRecodeListener = listener
    }

    override fun onError(code: Int, msg: String) {
        Log.e(TAG, msg + "")
        isRecoding = false
        mStoping = false
        mOnRecodeListener?.onRecodeError(code, msg)
    }

    override fun onStop(code: Int) {
        isRecoding = false
        mStoping = false
        recodePath?.let {
            mOnRecodeListener?.onRecodeFinish(if (code == 0) System.currentTimeMillis() - mStartTime else 0, it)
        }

    }

    override fun onStart() {
        mStartTime = System.currentTimeMillis()
        mOnRecodeListener?.onRecodeStart()
    }

    override fun onPrepare() {

    }

    interface OnRecodeListener {
        fun onRecodeStart()
        fun onRecodeError(code: Int, msg: String)
        fun onRecodeFinish(time: Long, path: String)
    }
}

/**
 * Create a File for saving an image or video
 */
fun getOutputMediaFile(): File {
    val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".mp4"
    return File(getSaveFilePath(SaveTYPE.VIDEO, fileName))
}


