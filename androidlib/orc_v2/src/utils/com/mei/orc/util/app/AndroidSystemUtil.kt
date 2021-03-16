package com.mei.orc.util.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.webkit.WebSettings
import com.mei.orc.Cxt
import com.mei.orc.ui.toast.UIToast
import java.io.File
import java.util.*


/**
 * Created by 杨强彪 on 2016/11/11.
 *
 * @ 描述： 系统相关工具类
 */

/**
 * 添加通讯录 (调用拨号面板)
 */
fun Context.doAddContacts(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phoneNumber")
    startActivity(intent)
}

/**
 * 根据文件后缀名获得对应的MIME类型。
 */
fun getMIMEType(file: File): String {
    var type = "*/*"
    val fName = file.name
    //获取后缀名前的分隔符"."在fName中的位置。
    val dotIndex = fName.lastIndexOf(".")
    if (dotIndex < 0) {
        return type
    }
    /* 获取文件的后缀名 */
    val end = fName.substring(dotIndex).toLowerCase(Locale.getDefault())
    if (TextUtils.isEmpty(end)) return type
    //在MIME和文件类型的匹配表中找到对应的MIME类型。
    for (aMIME_MapTable in MIME.MIME_MapTable) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
        if (end == aMIME_MapTable[0])
            type = aMIME_MapTable[1]
    }
    return type
}


/**
 * 复制某文本到剪切板。
 */
fun Context?.copyToClipboard(content: CharSequence): Boolean {
    if (this == null) {
        UIToast.toast(Cxt.get(), "复制失败")
        return false
    }
    return try {
        val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cmb.setPrimaryClip(ClipData.newPlainText(null, content))
        true
    } catch (e: Exception) {
        e.printStackTrace()
        UIToast.toast(this, "复制失败")
        false
    }

}

/**
 * 从剪切板获取复制信息
 */
fun Context?.getCopyFromClipBoard(defaultStr: CharSequence): CharSequence {
    if (this == null) {
        return ""
    }
    return try {
        val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var result = defaultStr
        if (cmb.hasPrimaryClip()) {
            result = cmb.primaryClip?.getItemAt(0)?.text ?: ""
        }
        result
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

/**
 * 清空剪贴板内容
 */
fun Context?.clearClipboard() {
    if (this == null) {
        return
    }
    try {
        val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cmb.setPrimaryClip(ClipData.newPlainText(null, ""))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context?.getUserAgent(): String? {
    if (this == null) {
        return ""
    }
    val userAgent: String? = try {
        WebSettings.getDefaultUserAgent(this)
    } catch (e: java.lang.Exception) {
        System.getProperty("http.agent")
    }
    val sb = StringBuffer()
    var i = 0
    val length = userAgent?.length ?: 0
    while (i < length) {
        val c = userAgent?.get(i)
        if (c != null) {
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", c.toInt()))
            } else {
                sb.append(c)
            }
        }
        i++
    }
    return sb.toString()
}


