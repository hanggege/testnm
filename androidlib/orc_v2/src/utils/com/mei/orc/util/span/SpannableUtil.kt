package com.mei.orc.util.span

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import com.mei.orc.util.string.absPattern

/**
 *
 * @author Created by Ling on 2019-08-06
 */


fun String.buildHighLightByTag(startTag: String, endTag: String, @ColorInt color: Int): SpannableString? {
    try {
        val list = absPattern(startTag)
        if (list.isNotEmpty() && startTag.isNotEmpty()) {
            var comment = this
            comment = comment.replace(startTag.toRegex(), "")
            comment = comment.replace(endTag.toRegex(), "")
            val spannableString = SpannableString(comment)
            var num = 0
            for (i in list.indices) {
                num = indexOf(startTag, num)
                val startIndex = num - (startTag.length + endTag.length) * i
                val endIndex = indexOf(endTag, num) - startTag.length * (i + 1) - endTag.length * i
                val len = if (endIndex > startIndex) endIndex else startIndex
                spannableString.setSpan(ForegroundColorSpan(color), startIndex, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                num += startTag.length
            }
            return spannableString
        } else {
            return SpannableString(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return SpannableString(this)
    }
}

/**
 * 支持特定字符为高亮显示
 */
fun String.buildHighLightByTarget(target: String, @ColorInt color: Int): SpannableString? {
    try {
        val list = absPattern(target)
        return if (list.isNotEmpty() && target.isNotEmpty()) {
            val spannableString = SpannableString(this)
            var num = 0
            for (i in list.indices) {
                num = indexOf(list[i], num)
                spannableString.setSpan(ForegroundColorSpan(color), num, num + list[i].length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                num += target.length
            }
            spannableString
        } else {
            SpannableString(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return SpannableString(this)
    }
}
