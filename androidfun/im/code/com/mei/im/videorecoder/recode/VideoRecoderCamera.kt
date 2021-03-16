@file:Suppress("DEPRECATION")

package com.mei.im.videorecoder.recode

import android.annotation.SuppressLint
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.util.Log
import com.mei.im.videorecoder.preview.PreviewImpl
import java.io.IOException


/**
 *  Created by zzw on 2019-07-04
 *  Des:21之前  Camera实现的视频录制
 */

@Suppress("DEPRECATION")
class VideoRecoderCamera(callback: Callback, previewImpl: PreviewImpl) : VideoRecoderImpl(callback, previewImpl) {

    val TAG = "VideoRecoderCamera"

    private var mMediaRecorder: MediaRecorder? = null
    private var mCamera: Camera? = null
    private var mPath: String? = null

    init {
        mCamera = getCameraInstance()
        if (mCamera == null) {
            mCallback.onError(-1, "camera is null")
        }
        previewImpl.onSurfaceChanged = {
            setUpPreview()
            adjustCameraParameters()
        }
        //添加到父布局
        mPreviewImpl.getView()
    }

    override fun prepare(): Boolean {
        if (mCamera == null) return false
        try {
            mCamera?.unlock()
            mMediaRecorder = MediaRecorder().apply {
                setCamera(mCamera)
                setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                setVideoSource(MediaRecorder.VideoSource.CAMERA)
                setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P))
                setOutputFile(mPath)
                setPreviewDisplay(mPreviewImpl.getSurface())
                setOrientationHint(90)
                prepare()
                mCallback.onPrepare()
            }
        } catch (e: Exception) {
            releaseMediaRecorder()
            mCallback.onError(-1, e.toString())
            return false
        }

        return true

    }

    override fun start(path: String) {
        this.mPath = path
        if (!prepare()) return

        try {
            mMediaRecorder?.start()
            isRecoding = true
            mCallback.onStart()

        } catch (e: Exception) {
            releaseMediaRecorder()
            mCallback.onError(-1, e.toString())
        }
    }

    override fun stop() {
        try {
            mMediaRecorder?.stop()
            isRecoding = false
            releaseMediaRecorder()
            mCallback.onStop()

        } catch (e: Exception) {
            mCallback.onError(-1, e.toString())
        }
    }

    override fun release() {
        try {
            releaseMediaRecorder()
            releaseCamera()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun releaseMediaRecorder() {
        mMediaRecorder?.apply {
            if (isRecoding) {
                stop()
                isRecoding = false
            }
            reset()
            release()
        }
        mMediaRecorder = null
        mCamera?.lock()
    }

    private fun releaseCamera() {
        mCamera?.apply {
            release()
        }
        mCamera = null
    }


    /**
     * A safe way to get an instance of the Camera object.
     */
    private fun getCameraInstance(): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open()
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
        }
        return c // returns null if camera is unavailable
    }


    // Suppresses Camera#setPreviewTexture
    @SuppressLint("NewApi")
    private fun setUpPreview() {
        try {
            mCamera?.setPreviewDisplay(mPreviewImpl.getSurfaceHolder())
        } catch (e: IOException) {
            Log.d(TAG, "Error setUpPreview: " + e.message)
        }
    }

    private var mShowingPreview = false
    private fun adjustCameraParameters() {
        mCamera?.apply {
            try {
                if (mShowingPreview) {
                    stopPreview()
                }
                setDisplayOrientation(90)

                val parameters = parameters
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                setParameters(parameters)
                startPreview()
                mShowingPreview = true
            } catch (e: Exception) {
                Log.d(TAG, "Error adjustCameraParameters: " + e.message)
            }
        }
    }

}