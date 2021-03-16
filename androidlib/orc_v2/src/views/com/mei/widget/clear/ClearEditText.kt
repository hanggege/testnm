package com.mei.widget.clear

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.appcompat.widget.AppCompatEditText

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/25
 */
class ClearEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle) :
        AppCompatEditText(context, attrs, defStyleAttr), View.OnFocusChangeListener {


    private val radius: Float by lazy { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics) }
    private var hasFocus: Boolean = false
    private val mTouchSlop: Int by lazy { ViewConfiguration.get(context).scaledTouchSlop }
    private val clearDrawable: ClearDrawable by lazy {
        ClearDrawable(this).apply {
            setBounds(0, 0, (radius * 2).toInt(), (radius * 2).toInt())
        }
    }


    init {
        onFocusChangeListener = this
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                refreshClearIcon()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    override fun onFocusChange(v: View?, has: Boolean) {
        hasFocus = has
        refreshClearIcon()
    }

    private fun refreshClearIcon() {
        val right = if (hasFocus && length() > 0) clearDrawable else null
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], right, compoundDrawables[3])
    }


    private var initXY = Pair(0f, 0f)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initXY = Pair(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                if (checkXYPosition(event.x, event.y, mTouchSlop)) {
                    val centerX = measuredWidth - totalPaddingRight + totalPaddingLeft - paddingLeft - radius
                    val centerY = (measuredHeight - paddingTop - paddingBottom) / 2f + paddingTop
                    if (checkXYPosition(centerX, centerY, (radius * 3).toInt())) {
                        setText("")
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun checkXYPosition(x: Float, y: Float, offset: Int): Boolean {
        return Math.abs(initXY.first - x) < offset && Math.abs(initXY.second - y) < offset
    }
}

class ClearDrawable(view: ClearEditText) : Drawable() {
    private val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.2f, view.context.resources.displayMetrics)
        }
    }

    override fun draw(canvas: Canvas) {
        paint.color = Color.parseColor("#ADB9B3")
        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()
        val radius = Math.min(bounds.width(), bounds.height()) / 2f
        canvas.drawCircle(centerX, centerY, radius, paint)

        paint.color = Color.WHITE
        val offset = radius * 1 / 3
        canvas.drawLine(centerX - offset, centerY - offset, centerX + offset, centerY + offset, paint)
        canvas.drawLine(centerX + offset, centerY - offset, centerX - offset, centerY + offset, paint)
    }


    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

}