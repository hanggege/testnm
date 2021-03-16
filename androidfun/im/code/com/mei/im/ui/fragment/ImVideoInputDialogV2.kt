package com.mei.im.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mei.chat.ext.REQUEST_CODE_VIDEO
import com.mei.im.ui.IMVideoActivityLauncher
import com.mei.im.videorecoder.VideoRecodeView
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import kotlinx.android.synthetic.main.dialog_video_input_v2.*
import java.util.*

/**
 *  Created by zzw on 2019-07-04
 *  Des:
 */

class ImVideoInputDialogV2 : DialogFragment(), View.OnTouchListener, VideoRecodeView.OnRecodeListener {

    private val REFRESH_UI_DUR = 50
    private val MAX_TIME = 15000 / REFRESH_UI_DUR
    private val MIN_TIME = 3000

    private var mTimer: Timer? = null
    private var mTimeCount: Int = 0

    private val mainHandler = Handler(Looper.getMainLooper())
    private val updateProgress = Runnable {
        progress_right.progress = mTimeCount
        progress_left.progress = mTimeCount
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setStyle(STYLE_NORMAL, R.style.maskDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_video_input_v2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_right.max = MAX_TIME
        progress_left.max = MAX_TIME
        progress_left.rotation = 180.0f

        camera_preview.setRecodeListener(this)
        btn_record.setOnTouchListener(this)
    }

    private var mFlogTime = 0L

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (System.currentTimeMillis() - mFlogTime < 500) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                UIToast.toast(activity, "请勿快速点击")
                mFlogTime = System.currentTimeMillis()
            }
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (camera_preview.isRecoding) return true
                mFlogTime = System.currentTimeMillis()
                camera_preview.start()
            }
            MotionEvent.ACTION_UP -> {
                mFlogTime = System.currentTimeMillis()
                camera_preview.stop()
            }
        }
        return true
    }


    override fun onRecodeStart() {
        mTimer = Timer()
        mTimer?.schedule(object : TimerTask() {
            override fun run() {
                mTimeCount++
                mainHandler.post(updateProgress)
                if (mTimeCount == MAX_TIME) {
                    mainHandler.post { camera_preview?.stop() }
                }
            }
        }, 0, REFRESH_UI_DUR.toLong())
    }

    override fun onRecodeError(code: Int, msg: String) {
        release()
        UIToast.toast(activity, "录制出现错误")
        dismissAllowingStateLoss()
    }

    override fun onRecodeFinish(time: Long, path: String) {
        release()
        if (time >= MIN_TIME) {
            IMVideoActivityLauncher.startForResult(activity, true, path, REQUEST_CODE_VIDEO)
            camera_preview?.postDelayed({ dismissAllowingStateLoss() }, 200)
        } else {
            UIToast.toast(activity, getString(R.string.chat_video_too_short))
            mTimeCount = 0
            mainHandler.post(updateProgress)
        }
    }

    private fun release() {
        mTimer?.apply {
            cancel()
        }
        mTimer = null
        mTimeCount = 0
        mainHandler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }

}


