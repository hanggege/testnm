package com.mei.chat.recoder.control.recoder

import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import com.mei.orc.util.file.OrcFileUtil
import com.mei.orc.util.string.stringConcate
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/12
 */
class MediaRecordControl(private val mOnRecodeControlListener: OnRecordControlListener?) : IRecordControl, MediaRecorder.OnInfoListener {

    private var mMediaRecorder: MediaRecorder? = null

    private var mMainHandler: Handler? = null

    private var voiceFile: File? = null
    private var isRecording: Boolean = false
    private var startTime: Long = 0
    private val vibrationRunnable = object : Runnable {
        override fun run() {
            if (!isRecording) return
            val vibration = mMediaRecorder?.let {
                it.maxAmplitude * 13 / 32767
            }
            vibration?.let { mOnRecodeControlListener?.onAudioVibration(it) }
            mMainHandler?.postDelayed(this, VIBRATION_TIME)
        }
    }


    private val voiceFilePath: String
        get() {
            val fileDir = stringConcate(OrcFileUtil.getCacheDir(), "voice")
            val filePath = stringConcate(fileDir, File.separator, System.currentTimeMillis().toString() + ".amr")
            OrcFileUtil.createDirs(fileDir)
            OrcFileUtil.createFile(filePath)
            return filePath
        }


    override fun start() {
        this.mMediaRecorder = MediaRecorder().apply {
            try {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setAudioChannels(1)
                setAudioSamplingRate(8000)
                setAudioEncodingBitRate(64)
                this@MediaRecordControl.voiceFile = File(voiceFilePath)
                setOutputFile(voiceFile?.absolutePath)
                setMaxDuration(MAX_RECODE_TIME_S * 1000)
                setOnInfoListener(this@MediaRecordControl)
                prepare()
                this@MediaRecordControl.isRecording = true
                start()

                this@MediaRecordControl.mOnRecodeControlListener?.onStart(voiceFile)
                this@MediaRecordControl.startTime = System.currentTimeMillis()

                if (this@MediaRecordControl.mMainHandler == null) {
                    this@MediaRecordControl.mMainHandler = Handler(Looper.getMainLooper())
                }
                this@MediaRecordControl.mMainHandler?.postDelayed(vibrationRunnable, VIBRATION_TIME)
            } catch (e: Exception) {
                e.printStackTrace()
                this@MediaRecordControl.isRecording = false
                this@MediaRecordControl.mOnRecodeControlListener?.onFail(-1, "录音启动失败")
            }
        }
    }


    override fun stop() {
        stop(0)
    }

    private fun stop(type: Int) {
        this.isRecording = false

        mMainHandler?.removeCallbacks(vibrationRunnable).let {
            mMainHandler = null
        }

        try {
            mMediaRecorder?.let {
                it.stop()
                it.release()
                mMediaRecorder = null

                val endTime = System.currentTimeMillis()
                val time = ((endTime - startTime) / 1000).toInt()
                mOnRecodeControlListener?.onFinish(type, voiceFile, time)
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
            mOnRecodeControlListener?.onFail(-2, "录音结束失败")
        }

    }


    override fun onInfo(mr: MediaRecorder, what: Int, extra: Int) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            if (mMainHandler == null) return
            mMainHandler?.post { stop(1) }
        }
    }

    companion object {

        //最大60s录制时间
        private const val MAX_RECODE_TIME_S = 180

        private const val VIBRATION_TIME = 100L
    }
}
