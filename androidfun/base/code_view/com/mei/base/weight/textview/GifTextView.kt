package com.mei.base.weight.textview

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


/**
 * Created by hang on 2018/7/24.
 * TextView后面加gif
 */

class GifTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle) : AppCompatTextView(context, attrs, defStyleAttr), Drawable.Callback {

    override fun invalidateDrawable(who: Drawable) {
        invalidate()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        handler?.postAtTime(what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        handler?.removeCallbacks(what)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        handleAnimationDrawable(true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        handleAnimationDrawable(false)
    }

    private fun handleAnimationDrawable(isPlay: Boolean) {
        val text = text
        if (text is Spanned) {
            val spans = text.getSpans(0, text.length,
                    ImageSpan::class.java)
            for (s in spans) {
                val d = s.drawable
                if (d is AnimationDrawable) {
                    if (isPlay) {
                        d.callback = this
                        d.start()
                    } else {
                        d.stop()
                        d.callback = null
                    }
                }
            }
        }
    }
}
