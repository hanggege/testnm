package com.mei.base.weight.textview

import android.content.Context
import android.content.res.ColorStateList
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mei.orc.util.date.dayEnd
import com.mei.orc.util.date.formatTimeVideo
import com.mei.wood.R

/**
 *
 * @author Created by Ling on 2019/5/14
 *
 * app:time_text_color="@color/color_333333" 倒计时字体的颜色
 * app:time_text="@string/ebook_time_down_text" 倒计时文案，推荐其中仅带有一个"%s"占位符，如果占位符过多会全部替换，如果没有则仅展示其文案
 *
 */
class TimeDownTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    private var mTextColor: ColorStateList? = null
    var mTimeText: String = ""
    var onTimeDownFinish: () -> Unit = {}

    private val textView by lazy {
        val temp = TextView(context)
        temp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f)
        temp.includeFontPadding = false
        temp.setLines(1)
        temp.gravity = Gravity.CENTER_VERTICAL
        temp.ellipsize = TextUtils.TruncateAt.END
        temp
    }

    private var timer: CountDownTimer? = null

    init {
        initFromAttributes(context, attrs, defStyleAttr)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        textView.setTextColor(mTextColor)
        addView(textView, layoutParams)
    }

    private fun initFromAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TimeDownTextView, defStyleAttr, 0)
        mTextColor = a.getColorStateList(R.styleable.TimeDownTextView_time_text_color)
        mTimeText = a.getText(R.styleable.TimeDownTextView_time_text)?.toString() ?: ""
        if (mTextColor == null) {
            mTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_999999))
        }
        a.recycle()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == View.VISIBLE) {
            countDownTime()
        } else {
            timer?.cancel()
        }
    }

    override fun removeAllViews() {
        super.removeAllViews()
        timer?.cancel()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        visibility = visibility
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        timer?.cancel()
    }

    private fun countDownTime() {
        val current = System.currentTimeMillis()
        val lastDate = dayEnd().time
        timer = object : CountDownTimer(lastDate - current, 1000) {
            override fun onFinish() {
                onTimeDownFinish()
            }

            override fun onTick(millisUntilFinished: Long) {
                setText(millisUntilFinished)
            }
        }.start()
    }

    private fun setText(diff: Long) {
        val hms = (diff / 1000).formatTimeVideo()
        textView.text = mTimeText.replace("%s", hms)
    }

}