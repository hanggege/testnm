package com.mei.base.weight.textview

import android.graphics.Paint
import android.text.TextUtils
import android.widget.TextView
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by hang on 2020/6/29.
 */
private const val regEx: String = "[\u4e00-\u9fa5]" // 中文范围

/**
 * 格式化字符串
 * @param string 原始输入字符串
 * @param maxCount 最大字符限制，中文算作2个字符，其他都算1个字符
 * @return
 */
fun String.formatText(maxCount: Int): String {
    return if (getStrLen(this) > maxCount * 2) {
        subStrByLen(this, 2 * maxCount)
    } else this
}

/**
 *
 * 截取字符串，超出最大字数截断并显示"..."
 * @param str 原始字符串
 * @param length 最大字数限制（以最大字数限制7个为例，当含中文时，length应设为2*7，不含中文时设为7）
 * @return 处理后的字符串
 */
private fun subStrByLen(str: String, length: Int): String {
    if (str.isEmpty()) {
        return ""
    }
    val chCnt = getStrLen(str)
    // 超出进行截断处理
    if (chCnt > length) {
        var cur = 0
        var cnt = 0
        val sb = StringBuilder()
        while (cnt <= length && cur < str.length) {
            val nextChar = str[cur]
            if (isChCharacter(nextChar.toString())) {
                cnt += 2
            } else {
                cnt++
            }
            if (cnt <= length) {
                sb.append(nextChar)
            } else {
                return "$sb..."
            }
            cur++
        }
        return "$sb..."
    }
    // 未超出直接返回
    return str
}

/**
 * 获取字符串中的中文字数
 */
private fun getChCount(str: String): Int {
    var cnt = 0
    val pattern: Pattern = Pattern.compile(regEx)
    val matcher: Matcher = pattern.matcher(str)
    while (matcher.find()) {
        cnt++
    }
    return cnt
}

/**
 * 判断字符是不是中文
 */
private fun isChCharacter(str: String): Boolean {
    if (str.isEmpty()) {
        return false
    }
    return if (str.length > 1) {
        false
    } else Pattern.matches(regEx, str)
}

/**
 * 获取字符长度，中文算作2个字符，其他都算1个字符
 */
private fun getStrLen(str: String): Int {
    return str.length + getChCount(str)
}

/**
 * 半角转全角
 * @return 全角字符串.
 */
fun String.toSBC(): String {
    val c = this.toCharArray()
    for (i in c.indices) {
        if (c[i] == ' ') {
            c[i] = '\u3000'
        } else if (c[i] < '\u007f') {
            c[i] = (c[i] + 65248)
        }
    }
    return String(c)
}

fun autoSplitText(tv: TextView, rawText: String): String {
    if (TextUtils.isEmpty(rawText)) {
        return ""
    }
    val tvPaint: Paint = tv.paint //paint，包含字体等信息
    val tvWidth = tv.width - tv.paddingLeft - tv.paddingRight.toFloat() //控件可用宽度
    if (tvWidth <= 0) {
        return rawText
    }
    //将原始文本按行拆分
    val rawTextLines = rawText.replace("\r".toRegex(), "").split("\n").toTypedArray()
    val sbNewText = StringBuilder()
    for (rawTextLine in rawTextLines) {
        if (tvPaint.measureText(rawTextLine) <= tvWidth) {
            //如果整行宽度在控件可用宽度之内，就不处理了
            sbNewText.append(rawTextLine)
        } else {
            //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
            var lineWidth = 0f
            var cnt = 0
            while (cnt != rawTextLine.length) {
                val ch = rawTextLine[cnt]
                lineWidth += tvPaint.measureText(ch.toString())
                if (lineWidth <= tvWidth) {
                    sbNewText.append(ch)
                } else {
                    sbNewText.append("\n")
                    lineWidth = 0f
                    --cnt
                }
                ++cnt
            }
        }
        sbNewText.append("\n")
    }
    //把结尾多余的\n去掉
    if (!rawText.endsWith("\n")) {
        sbNewText.deleteCharAt(sbNewText.length - 1)
    }
    return sbNewText.toString()
}