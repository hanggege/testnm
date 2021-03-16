package com.mei.wood.util

import com.google.gson.reflect.TypeToken
import com.mei.orc.util.json.json2Obj

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/12/17
 *
 * 解析Url协议的特别的内链，后面只接json的  xiaolu://xxx/$json
 */

/**
 * 查询所有参数
 */
fun String.parseJsonAll(): MutableMap<String, Any> {
    val index = indexOf("{")
    return queryStringToNamesAndValues(
            if (index in 0 until length) substring(index)
            else this
    )
}

/**
 * 获取指定key的参数
 */
fun String.parseJsonValue(key: String, defaultValue: String = ""): String {
    val v: Any? = parseJsonAll()[key]
    return v?.toString() ?: defaultValue
}

private fun queryStringToNamesAndValues(encodedQuery: String): MutableMap<String, Any> {
    var result = mutableMapOf<String, Any>()
    val token = object : TypeToken<Map<String, Any>>() {}.type

    encodedQuery.json2Obj<Map<String, Any>>(token)?.toMutableMap()?.let {
        result = it
    }

    return result
}