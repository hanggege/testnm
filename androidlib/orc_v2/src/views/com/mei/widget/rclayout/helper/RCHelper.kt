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
 * Last modified 2018-04-13 23:18:02
 *
 * GitHub: https://github.com/GcsSloop
 * WeiBo: http://weibo.com/GcsSloop
 * WebSite: http://www.gcssloop.com
 */

package com.mei.widget.rclayout.holder


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import com.mei.orc.R
import java.util.*

/**
 * 作用：圆角辅助工具
 * 作者：GcsSloop
 */
class RCHelper {

    val mClipPath: Path by lazy { Path() }                // 剪裁区域路径
    val mPaint: Paint by lazy { Paint() }                  // 画笔

    val mAreaRegion: Region by lazy { Region() }           // 内容区域
    val mLayer: RectF by lazy { RectF() }                // 画布图层大小

    var mClipBackground: Boolean = false        // 是否剪裁背景
    var mRoundAsCircle = false // 圆形
    var radii = FloatArray(8)   // top-left, top-right, bottom-right, bottom-left

    var mDefaultStrokeColor: Int = 0        // 默认描边颜色
    var mStrokeColor: Int = 0               // 描边颜色
    var mStrokeColorStateList: ColorStateList? = null// 描边颜色的状态
    var mStrokeWidth: Int = 0               // 描边半径

    //--- Selector 支持 ----------------------------------------------------------------------------

    var mChecked: Boolean = false              // 是否是 check 状态
    var mOnCheckedChangeListener: OnCheckedChangeListener? = null

    fun initAttrs(context: Context, attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RCAttrs)
        mRoundAsCircle = ta.getBoolean(R.styleable.RCAttrs_round_as_circle, false)
        mStrokeColorStateList = ta.getColorStateList(R.styleable.RCAttrs_rc_stroke_color)

        mStrokeColor = mStrokeColorStateList?.defaultColor ?: Color.WHITE
        mDefaultStrokeColor = mStrokeColorStateList?.defaultColor ?: Color.WHITE

        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.RCAttrs_rc_stroke_width, 0)
        mClipBackground = ta.getBoolean(R.styleable.RCAttrs_clip_background, false)
        val roundCorner = ta.getDimensionPixelSize(R.styleable.RCAttrs_round_corner, 0)
        val roundCornerTopLeft = ta.getDimensionPixelSize(
                R.styleable.RCAttrs_round_corner_top_left, roundCorner)
        val roundCornerTopRight = ta.getDimensionPixelSize(
                R.styleable.RCAttrs_round_corner_top_right, roundCorner)
        val roundCornerBottomLeft = ta.getDimensionPixelSize(
                R.styleable.RCAttrs_round_corner_bottom_left, roundCorner)
        val roundCornerBottomRight = ta.getDimensionPixelSize(
                R.styleable.RCAttrs_round_corner_bottom_right, roundCorner)
        ta.recycle()

        radii[0] = roundCornerTopLeft.toFloat()
        radii[1] = roundCornerTopLeft.toFloat()

        radii[2] = roundCornerTopRight.toFloat()
        radii[3] = roundCornerTopRight.toFloat()

        radii[4] = roundCornerBottomRight.toFloat()
        radii[5] = roundCornerBottomRight.toFloat()

        radii[6] = roundCornerBottomLeft.toFloat()
        radii[7] = roundCornerBottomLeft.toFloat()


        mPaint.color = Color.WHITE
        mPaint.isAntiAlias = true
    }

    fun onSizeChanged(view: View, w: Int, h: Int) {
        mLayer.set(0f, 0f, w.toFloat(), h.toFloat())
        refreshRegion(view)
    }

    fun refreshRegion(view: View) {
        val w = mLayer.width().toInt()
        val h = mLayer.height().toInt()
        val areas = RectF()
        areas.left = view.paddingLeft.toFloat()
        areas.top = view.paddingTop.toFloat()
        areas.right = (w - view.paddingRight).toFloat()
        areas.bottom = (h - view.paddingBottom).toFloat()
        mClipPath.reset()
        if (mRoundAsCircle) {
            val d = if (areas.width() >= areas.height()) areas.height() else areas.width()
            val r = d / 2
            val center = PointF((w / 2).toFloat(), (h / 2).toFloat())
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                mClipPath.addCircle(center.x, center.y, r, Path.Direction.CW)

                mClipPath.moveTo(0f, 0f)  // 通过空操作让Path区域占满画布
                mClipPath.moveTo(w.toFloat(), h.toFloat())
            } else {
                val y = h / 2 - r
                mClipPath.moveTo(areas.left, y)
                mClipPath.addCircle(center.x, y + r, r, Path.Direction.CW)
            }
        } else {
            mClipPath.addRoundRect(areas, radii, Path.Direction.CW)
        }
        val clip = Region(areas.left.toInt(), areas.top.toInt(),
                areas.right.toInt(), areas.bottom.toInt())
        mAreaRegion.setPath(mClipPath, clip)
    }

    fun onClipDraw(canvas: Canvas) {
        if (mStrokeWidth > 0) {
            // 支持半透明描边，将与描边区域重叠的内容裁剪掉
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            mPaint.color = Color.WHITE
            mPaint.strokeWidth = (mStrokeWidth * 2).toFloat()
            mPaint.style = Paint.Style.STROKE
            canvas.drawPath(mClipPath, mPaint)
            // 绘制描边
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            mPaint.color = mStrokeColor
            mPaint.style = Paint.Style.STROKE
            canvas.drawPath(mClipPath, mPaint)
        }
        mPaint.color = Color.WHITE
        mPaint.style = Paint.Style.FILL

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            canvas.drawPath(mClipPath, mPaint)
        } else {
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

            val path = Path()
            path.addRect(0f, 0f, mLayer.width().toInt().toFloat(), mLayer.height().toInt().toFloat(), Path.Direction.CW)
            path.op(mClipPath, Path.Op.DIFFERENCE)
            canvas.drawPath(path, mPaint)
        }
    }

    fun drawableStateChanged(view: View) {
        if (view is RCAttrs) {
            val stateListArray = ArrayList<Int>()
            if (view is Checkable) {
                stateListArray.add(android.R.attr.state_checkable)
                if ((view as Checkable).isChecked)
                    stateListArray.add(android.R.attr.state_checked)
            }
            if (view.isEnabled) stateListArray.add(android.R.attr.state_enabled)
            if (view.isFocused) stateListArray.add(android.R.attr.state_focused)
            if (view.isPressed) stateListArray.add(android.R.attr.state_pressed)
            if (view.isHovered) stateListArray.add(android.R.attr.state_hovered)
            if (view.isSelected) stateListArray.add(android.R.attr.state_selected)
            if (view.isActivated) stateListArray.add(android.R.attr.state_activated)
            if (view.hasWindowFocus()) stateListArray.add(android.R.attr.state_window_focused)

            if (mStrokeColorStateList != null && mStrokeColorStateList!!.isStateful) {
                val stateList = IntArray(stateListArray.size)
                for (i in stateListArray.indices) {
                    stateList[i] = stateListArray[i]
                }
                val stateColor = mStrokeColorStateList!!.getColorForState(stateList, mDefaultStrokeColor)
                (view as RCAttrs).strokeColor = stateColor
            }
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(view: View, isChecked: Boolean)
    }
}