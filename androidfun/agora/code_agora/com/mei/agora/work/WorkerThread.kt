package com.mei.agora.work

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.mei.init.meiApplication
import com.mei.orc.john.model.JohnUser
import com.mei.provider.AppBuildConfig
import com.net.model.chick.tab.tabbarConfig
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoEncoderConfiguration
import java.io.File


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-11-26
 */

private const val ACTION_WORKER_JOIN_CHANNEL = 0X2010
private const val ACTION_WORKER_LEAVE_CHANNEL = 0X2011
var AGORA_FILE_LOG = ""

class WorkerThread(val handler: IRtcEngineEventHandler) : Thread() {


    private var workerHandler: WorkerThreadHandler? = null
    private var rtcEngine: RtcEngine? = null

    override fun run() {
        super.run()
        Looper.prepare()
        workerHandler = WorkerThreadHandler(this)
        initAgoraEngine()
        Looper.loop()
    }

    private fun saveRootDir(): String {
        val cacheDir = if (meiApplication().checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            meiApplication().getExternalFilesDir(null)?.path ?: ""
        } else {
            meiApplication().filesDir?.path ?: ""
        }
        return when {
            cacheDir.isNotEmpty() -> cacheDir
            else -> Environment.getDataDirectory().path
        }
    }

    fun initAgoraEngine(): RtcEngine? {
        if (rtcEngine == null) {
            rtcEngine = try {
                val engine = RtcEngine.create(meiApplication().applicationContext, AppBuildConfig.AGORA_APP_ID, handler)
                engine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
                engine.enableVideo()
                engine.enableDualStreamMode(true)
                if (tabbarConfig.local_log_enable) {
                    AGORA_FILE_LOG = "${saveRootDir()}${File.separator}agora${File.separator}agora-rtc${System.currentTimeMillis()}.log"
                    try {
                        File(AGORA_FILE_LOG).parentFile?.mkdirs()
                        engine.setLogFile(AGORA_FILE_LOG)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                engine.adjustPlaybackSignalVolume(200)
                engine.enableAudioVolumeIndication(400, 3, true)
                engine.setVideoEncoderConfiguration(
                        VideoEncoderConfiguration(
                                VideoEncoderConfiguration.VD_640x360,
                                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                                VideoEncoderConfiguration.STANDARD_BITRATE,
                                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
                        )
                )
//                /** 低端机不使用美颜 **/
//                if (Themis.judgeDeviceLevel(meiApplication().applicationContext) != Themis.DEVICE_LEVEL_LOW) {
//                    engine.setBeautyEffectOptions(
//                            true, BeautyOptions(
//                            LIGHTENING_CONTRAST_NORMAL,
//                            0.8f,
//                            0.8f,
//                            0.8f
//                    )
//                    )
//                }
                engine
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return rtcEngine
    }

    fun enableLocalVideo(enableVideo: Boolean) {
        rtcEngine?.enableLocalVideo(enableVideo)
    }

    fun joinChannel(channel: String, token: String?) {
        if (currentThread() != this) {
            workerHandler?.sendMessage(Message().apply {
                what = ACTION_WORKER_JOIN_CHANNEL
                obj = Pair(channel, token)
            })
        }
        rtcEngine?.joinChannel(token, channel, "", JohnUser.getSharedUser().userID)
    }

    fun leaveChannel() {
        if (currentThread() != this) {
            workerHandler?.sendMessage(Message().apply {
                what = ACTION_WORKER_LEAVE_CHANNEL
            })
        }
        rtcEngine?.leaveChannel()
    }


}

private class WorkerThreadHandler(var worker: WorkerThread?) : Handler() {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val obj = msg.obj
        when (msg.what) {
            ACTION_WORKER_JOIN_CHANNEL -> {
                if (obj is Pair<*, *>) worker?.joinChannel(obj.first as? String
                        ?: "", obj.second as? String ?: "")
            }
            ACTION_WORKER_LEAVE_CHANNEL -> {
                worker?.leaveChannel()
            }
        }
    }
}