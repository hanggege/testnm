package com.mei.chat.recoder.control.uistate


import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/13
 */
class PopWindowStateControlB(private val mTextView: TextView) : IUIStateControl {

    private val tvHint: TextView
    private val ivHint: ImageView
    private val mContext = mTextView.context
    private val micImages: Array<Int> by lazy {
        arrayOf(R.drawable.im_press_say_1,
                R.drawable.im_press_say_2,
                R.drawable.im_press_say_3,
                R.drawable.im_press_say_4,
                R.drawable.im_press_say_5,
                R.drawable.im_press_say_6)
    }

    private val mPopWindow: PopupWindow by lazy {
        val popView = LayoutInflater.from(mContext).inflate(R.layout.im_press_say_layout, null)
        PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isOutsideTouchable = false
            isFocusable = false
        }
    }


    init {
        tvHint = mPopWindow.contentView.findViewById(R.id.recording_hint)
        ivHint = mPopWindow.contentView.findViewById(R.id.mic_image)
    }

    override fun stateChange(oldState: IUIStateControl.STATE, nowState: IUIStateControl.STATE) {
        when (nowState) {
            IUIStateControl.STATE.DOWN -> {
                showPop()

                tvHint.text = "手指上滑，取消发送"
                ivHint.setImageResource(R.drawable.im_press_say_1)

                mTextView.text = "松开结束"
            }
            IUIStateControl.STATE.DOWN_CANCEL -> {
                tvHint.text = "松开手指，取消发送"
                ivHint.setImageResource(R.drawable.im_press_cancel)
                mTextView.text = "松开取消"
            }
            IUIStateControl.STATE.DOWN_TIME_OUT -> {
                ivHint.setImageResource(R.drawable.im_press_say_short_or_long)
                tvHint.text = "说话时间过长"
            }
            IUIStateControl.STATE.DOWN_TIME_SHORT -> {
                showPop()
                tvHint.text = "说话时间过短"
                ivHint.setImageResource(R.drawable.im_press_say_short_or_long)
                tvHint.postDelayed({
                    mPopWindow.dismiss()
                }, 750)
            }

            IUIStateControl.STATE.DEF,
            IUIStateControl.STATE.UP_CANCEL,
            IUIStateControl.STATE.UP -> {
                mTextView.text = "按住说话"
                mPopWindow.dismiss()
            }
        }
    }

    private fun showPop() {
        if (mContext is Activity) {
            val root = mContext.window.decorView.findViewById<View>(android.R.id.content)
            mPopWindow.showAtLocation(root, Gravity.CENTER, 0, 0)
        }
    }

    override fun onAudioVibration(vibration: Int) {
        var v = vibration
        if (mPopWindow.isShowing) {

            if (v < 0) {
                v = 0
            }
            if (v > micImages.size - 1) {
                v = micImages.size - 1
            }

            // 切换msg切换图片
            ivHint.setImageResource(micImages[v])
        }
    }
}
