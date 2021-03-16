/*
 * Copyright 2018 GcsSloop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified 2018-04-13 23:18:56
 *
 * GitHub: https://github.com/GcsSloop
 * WeiBo: http://weibo.com/GcsSloop
 * WebSite: http://www.gcssloop.com
 */

package com.mei.widget.rclayout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Checkable
import android.widget.RelativeLayout

import com.mei.widget.rclayout.holder.RCAttrs
import com.mei.widget.rclayout.holder.RCHelper


/**
 * 作用：圆角相对布局
 * 作者：GcsSloop
 */
class RCRelativeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr), Checkable, RCAttrs {
    private val mRCHelper: RCHelper by lazy { RCHelper() }

    //--- 公开接口 ----------------------------------------------------------------------------------

    override var isClipBackground: Boolean
        get() = mRCHelper.mClipBackground
        set(clipBackground) {
            mRCHelper.mClipBackground = clipBackground
            invalidate()
        }

    override var isRoundAsCircle: Boolean
        get() = mRCHelper.mRoundAsCircle
        set(roundAsCircle) {
            mRCHelper.mRoundAsCircle = roundAsCircle
            invalidate()
        }

    override val topLeftRadius: Float
        get() = mRCHelper.radii[0]

    override val topRightRadius: Float
        get() = mRCHelper.radii[2]

    override val bottomLeftRadius: Float
        get() = mRCHelper.radii[4]

    override val bottomRightRadius: Float
        get() = mRCHelper.radii[6]

    override var strokeWidth: Int
        get() = mRCHelper.mStrokeWidth
        set(strokeWidth) {
            mRCHelper.mStrokeWidth = strokeWidth
            invalidate()
        }

    override var strokeColor: Int
        get() = mRCHelper.mStrokeColor
        set(strokeColor) {
            mRCHelper.mStrokeColor = strokeColor
            invalidate()
        }

    init {
        attrs?.let {
            mRCHelper.initAttrs(context, it)
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRCHelper.onSizeChanged(this, w, h)
    }

    @Suppress("DEPRECATION")
    override fun dispatchDraw(canvas: Canvas) {
        canvas.saveLayer(mRCHelper.mLayer, null, Canvas.ALL_SAVE_FLAG)
        super.dispatchDraw(canvas)
        mRCHelper.onClipDraw(canvas)
        canvas.restore()
    }

    override fun draw(canvas: Canvas) {
        if (mRCHelper.mClipBackground) {
            canvas.save()
            canvas.clipPath(mRCHelper.mClipPath)
            super.draw(canvas)
            canvas.restore()
        } else {
            super.draw(canvas)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        if (action == MotionEvent.ACTION_DOWN && !mRCHelper.mAreaRegion.contains(ev.x.toInt(), ev.y.toInt())) {
            return false
        }
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
            refreshDrawableState()
        } else if (action == MotionEvent.ACTION_CANCEL) {
            isPressed = false
            refreshDrawableState()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun setRadius(radius: Int) {
        for (i in 0 until mRCHelper.radii.size) {
            mRCHelper.radii[i] = radius.toFloat()
        }
        invalidate()
    }

    override fun setTopLeftRadius(topLeftRadius: Int) {
        mRCHelper.radii[0] = topLeftRadius.toFloat()
        mRCHelper.radii[1] = topLeftRadius.toFloat()
        invalidate()
    }

    override fun setTopRightRadius(topRightRadius: Int) {
        mRCHelper.radii[2] = topRightRadius.toFloat()
        mRCHelper.radii[3] = topRightRadius.toFloat()
        invalidate()
    }

    override fun setBottomLeftRadius(bottomLeftRadius: Int) {
        mRCHelper.radii[6] = bottomLeftRadius.toFloat()
        mRCHelper.radii[7] = bottomLeftRadius.toFloat()
        invalidate()
    }

    override fun setBottomRightRadius(bottomRightRadius: Int) {
        mRCHelper.radii[4] = bottomRightRadius.toFloat()
        mRCHelper.radii[5] = bottomRightRadius.toFloat()
        invalidate()
    }

    override fun invalidate() {
        mRCHelper.refreshRegion(this)
        super.invalidate()
    }


    //--- Selector 支持 ----------------------------------------------------------------------------

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        mRCHelper.drawableStateChanged(this)
    }

    override fun setChecked(checked: Boolean) {
        if (mRCHelper.mChecked != checked) {
            mRCHelper.mChecked = checked
            refreshDrawableState()
            if (mRCHelper.mOnCheckedChangeListener != null) {
                mRCHelper.mOnCheckedChangeListener!!.onCheckedChanged(this, mRCHelper.mChecked)
            }
        }
    }

    override fun isChecked(): Boolean {
        return mRCHelper.mChecked
    }

    override fun toggle() {
        isChecked = !mRCHelper.mChecked
    }

    fun setOnCheckedChangeListener(listener: RCHelper.OnCheckedChangeListener) {
        mRCHelper.mOnCheckedChangeListener = listener
    }
}
