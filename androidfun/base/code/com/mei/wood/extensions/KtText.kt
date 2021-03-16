package com.mei.wood.extensions

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*
import androidx.annotation.ColorInt
import androidx.core.text.set
import com.mei.wood.span.CenterVerticalSpan
import com.mei.wood.span.ClickSpan

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/1/4
 */

/**
 * 支持TextView跳转的Spannable
 * 这里有个问题，如果存在多个#[link] 一样的字段时会出现[link]都会进行标记跳转，
 * 请用下面的方法[appendLink]拼接出来
 */
fun Spannable.buildLink(
        link: String,
        color: Int = Color.parseColor("#FFC564"),
        radio: Float = 0F,//相对字体大小，原字体比例
        click: (() -> Unit)? = null
): Spannable {
    if (link.isNotEmpty()) {
        var index = indexOf(link)
        while (index >= 0) {
            if (click != null) {
                set(index, index + link.length, ClickSpan(color) { click() })
            } else {
                set(index, index + link.length, ForegroundColorSpan(color))
            }
            if (radio > 0) {
                set(index, index + link.length, RelativeSizeSpan(radio))
            }
            index = indexOf(link, index + link.length)
        }
    }
    return this
}

fun SpannableStringBuilder.appendLink(
        link: String,
        @ColorInt color: Int = Color.parseColor("#FFC564"),
        radio: Float = 0F,//相对字体大小，原字体比例
        deleteEnable: Boolean = false,//是否启用删除线
        underLine: Boolean = false,//是否启用下划线
        style: Int = 0, //0,没有 1，加粗 2，斜体 3：加粗斜
        @ColorInt backgroundColor: Int = 0,//背景色
        verticalCenter: Boolean = false, //是否居中显示
        click: ((String) -> Unit)? = null
): SpannableStringBuilder {
    if (link.isNotEmpty()) {
        val colorSpan: Any = if (click == null) ForegroundColorSpan(color)
        else ClickSpan(color) { click(link) }
        val spanList = arrayListOf(colorSpan)

        if (radio > 0 && radio != 1f) {
            spanList.add(RelativeSizeSpan(radio))
        }
        if (deleteEnable) {
            spanList.add(StrikethroughSpan())
        }
        if (underLine) {
            spanList.add(UnderlineSpan())
        }
        when (style) {
            1 -> spanList.add(StyleSpan(Typeface.BOLD))
            2 -> spanList.add(StyleSpan(Typeface.ITALIC))
            3 -> spanList.add(StyleSpan(Typeface.BOLD_ITALIC))
        }

        if (backgroundColor != 0) {
            spanList.add(BackgroundColorSpan(backgroundColor))
        }
        if (verticalCenter) {
            spanList.add(CenterVerticalSpan())
        }
        appendSpannable(link, spanList)
    }
    return this
}

/**
 * 设置首行缩进
 * @param indent 缩进宽度
 */
fun SpannableStringBuilder.buildFirstIndent(indent: Int): SpannableStringBuilder {
    set(0, length, LeadingMarginSpan.Standard(indent, 0))
    return this
}

fun SpannableStringBuilder.appendSpannable(text: String, span: Any): SpannableStringBuilder {
    return appendSpannable(text, arrayListOf(span))
}

fun SpannableStringBuilder.appendSpannable(text: String, spans: List<Any>): SpannableStringBuilder {
    val start = length
    append(text)
    spans.forEach { setSpan(it, start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
    return this
}


fun String.subStringEnd(len: Int, postfix: String = "..."): String {
    return if (length > len) {
        "${this.substring(0, len - 1)}$postfix"
    } else this
}