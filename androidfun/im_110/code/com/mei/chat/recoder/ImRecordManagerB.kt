package com.mei.chat.recoder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.mei.chat.recoder.callback.OnRecordListener
import com.mei.chat.recoder.control.recoder.IRecordControl
import com.mei.chat.recoder.control.recoder.MediaRecordControl
import com.mei.chat.recoder.control.recoder.OnRecordControlListener
import com.mei.chat.recoder.control.uistate.IUIStateControl
import com.mei.chat.recoder.control.uistate.PopWindowStateControlB
import com.mei.orc.ext.dip
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.permission.PermissionCheck
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/12
 */

@Suppress("unused")
fun View.recordAudioB(hintTv: TextView, start: () -> Unit = {},
                      finish: (File?, Int) -> Unit = { _, _ -> }) {
    RecordManagerB(this, hintTv, object : OnRecordListener {
        override fun onFinish(file: File?, time: Int) {
            finish(file, time)
        }

        override fun onStart(file: File?) {
            start()
        }
    })
}


@SuppressLint("ClickableViewAccessibility")
class RecordManagerB constructor(private val mView: View, private val hintTv: TextView, private val mOnRecordListener: OnRecordListener) : View.OnTouchListener {

    private val mRecordControl: IRecordControl by lazy { MediaRecordControl(MyRecordControlListener()) }
    private val mStateControl: IUIStateControl by lazy { PopWindowStateControlB(hintTv) }
    private val mContext: Context = mView.context
    private var downRawY: Float = 0.0f

    //按住取消滑动最小距离
    private val downCancelMoveMinDistance: Int = -mContext.dip(70)

    private var nowState: IUIStateControl.STATE = IUIStateControl.STATE.DEF

    init {
        mView.setOnTouchListener(this)
    }

    //为了解决在按住说话的时候点击其他地方没有反应，所以activity上层拦截手打分发过来的event事件，
    //因为是直接调用的view的dispatchTouchEvent函数,没有经过viewGroup计算相关坐标， 需要注意坐标值相关的
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (PermissionCheck.hasPermission(mContext, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mView.isSelected = true

                    downRawY = event.rawY

                    mRecordControl.start()
                    stateChange(IUIStateControl.STATE.DOWN)
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (nowState == IUIStateControl.STATE.DOWN_TIME_OUT) {
                        return true
                    }
                    if (event.rawY - downRawY < downCancelMoveMinDistance) {
                        stateChange(IUIStateControl.STATE.DOWN_CANCEL)
                    } else {
                        stateChange(IUIStateControl.STATE.DOWN)
                    }
                    return true
                }
                //手指一直按在同一个位置的话，那么抬起手指不会触发up，将会触发cancel
                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> {
                    return up(event)
                }

            }
        } else if (event.action == MotionEvent.ACTION_DOWN) {
            (mContext as? FragmentActivity)?.let { activity ->
                activity.requestMulPermissionZip(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    if (!it) {
                        UIToast.toast(activity, "需要录音权限才能发语音哦")
                    }
                }

            }

        }
        return true

    }

    private fun up(event: MotionEvent): Boolean {
        mView.isSelected = false
        if (nowState == IUIStateControl.STATE.DOWN_TIME_OUT) {
            stateChange(IUIStateControl.STATE.UP)
            return true
        }
        val discard = event.rawY - downRawY < downCancelMoveMinDistance
        if (discard) {
            stateChange(IUIStateControl.STATE.UP_CANCEL)
        } else {
            stateChange(IUIStateControl.STATE.UP)
        }
        mRecordControl.stop()
        return true
    }

    private fun stateChange(state: IUIStateControl.STATE) {
        if (nowState === state) return
        mStateControl.stateChange(nowState, state)
        nowState = state
    }


    internal inner class MyRecordControlListener : OnRecordControlListener {

        override fun onStart(file: File?) {
            mOnRecordListener.onStart(file)
        }

        override fun onFinish(type: Int, file: File?, time: Int) {
            when {
                type == 1 -> {
                    stateChange(IUIStateControl.STATE.DOWN_TIME_OUT)
                    mOnRecordListener.onFinish(file, time)
                }
                time < 1 -> {
                    //如果不是抬起的话，那么说明是取消发送了
                    if (nowState != IUIStateControl.STATE.UP) {
                        mOnRecordListener.onFinish(null, 0)
                        return
                    }
                    stateChange(IUIStateControl.STATE.DOWN_TIME_SHORT)
                    mOnRecordListener.onFinish(null, 0)
                }
                nowState != IUIStateControl.STATE.UP -> mOnRecordListener.onFinish(null, 0)
                else -> mOnRecordListener.onFinish(file, time)
            }
        }

        override fun onFail(code: Int, errorMsg: String) {
            mOnRecordListener.onFinish(null, 0)
            UIToast.toast(mContext, "录音失败，请重试！")
        }

        override fun onAudioVibration(vibration: Int) {
            if (nowState == IUIStateControl.STATE.DOWN)
                mStateControl.onAudioVibration(vibration)
        }
    }
}
