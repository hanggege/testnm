package com.mei.widget.bottomline

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import android.widget.Space
import androidx.annotation.ColorInt
import com.mei.orc.R
import java.util.*

/**
 * Created by joker on 2018/3/20.
 * See [R.styleable.BottomLineLayout]
 *
 * @attr ref [R.styleable.BottomLineLayout_bottomLineColor] 下划线的颜色
 * @attr ref [R.styleable.BottomLineLayout_bottomLineSelectedColor] 下划线选中的颜色
 * @attr ref [R.styleable.BottomLineLayout_bottomLineIndex]    选中第几项，与代码布局的结构一致
 * @attr ref [R.styleable.BottomLineLayout_bottomLineHeight]  下划线的高度（线的粗细）默认为1dp
 * @attr ref [R.styleable.BottomLineLayout_bottomLineWidth]   下划线的宽度，如果没有的话则是用当前控件的宽度
 * @attr ref [R.styleable.BottomLineLayout_bottomLineOffset]   下划线的偏移量，与app:bottomLineWidth="10dp"冲突，只存在一个
 *
 *
 * 注意，Space是不加下划线的
 */

open class BottomLineLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    protected var lineWidth: Float = 0.toFloat()
    protected var lineHeight: Float = 0.toFloat()
    protected var lineOffset: Float = 0.toFloat()
    private var selectedIndex: Int = 0//当前选中项

    @ColorInt
    protected var lineColor: Int = 0

    @ColorInt
    protected var lineSelectedColor: Int = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomLineLayout)
        lineColor = typedArray.getColor(R.styleable.BottomLineLayout_bottomLineColor, Color.GRAY)
        lineSelectedColor = typedArray.getColor(R.styleable.BottomLineLayout_bottomLineSelectedColor, Color.parseColor("#ff8420"))
        selectedIndex = typedArray.getInteger(R.styleable.BottomLineLayout_bottomLineIndex, 0)
        lineWidth = typedArray.getDimension(R.styleable.BottomLineLayout_bottomLineWidth, View.NO_ID.toFloat())
        lineHeight = typedArray.getDimension(R.styleable.BottomLineLayout_bottomLineHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0F, context.resources.displayMetrics))
        lineOffset = typedArray.getDimension(R.styleable.BottomLineLayout_bottomLineOffset, 0f)
        typedArray.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val pain = Paint().apply {
            style = Paint.Style.FILL
            strokeWidth = lineHeight
            isAntiAlias = false
        }

        if (childCount > 0) {
            val noSpaceViewList = ArrayList<View>()
            for (i in 0 until childCount) {
                val childView = getChildAt(i)
                if (childView is Space) {
                } else {
                    noSpaceViewList.add(childView)
                }
            }
            for (i in noSpaceViewList.indices) {
                val childView = noSpaceViewList[i]
                if (i == selectedIndex) {
                    pain.color = lineSelectedColor
                    childView.isSelected = true
                } else {
                    pain.color = lineColor
                    childView.isSelected = false
                }
                drawLine(canvas, pain, childView)
            }
        }
    }

    protected open fun drawLine(canvas: Canvas, paint: Paint, childView: View) {
        var childWidth = lineWidth
        if (lineWidth == View.NO_ID.toFloat()) {
            childWidth = childView.measuredWidth - lineOffset * 2
        }
        canvas.drawLine(childView.left + (childView.measuredWidth - childWidth) / 2,
                measuredHeight.toFloat() - lineHeight / 2 - paddingBottom.toFloat(),
                childView.left + (childView.measuredWidth + childWidth) / 2,
                measuredHeight.toFloat() - lineHeight / 2 - paddingBottom.toFloat(), paint)
    }

    fun setSelectedIndex(selectedIndex: Int) {
        this.selectedIndex = selectedIndex
        invalidate()
    }
}