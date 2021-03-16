package com.mei.wood.util

import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.json.toJson
import com.mei.orc.util.string.decodeUrl

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/12/4
 *
 * 解析Url协议的内链  aman://xxx?i=1&b=2&c=3
 */

/**
 * 查询所有参数
 */
fun String.parseAllValues(): MutableMap<String, String> {
    val pos = indexOf('?')
    val query = if (pos in 0 until length) substring(pos + 1) else ""
    return queryStringToNamesAndValues(query)
}

/**
 * 解析所有参数到对象中
 */
inline fun <reified T> String.parseData(defaultValue: T): T {
    val map = parseAllValues()
    return map.toJson()?.json2Obj(T::class.java) ?: defaultValue
}

/**
 * 获取指定key的参数
 */
@JvmOverloads
fun String.parseValue(key: String, defaultValue: String = ""): String = parseAllValues().getOrElse(key) { defaultValue }

fun <T> String.parseValue(key: String, defaultValue: T): T {
    val v: String = parseAllValues()[key].orEmpty()
    return convertType(v, defaultValue)
}


@Suppress("UNCHECKED_CAST")
private fun <T> convertType(v: String, defaultValue: T): T {
    var typeValue: T? = defaultValue
    try {
        typeValue = when (defaultValue) {
            is Int -> Integer.valueOf(v) as? T
            is Long -> java.lang.Long.valueOf(v) as? T
            is Double -> java.lang.Double.valueOf(v) as? T
            is Float -> java.lang.Float.valueOf(v) as? T
            is String -> v as? T
            else -> null
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return typeValue ?: defaultValue
}


private fun queryStringToNamesAndValues(encodedQuery: String): MutableMap<String, String> {
    val result = mutableMapOf<String, String>()
    var pos = 0
    while (pos <= encodedQuery.length) {
        var ampersandOffset = encodedQuery.indexOf('&', pos)
        if (ampersandOffset == -1) ampersandOffset = encodedQuery.length

        val equalsOffset = encodedQuery.indexOf('=', pos)
        if (equalsOffset == -1 || equalsOffset > ampersandOffset) {


            result[encodedQuery.substring(pos, ampersandOffset).decodeUrl()] = "" // No value for this name.
        } else {
            result[encodedQuery.substring(pos, equalsOffset).decodeUrl()] = encodedQuery.substring(equalsOffset + 1, ampersandOffset).decodeUrl()
        }
        pos = ampersandOffset + 1
    }
    return result
}
