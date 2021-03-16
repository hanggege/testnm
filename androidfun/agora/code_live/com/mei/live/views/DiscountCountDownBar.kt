package com.mei.live.views

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.unit.TimeUnit
import com.mei.wood.R
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import kotlinx.android.synthetic.main.layout_indicator_progress.view.*
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * DiscountCountDownBar
 *
 * 快速咨询-直播间-倒计时view
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-10-13
 */
class DiscountCountDownBar(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    // ms制
    var duration = 100L
        @SuppressLint("SetTextI18n")
        set(value) {
            if (value != field) {
                field = value
                progressDurationText.text = "${value / TimeUnit.MINUTE}分钟"
                progressText.text = "剩余${value.toTimeFormat()}"
            }
        }

    var progress = 100L
        set(value) {
            if (field != value) {
                field = value
                refreshProgress(max(0f, min((value * 1f / duration), 1f)))
            }
        }

    private var svgaEntity: SVGAVideoEntity? = null

    private val animator by lazy {
        ValueAnimator.ofInt(duration.toInt(), 0).apply {
            interpolator = LinearInterpolator()
            duration = this@DiscountCountDownBar.duration
            addUpdateListener {
                refreshProgress(1 - it.animatedFraction)
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    progressIndicator.pauseAnimation()
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationRepeat(animation: Animator?) {}
            })
        }
    }

    init {
        layoutInflaterKtToParentAttach(R.layout.layout_indicator_progress)
        progressColor.updateLayoutParams { width = backgroundColor.measuredWidth }
        progressText.paint.isFakeBoldText = true
        SVGAParser(context).decodeFromAssets("discount_countdown.svga", object : SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                svgaEntity = videoItem
            }

            override fun onError() {
            }
        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        progressColor.updateLayoutParams<LayoutParams> {
            startToStart = backgroundColor.id
            topToTop = backgroundColor.id
            bottomToBottom = backgroundColor.id
            endToEnd = NO_ID
            width = backgroundColor.width
        }
    }

    fun start() {
        if (!animator.isRunning) {
            animator.start()
            startIndicatorAnimation()
        }
    }

    fun stop() {
        animator.cancel()
        progressIndicator.stopAnimation(true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    fun startIndicatorAnimation() {
        svgaEntity?.apply {
            val drawable = SVGADrawable(this)
            progressIndicator.setImageDrawable(drawable)
            progressIndicator.startAnimation()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshProgress(ratio: Float) {
        progressColor.updateLayoutParams {
            width = progressIndicator.width + ((backgroundColor.width - progressIndicator.width) * ratio).toInt()
        }

        progress = (duration * ratio).toLong()

        progressText.text = "剩余${(duration * ratio).toLong().toTimeFormat()}"

        val indicatorTextHalfWidth = progressText.width / 2f
        val indicatorHalfWidth = progressIndicator.width / 2f

        when {
            progressEndLine.left - indicatorHalfWidth + indicatorTextHalfWidth > backgroundColor.right -> {
                progressText.updateLayoutParams<LayoutParams> {
                    startToStart = NO_ID
                    endToEnd = backgroundColor.id
                    bottomToBottom = this@DiscountCountDownBar.id
                    topToBottom = progressIndicator.id
                }
            }
            progressEndLine.left - indicatorHalfWidth < backgroundColor.left + indicatorTextHalfWidth -> {
                progressText.updateLayoutParams<LayoutParams> {
                    startToStart = backgroundColor.id
                    endToEnd = NO_ID
                    bottomToBottom = this@DiscountCountDownBar.id
                    topToBottom = progressIndicator.id
                }
            }
            else -> {
                progressText.updateLayoutParams<LayoutParams> {
                    startToStart = progressIndicator.id
                    endToEnd = progressIndicator.id
                    bottomToBottom = this@DiscountCountDownBar.id
                    topToBottom = progressIndicator.id
                }
            }
        }
    }

    private fun Long?.toTimeFormat(): String {
        if (this == null) return ""
        var time = this
        val result = StringBuilder()
        if (this > TimeUnit.DAY) throw IllegalArgumentException("time$this")
        if (this < 0) time = 0
        val hour = (time / TimeUnit.HOUR).toInt()
        if (hour > 0) {
            result.append("${String.format("%02d", hour)}:")
            time -= hour * TimeUnit.HOUR
        }

        val min = (time / TimeUnit.MINUTE).toInt()
        result.append("${String.format("%02d", min)}:")
        time -= min * TimeUnit.MINUTE

        val seconds = ceil(time * 1f / TimeUnit.SECOND).toInt()
        result.append(String.format("%02d", seconds))
        return result.toString()
    }

}