package com.mei.widget.actionbar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import com.mei.orc.R
import com.mei.widget.arrow.ArrowView
import com.mei.widget.menupoint.MenuPointView

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/12
 * 通用顶部ActionBar
 *
 *    app:left_layout="@layout/layout_name"
 *    app:center_layout="@layout/layout_name"
 *    app:right_layout="@layout/layout_name"
 *    app:line_color="@android:color/color_name"
 *    app:line_width="0.5dp"
 */
class MeiActionBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RelativeLayout(context, attrs, defStyleAttr) {
    val leftContainer: RelativeLayout by lazy {
        RelativeLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).apply {
                addRule(ALIGN_PARENT_LEFT)
            }
        }
    }
    val centerContainer: RelativeLayout by lazy {
        RelativeLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            }
            gravity = Gravity.CENTER
        }
    }
    val rightContainer: RelativeLayout by lazy {
        RelativeLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).apply {
                addRule(ALIGN_PARENT_RIGHT)
            }
        }
    }

    /** 底部分割线 **/
    val bottomLine: View by lazy {
        View(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).apply {
                addRule(ALIGN_PARENT_BOTTOM)
            }
        }
    }

    /** xml 属性 ***/
    private var left_layout: Int = View.NO_ID
    private var right_layout: Int = View.NO_ID
    private var center_layout: Int = View.NO_ID

    @ColorInt
    var line_color: Int = Color.parseColor("#E8E8E8")
        set(value) {
            field = value
            bottomLine.setBackgroundColor(value)
        }
    var line_width = 0f
        set(value) {
            field = value
            bottomLine.updateLayoutParams { height = value.toInt() }
        }


    init {
        addView(leftContainer)
        addView(rightContainer)
        addView(centerContainer)
        addView(bottomLine)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MeiActionBar)
        left_layout = typedArray.getResourceId(R.styleable.MeiActionBar_left_layout, View.NO_ID)
        right_layout = typedArray.getResourceId(R.styleable.MeiActionBar_right_layout, View.NO_ID)
        center_layout = typedArray.getResourceId(R.styleable.MeiActionBar_center_layout, View.NO_ID)
        line_color = typedArray.getColor(R.styleable.MeiActionBar_bar_line_color, Color.parseColor("#E8E8E8"))
        line_width = typedArray.getDimension(R.styleable.MeiActionBar_bar_line_width,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5F, context.resources.displayMetrics))

        if (left_layout != View.NO_ID) leftContainer.addView(LayoutInflater.from(context).inflate(left_layout, this, false))
        else leftContainer.addView(context.defaultLeftView())


        if (center_layout != View.NO_ID) centerContainer.addView(LayoutInflater.from(context).inflate(center_layout, this, false))
        else centerContainer.addView(context.defaultCenterView())


        typedArray.recycle()
        doOnLayout {
            updateCenterMargin()
        }


    }

    fun updateCenterMargin() {
        centerContainer.updateLayoutParams<LayoutParams> {
            val margin = Math.max(leftContainer.measuredWidth, rightContainer.measuredWidth)
            setMargins(margin, 0, margin, 0)
        }
    }


}


@JvmOverloads
fun Context.defaultRightTextView(txt: String, @ColorInt color: Int = Color.parseColor("#FFA000"), size: Float = 16f): TextView =
        TextView(this).apply {

            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt()
                setMargins(padding, 0, padding, 0)
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            textSize = size
            gravity = Gravity.CENTER
            setTextColor(color)
            text = txt
        }

fun Context.defaultRightImageView(@DrawableRes res: Int = View.NO_ID): ImageView =
        ImageView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT).apply {
                setMargins(0, 0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt(), 0)
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
            if (res != View.NO_ID) setImageResource(res)
        }

fun Context.defaultRightView(@ColorInt color: Int = Color.parseColor("#333333")): View =
        MenuPointView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics).toInt()
            ).apply {
                val padright = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt()
                val padleft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt()
                setPadding(padleft, 0, padright, 0)
                pointWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)
                pointColor = ColorStateList.valueOf(color)
                addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            }
        }

fun Context.defaultLeftView(): ArrowView =
        ArrowView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics).toInt()
            ).apply {
                val padlr = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt()
                val padbt = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
                setPadding(padlr, padbt, padlr, padbt)
            }
            setArrowColor(Color.parseColor("#666666"))
        }

fun Context.defaultCenterView(): TextView =
        TextView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#333333"))
        }

