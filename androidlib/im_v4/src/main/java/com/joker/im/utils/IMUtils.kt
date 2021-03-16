package com.joker.im.utils

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-14
 */

/**
 * 将一串带字母的群组id转换成ASCII码Int值，返回做标识用
 */
fun String.toNotifyInt(): Int {
    try {
        return toInt()
    } catch (e: Exception) {
        try {
            val stringBuilder = StringBuilder()
            for (i in this.length - 1 downTo 1) {//遍历字符串
                val c = this[i]
                // 数字，或英文通过
                if (c.toInt() in 48..57 || c.toInt() in 65..90 || c.toInt() in 97..122) {
                    stringBuilder.append(c.toInt() - 48)
                    // 有10个数了就直接给出来
                    if (stringBuilder.toString().length >= 9) {
                        var rest = stringBuilder.toString()
                        rest = rest.substring(0, 8)
                        return Integer.parseInt(rest)
                    }
                }
            }
            return stringBuilder.toString().toInt()
        } catch (e: Exception) {
            return 0
        }
    }
}