package com.mei.orc.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * 佛祖保佑         永无BUG
 *
 * Created by joker on 2017/2/7.
 */

/**
 * 获取Intent
 */
inline fun <reified T : Activity> Context.getIntent(vararg params: Pair<String, Any?>): Intent {
    val intent = Intent(this, T::class.java)
    if (params.isNotEmpty()) fillIntentArguments(intent, params)
    return intent
}

inline fun <reified T : Activity> Fragment.getIntent(vararg params: Pair<String, Any?>): Intent {
    val intent = Intent(activity, T::class.java)
    if (params.isNotEmpty()) fillIntentArguments(intent, params)
    return intent
}

fun Any.getEmptyIntent(vararg params: Pair<String, Any?>): Intent {
    val intent = Intent()
    if (params.isNotEmpty()) fillIntentArguments(intent, params)
    return intent
}

fun fillIntentArguments(intent: Intent, params: Array<out Pair<String, Any?>>) {
    params.forEach {
        val value = it.second
        if (value != null) {
            when (value) {
                is Int -> intent.putExtra(it.first, value)
                is Long -> intent.putExtra(it.first, value)
                is CharSequence -> intent.putExtra(it.first, value)
                is String -> intent.putExtra(it.first, value)
                is Float -> intent.putExtra(it.first, value)
                is Double -> intent.putExtra(it.first, value)
                is Char -> intent.putExtra(it.first, value)
                is Short -> intent.putExtra(it.first, value)
                is Boolean -> intent.putExtra(it.first, value)
                is Serializable -> intent.putExtra(it.first, value)
                is Bundle -> intent.putExtra(it.first, value)
                is Parcelable -> intent.putExtra(it.first, value)
                is Array<*> -> when {
                    value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                    value.isArrayOf<String>() -> intent.putExtra(it.first, value)
                    value.isArrayOf<Parcelable>() -> intent.putExtra(it.first, value)
                    else -> throw RuntimeException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
                }
                is IntArray -> intent.putExtra(it.first, value)
                is LongArray -> intent.putExtra(it.first, value)
                is FloatArray -> intent.putExtra(it.first, value)
                is DoubleArray -> intent.putExtra(it.first, value)
                is CharArray -> intent.putExtra(it.first, value)
                is ShortArray -> intent.putExtra(it.first, value)
                is BooleanArray -> intent.putExtra(it.first, value)
                else -> throw RuntimeException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
        }
    }
}
