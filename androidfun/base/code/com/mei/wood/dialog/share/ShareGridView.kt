package com.mei.wood.dialog.share

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import com.mei.orc.ext.dip
import com.mei.widget.choince.ChoiceView


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/29
 *
 * 列表组
 */
@SuppressLint("ViewConstructor")
class ShareGridView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {

    init {
        isClickable = true
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(Color.WHITE)
        setPadding(0, dip(5), 0, 0)
    }

    @JvmOverloads
    fun refresh(list: List<ShowItem>, back: ShareViewBack, needClose: Boolean = true): ShareGridView {
        removeAllViews()
        list.groupBy { list.indexOf(it) / 4 }
                .forEach { (_, v) ->
                    addView(LinearLayout(context).apply {
                        gravity = Gravity.CENTER_VERTICAL
                        weightSum = 4f
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        v.forEach { addView(provideItem(it, 60f, 15f, back)) }
                        setPadding(0, dip(25), 0, 0)
                    })
                }
        val divider = dividerView()
        divider.updateLayoutParams<LinearLayout.LayoutParams> {
            topMargin = dip(30)
        }
        if (needClose) {
            addView(divider)
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