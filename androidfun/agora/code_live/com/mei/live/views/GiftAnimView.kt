package com.mei.live.views

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.mei.live.manager.GiftDoubleAnimManager
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.ext.sp
import com.mei.wood.R
import kotlinx.android.synthetic.main.view_gift_anim.view.*

/**
 * Created by hang on 2019-12-09.
 */
class GiftAnimView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attributeSet, defStyle) {

    private lateinit var doubleAnimManager: GiftDoubleAnimManager

    var count = 0

    var exitRunnable: Runnable? = null
    private val SHOW_GIFT_TIME: Long = 8000

    var showTime: Long = 0L

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)

        layoutInflaterKtToParentAttach(R.layout.view_gift_anim)
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.GiftAnimView)
        val textSize = ta.getDimension(R.styleable.GiftAnimView_double_text_size, sp(24).toFloat())
        count_white_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        count_blue_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        count_red_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        ta.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        doubleAnimManager = GiftDoubleAnimManager(count_white_view, count_blue_view, count_red_view)
    }

    private fun notifyDoubleAnimState(isShow: Boolean) {
        count_white_view.isVisible = isShow
        count_blue_view.isVisible = isShow
        count_red_view.isVisible = isShow
    }

    fun startAnim(num: Int) {
        notifyDoubleAnimState(true)
        count += num

        count_white_view.text = "X $count"
        count_blue_view.text = "X $count"
        count_red_view.text = "X $count"
        if (::doubleAnimManager.isInitialized) {
            doubleAnimManager.startAnim()
        }
        listenAnimLife()
    }

    /**
     * 监听动画展示周期
     */
    private fun listenAnimLife() {
        showTime = SystemClock.elapsedRealtime()
        removeCallbacks(exitRunnable)
        exitRunnable = Runnable {
            notifyDoubleAnimState(false)
            count = 0
        }
        postDelayed(exitRunnable, SHOW_GIFT_TIME)
    }

    fun cancelAnim() {
        doubleAnimManager.cancelAnim()
        notifyDoubleAnimState(false)
        count = 0
        removeCallbacks(exitRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearAnimation()
        removeCallbacks(exitRunnable)
    }
}