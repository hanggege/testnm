package com.mei.wood.dialog.share

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.mei.orc.ext.dip
import com.mei.widget.choince.ChoiceView

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/29
 */
interface ShareViewBack {
    fun handle(@ShowFlag type: Int)
    fun cancel()
}

/**
 * 线性组
 */
@SuppressLint("ViewConstructor")
class ShareLineView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {

    init {
        isClickable = true
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(Color.WHITE)

    }

    private val titleTv: TextView by lazy {
        TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip(40))
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(Color.parseColor("#666666"))
            text = "分享到"
        }
    }

    fun setTitle(text: CharSequence): ShareLineView {
        titleTv.text = text
        return this
    }

    @JvmOverloads
    fun refresh(list: List<ShowItem>, back: ShareViewBack, needClose: Boolean = true): ShareLineView {
        removeAllViews()
        addView(titleTv)
        addView(LinearLayout(context).apply {
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip(112))
            list.forEach { addView(provideItem(it, 56f, 5f, back)) }
            if (list.size < 5) {
                setPadding(dip(15), 0, dip(15), 0)
            }
        })
        if (needClose) {
            addView(dividerView())
            addView(ChoiceView(context).apply {
                layoutParams = LinearLayout.LayoutParams(dip(50), dip(50)).apply { gravity = Gravity.CENTER }
                type = 2
                setPadding(dip(17))
                setOnClickListener { back.cancel() }
            })
        }
        return this

    }


}

/***
 * 分割线
 */
internal fun View.dividerView(): View =
        View(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip(0.5f))
            setBackgroundColor(Color.parseColor("#cccccc"))
        }

/**
 * Item 图上文下
 */
internal fun View.provideItem(item: ShowItem, size: Float, space: Float, back: ShareViewBack): View =
        LinearLayout(context).apply {
            setOnClickListener { back.handle(item.type) }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(object : ImageView(context) {

                /**
                 * 加点击阴影 ，画上去的
                 */
                @SuppressLint("DrawAllocation")
                override fun onDraw(canvas: Canvas) {
                    super.onDraw(canvas)
                    val paint = Paint().apply { color = if (isPressed) Color.parseColor("#22000000") else Color.TRANSPARENT }
                    canvas.drawCircle(measuredWidth.toFloat() / 2, measuredHeight.toFloat() / 2, measuredWidth.toFloat() / 2, paint)
                }

                override fun dispatchSetPressed(pressed: Boolean) {
                    super.dispatchSetPressed(pressed)
                    invalidate()
                }
            }.apply {
                isClickable = true
                setOnClickListener { back.handle(item.type) }
                layoutParams = LinearLayout.LayoutParams(dip(size), dip(size))
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(item.res)
            })
            addView(TextView(context).apply {
                text = item.title
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(Color.parseColor("#CCCCCC"))
                setPadding(0, dip(space), 0, 0)
            })
        }

