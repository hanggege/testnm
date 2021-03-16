package com.mei.live.views.room

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.wood.R
import kotlinx.android.synthetic.main.view_live_skill_loop.view.*

/**
 * Created by hang on 2020/11/13.
 */
class LiveSkillLoopView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    init {
        layoutInflaterKtToParentAttach(R.layout.view_live_skill_loop)
    }

    fun initData(skills: List<String>) {
        if (skills.isEmpty()) {
            isVisible = false
        } else {
            isVisible = true
            val skillStr = skills.joinToString("、")
            val skillStrList = splitText(dip(150), generateTextView("").paint, skillStr)
            skillStrList.forEach {
                view_flipper.addView(generateTextView(it))
            }
            if (skillStrList.size > 1) {
                view_flipper.setOutAnimation(context, R.anim.anim_flipper_out)
                view_flipper.setInAnimation(context, R.anim.anim_flipper_in)
            }
        }
    }

    private fun generateTextView(string: String): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            setTextColor(Color.WHITE)
            text = string
        }
    }

    private fun splitText(tvWidth: Int, tvPaint: Paint, string: String): List<String> {
        if (string.isEmpty()) {
            return arrayListOf()
        }
        if (tvWidth <= 0) {
            return arrayListOf(string)
        }
        //将原始文本按行拆分
        val rawTextLines = string.replace("\r".toRegex(), "").split("\n").toTypedArray()
        val sbNewText = StringBuilder()
        val strList = arrayListOf<String>()
        for (rawTextLine in rawTextLines) {
            if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                //如果整行宽度在控件可用宽度之内，就不处理了
                strList.add(rawTextLine)
            } else {
                //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                var lineWidth = 0f
                var cnt = 0
                while (cnt != rawTextLine.length) {
                    val ch = rawTextLine[cnt]
                    lineWidth += tvPaint.measureText(ch.toString())
                    if (lineWidth <= tvWidth) {
                        sbNewText.append(ch)
                        if (sbNewText.startsWith("、")) {
                            sbNewText.deleteCharAt(0)
                        }
                        if (cnt == rawTextLine.length - 1) {
                            strList.add(sbNewText.toString())
                        }
                    } else {
                        if (sbNewText.endsWith("、")) {
                            sbNewText.deleteCharAt(sbNewText.length - 1)
                        }
                        strList.add(sbNewText.toString())
                        sbNewText.clear()

                        lineWidth = tvPaint.measureText(ch.toString())
                        sbNewText.append(ch)
                    }
                    ++cnt
                }
            }
        }
        return strList
    }
}