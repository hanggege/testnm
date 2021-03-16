package com.mei.orc.net

import java.net.URLEncoder


/**
 * Created by joker on 2017/11/14.
 * 针对java8空格被+替换
 */

@JvmOverloads
fun String.encode(enc: String = "utf-8"): String {
    val sb = StringBuilder()
    try {
        val encodeStr = URLEncoder.encode(this, enc)
        if (encodeStr.contains('+'.toString())) {
            for (i in encodeStr.indices) {
                val c = encodeStr[i]
                if ('+' == c) {
                    sb.append("%20")
                } else {
                    sb.append(encodeStr[i])
                }
            }
        } else {
            sb.append(encodeStr)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        sb.append(this)
    }
    return sb.toString()
}

