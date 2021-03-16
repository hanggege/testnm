@file:Suppress("unused")

package com.mei.orc.util.save

import android.content.Context
import com.mei.orc.Cxt
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.json.toJson
import com.tencent.mmkv.MMKV

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/7
 * [MMKV] 在应用刚启动时部分机型获取不到数据，对于应用一启动就需要保存或者获取时用[PreferenceData]
 */
const val FIRST_LOAD_OLD_DATA = "first_load_old_data"

/**
 * Application初始化
 */
fun Context.initMMKV() {
    MMKV.initialize(this)
}

inline var mmkv: MMKV
    set(_) {}
    get() {
        if (MMKV.getRootDir() == null) Cxt.get()?.initMMKV()
        return MMKV.defaultMMKV()
    }

/**
 * 是否已经保存key-value
 */
fun hasKeyMMKV(key: String): Boolean = mmkv.containsKey(key)

fun removeValuesForKeys(vararg keys: String) = keys.forEach { mmkv.removeValueForKey(it) }

/****************************获取基础类型************************************/
@JvmOverloads
fun String.getStringMMKV(defaultValue: String = ""): String {
    return mmkv.getString(this, defaultValue) ?: defaultValue
}

@JvmOverloads
fun String.getIntMMKV(defaultValue: Int = 0) = mmkv.getInt(this, defaultValue)

@JvmOverloads
fun String.getLongMMKV(defaultValue: Long = 0L) = mmkv.getLong(this, defaultValue)

@JvmOverloads
fun String.getBooleanMMKV(defaultValue: Boolean = false) = mmkv.getBoolean(this, defaultValue)

/****************************获取对象************************************/
inline fun <reified T : Any> String.getAnyMMKVNotNull(defaultValue: T): T {
    return getAnyMMKV() ?: defaultValue
}

@JvmOverloads
inline fun <reified T : Any> String.getAnyMMKV(defaultValue: T? = null): T? {
    return mmkv.getString(this, "")?.json2Obj() ?: defaultValue
}

fun <T> String.getObjectMMKVNotNull(clazz: Class<T>, defaultValue: T): T {
    return getObjectMMKV(clazz) ?: defaultValue
}

@JvmOverloads
fun <T> String.getObjectMMKV(clazz: Class<T>, defaultValue: T? = null): T? {
    return mmkv.getString(this, "")?.json2Obj(clazz) ?: return defaultValue
}


fun String.getSetString(defaultValue: Set<String> = setOf()): Set<String> {
    return mmkv.getStringSet(this, setOf()) ?: defaultValue
}

private const val LIST_STRING_SEPARATOR = "|+|-|"
fun String.getListString(): MutableList<String> {
    val resultStr = getStringMMKV()
    return if (resultStr.isEmpty()) arrayListOf() else resultStr.split(LIST_STRING_SEPARATOR).toMutableList()
}


/****************************保存数据************************************/
fun String.putMMKV(set: Set<String>?) = mmkv.putStringSet(this, set ?: setOf())

fun String.putMMKV(list: MutableList<String>?) = mmkv.putString(this, list.orEmpty().joinToString(LIST_STRING_SEPARATOR))

fun String.putMMKV(data: Any?) {
    data?.let {
        when (it) {
            is Int -> mmkv.putInt(this, it)
            is String -> mmkv.putString(this, it)
            is Boolean -> mmkv.putBoolean(this, it)
            is Float -> mmkv.putFloat(this, it)
            is Long -> mmkv.putLong(this, it)
            else -> mmkv.putString(this, it.toJson().orEmpty())
        }
    }
}


