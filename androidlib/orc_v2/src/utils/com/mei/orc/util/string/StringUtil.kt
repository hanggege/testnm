package com.mei.orc.util.string

import androidx.annotation.RawRes
import com.mei.orc.Cxt
import com.mei.orc.util.json.json2Obj
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *
 * @author Created by Ling on 2019-08-02
 */

fun String?.absPattern(regex: String): List<String> {
    val result: MutableList<String> = arrayListOf()
    if (this.isNullOrEmpty()) {
        return result
    }
    try {
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(this)
        while (matcher.find()) {
            result.add(matcher.group())
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return result
}

/**
 * 连接多个字符串
 *
 * @param args
 * @return
 */
fun stringConcate(vararg args: Any?): String {
    if (args.size < 4) {
        var result = ""
        for (s in args) {
            result += s
        }
        return result
    }
    return StringBuilder().append(*args).toString()
}

// android
fun String?.nonNullOf(): String {
    return orEmpty()
}

/**
 * 连接多个字符串,args间用splitStr连接
 *
 * @param splitStr
 * @param args
 * @return
 */
fun concateWithSplit(splitStr: String, args: Collection<*>?): String {
    return concateWithSplit(StringBuilder(), splitStr, args).toString()
}


/**
 * 连接多个字符串,args间用splitStr连接,返回StringBuilder
 *
 * @param tmp      StringBuilder的引用
 * @param splitStr 连接符
 * @param args     多个字符串
 * @return
 */
fun concateWithSplit(tmp: StringBuilder, splitStr: String, args: Collection<*>?): StringBuilder {
    if (args == null || args.isEmpty()) {
        return tmp
    }
    for (obj in args) {
        tmp.append(obj)
        tmp.append(splitStr)
    }
    tmp.delete(tmp.length - splitStr.length, tmp.length)
    return tmp
}

/**
 * String的hashCode再转string
 */
fun String?.hashStringOf(): String {
    return hashCode().toString()
}

fun <T> readRaw2Obj(@RawRes rawId: Int, tClass: Class<T>): T? = stringFromRawResourceId(rawId)?.json2Obj(tClass)

/**
 * 从raw文件中读取内容
 * @param resID R.raw.xxx
 */
fun stringFromRawResourceId(@RawRes resID: Int): String? {
    val inputStream = Cxt.get().resources.openRawResource(resID)
    var baos: ByteArrayOutputStream? = null
    var jsonStr: String? = null
    try {
        baos = ByteArrayOutputStream()
        val buffer = ByteArray(1024 * 8)
        var read: Int
        while (inputStream.read(buffer).let { read = it; it != -1 }) {
            baos.write(buffer, 0, read)
        }
        val data = baos.toByteArray()
        jsonStr = String(data, StandardCharsets.UTF_8)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            inputStream.close()
        } catch (e: Exception) {
        }
        try {
            baos?.close()
        } catch (e: Exception) {
        }
    }
    return jsonStr
}

//去空去换行
fun String.replaceBlank(): String? {
    var dest = ""
    if (this.isNotEmpty()) {
        val p = Pattern.compile("\t|\r|\n")
        val m: Matcher = p.matcher(this)
        dest = m.replaceAll(" ")
        return dest
    }
    return dest
}