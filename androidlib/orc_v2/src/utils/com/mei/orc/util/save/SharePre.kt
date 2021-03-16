package com.mei.orc.util.save

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.mei.orc.Cxt
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.json.toJson

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/12
 * 系统的数据保存方式
 */

inline var sp: PreferenceData
    set(_) {}
    get() = PreferenceData.instance

fun <T> String.getValue(defaultValue: T?): T? = sp.getValue(this, defaultValue)

fun <T> String.getNonValue(defaultValue: T): T = sp.getValue(this, defaultValue) ?: defaultValue

inline fun <reified T> String.getNonObjectValue(defaultValue: T): T {
    return sp.getValue(this, "")?.json2Obj(T::class.java) ?: return defaultValue
}

fun <T> String.putValue(value: T) = sp.putValue(this, value)

class PreferenceData {
    companion object {
        val instance: PreferenceData by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { PreferenceData() }
    }

    private var sharedPreference: SharedPreferences? = null

    /** 上面的[sharedPreference]是用来缓存的 ，下面的[shareEditor]用来内部操作的**/
    private var shareEditor: SharedPreferences?
        set(_) {}
        get() = if (sharedPreference != null) sharedPreference else initShared(Cxt.get())

    fun initShared(context: Context?): SharedPreferences? {
        sharedPreference = context?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        return sharedPreference
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    fun <T> getValue(key: String, defaultValue: T?): T? {
        val data = when (defaultValue) {
            is Int -> shareEditor?.getInt(key, defaultValue)
            is String -> shareEditor?.getString(key, defaultValue)
            is Boolean -> shareEditor?.getBoolean(key, defaultValue)
            is Float -> shareEditor?.getFloat(key, defaultValue)
            is Long -> shareEditor?.getLong(key, defaultValue)
            else -> null
        }
        return (data as? T) ?: defaultValue
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    fun <T> putValue(key: String, value: T?) {
        when (value) {
            is Int -> shareEditor?.edit { putInt(key, value) }
            is String -> shareEditor?.edit { putString(key, value) }
            is Boolean -> shareEditor?.edit { putBoolean(key, value) }
            is Float -> shareEditor?.edit { putFloat(key, value) }
            is Long -> shareEditor?.edit { putLong(key, value) }
            else -> shareEditor?.edit { putString(key, value.toJson().orEmpty()) }
        }
    }

    fun removeValuesForKeys(vararg keys: String) = keys.forEach { shareEditor?.edit { remove(it) } }
}