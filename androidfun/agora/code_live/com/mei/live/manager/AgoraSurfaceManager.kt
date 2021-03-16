package com.mei.live.manager

import android.view.SurfaceView
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.mei.agora.*
import com.mei.orc.ext.runAsync
import com.mei.orc.john.model.JohnUser
import io.agora.rtc.Constants

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-19
 */
open class AgoraSurfaceManager(
        val activity: FragmentActivity,
        var isStreaming: Boolean = false///** 直播状态 **/
) {

    init {
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    runAsync {
                        stopBroadcast()
                    }
                }
            }
        })
    }

    var surfaceViewLocal: SurfaceView? = null

    /**
     * 开始连线直播
     * @return 是否开启成功
     */
    open fun startBroadcast(videoContainer: ViewGroup, enableVideo: Boolean) {
        agoraConfig.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
        if (enableVideo) {
            surfaceViewLocal = agoraConfig.prepareRtcVideo(JohnUser.getSharedUser().userID)
            videoContainer.addView(surfaceViewLocal)
        }
        enableLocalVideo(enableVideo)
        isStreaming = true
    }

    /**
     * 结束直播
     * @param finish 是否关闭完成
     */
    open fun stopBroadcast() {
        agoraConfig.removeRtcVideo(0)
        activity.runOnUiThread { (surfaceViewLocal?.parent as? ViewGroup)?.removeView(surfaceViewLocal) }
        isStreaming = false
        System.gc()
    }

}