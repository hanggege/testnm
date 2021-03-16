package com.mei.orc.util.string

import android.text.Html
import android.text.Spanned
import java.net.URLDecoder

/**
 * Created by hang on 2019-07-19.
 */
fun String?.getHtmlText(): Spanned? {
    if (this.isNullOrEmpty()) return null

    val decodeString = this.htmlDecode()
    val result = decodeString.decodeUrl()
    if (result.isNotEmpty()) {
        @Suppress("DEPRECATION")
        return Html.fromHtml(result)
    } else {
        return null
    }
}

fun String.htmlDecode(): String {
    return this.replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace("&#39;", "\'")
            .replace("&quot;", "\"")
            .replace("&nbsp;".toRegex(), "")
}

/**
 * @param errorSelf 错误时返回原来的还是空字符串
 */
@JvmOverloads
fun String?.decodeUrl(errorSelf: Boolean = true): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        URLDecoder.decode(this, "UTF-8")
    } catch (e: Exception) {
        e.printStackTrace()
        if (errorSelf) this else ""
    }
}