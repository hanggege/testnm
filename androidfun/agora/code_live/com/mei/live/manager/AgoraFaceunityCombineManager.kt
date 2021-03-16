package com.mei.live.manager

import android.opengl.GLSurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.faceunity.FURenderer
import com.mei.agora.agoraConfig
import com.mei.agora.enableLocalVideo
import com.mei.agora.setClientRole
import com.mei.faceunity.entity.initFuRenderer
import com.mei.faceunity.getServiceKey
import com.mei.faceunity.loadNewFaceUnityInfo
import com.mei.init.meiApplication
import com.mei.orc.ext.*
import com.mei.wood.R
import com.mei.wood.util.logDebug
import io.agora.kit.media.VideoManager
import io.agora.kit.media.capture.VideoCaptureFrame
import io.agora.kit.media.connector.SinkConnector
import io.agora.kit.media.constant.Constant
import io.agora.rtc.Constants

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-11-26
 */
fun FragmentActivity.getAgoraSurfaceManager(faceKey: String = "", faceState: (Boolean) -> Unit = {}): AgoraSurfaceManager {
//    return AgoraSurfaceManager(this)
//    return if (Themis.judgeDeviceLevel(this) == Themis.DEVICE_LEVEL_LOW) AgoraSurfaceManager(this)
//    else AgoraFaceunityCombineManager(this, faceState)
    return (Themis.judgeDeviceLevel(meiApplication().applicationContext) > Themis.DEVICE_LEVEL_LOW)
            .selectBy(AgoraFaceunityCombineManager(faceKey, this, faceState), AgoraSurfaceManager(this))
}

class AgoraFaceunityCombineManager(
        faceKey: String,
        activity: FragmentActivity,
        val faceState: (Boolean) -> Unit = {}
) : AgoraSurfaceManager(activity) {

    init {
        /** 直播间是否动态去更新美颜效果 **/
        if (faceKey.isNotEmpty() && faceKey != getServiceKey()) {
            loadNewFaceUnityInfo(faceKey) {
                fuRenderer.initFuRenderer()
            }
        }
    }

    private var mFUInit = false
    private val fuRenderer: FURenderer by lazy {
        FURenderer.Builder(activity)
                .maxFaces(3)
                .createEGLContext(false)
                .setNeedFaceBeauty(true)
                .setOnTrackingStatusChangedListener {
                    logDebug("fuRenderer", "是否有人脸 : $it")
                    /** 上报是否有人在直播 **/
                    faceState(it == 1)
                }
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .build().initFuRenderer()

    }
    private val effectHandler: SinkConnector<VideoCaptureFrame> by lazy {
        SinkConnector<VideoCaptureFrame> {
            if (glSurfaceViewLocal?.tag == true) {
                val fuTextureId = fuRenderer.onDrawFrame(it.mImage, it.mTextureId, it.mFormat?.width
                        ?: 0, it.mFormat?.height ?: 0)
                fuTextureId
            } else 0
        }
    }

    private val videoManager: VideoManager by lazy { VideoManager.createInstance(meiApplication().applicationContext) }

    var glSurfaceViewLocal: GLSurfaceView? = null
    var videoParent: ViewGroup? = null
    private val loadingView: View by lazy {
        activity.layoutInflaterKt(R.layout.live_upstream_loading_layout)
    }

    /**
     * 开始连线直播
     * @return 是否开启成功
     */
    override fun startBroadcast(videoContainer: ViewGroup, enableVideo: Boolean) {
        try {
            agoraConfig.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
            if (enableVideo) {
                glSurfaceViewLocal = GLSurfaceView(activity)
                bindSurfaceViewEvent()

                videoManager.allocate(screenWidth, screenHeight, 30, Constant.CAMERA_FACING_FRONT)
                videoManager.setRenderView(glSurfaceViewLocal)
                videoManager.connectEffectHandler(effectHandler)
                videoManager.attachToRTCEngine(agoraConfig.rtcEngine())
                videoManager.startCapture()

                videoParent = FrameLayout(activity).apply {
                    addView(glSurfaceViewLocal)
                    (loadingView.parent as? ViewGroup)?.removeView(loadingView)
                    addView(loadingView)
                }
                videoContainer.addView(videoParent)
                videoManager.startCapture()
            }
            enableLocalVideo(enableVideo)
            isStreaming = true
        } catch (e: Exception) {
            e.printStackTrace()
            runAsync {
                stopBroadcast()
            }
        }
    }

    /**
     * 结束直播
     * finish 是否关闭完成
     */
    override fun stopBroadcast() {
        try {
            isStreaming = false
            activity.runOnUiThread {
                (videoParent?.parent as? ViewGroup)?.removeView(glSurfaceViewLocal)
            }

            videoManager.stopCapture()
            videoManager.deallocate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fuRenderer.onSurfaceDestroyed()
            mFUInit = false
            System.gc()
        }
    }

    private fun bindSurfaceViewEvent() {
        glSurfaceViewLocal?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                v?.tag = false
            }

            override fun onViewAttachedToWindow(v: View?) {
                glSurfaceViewLocal?.queueEvent {
                    if (!mFUInit) {
                        v?.tag = true
                        mFUInit = true
                        fuRenderer.onSurfaceCreated()
                    }
                }
            }

        })
    }


}
