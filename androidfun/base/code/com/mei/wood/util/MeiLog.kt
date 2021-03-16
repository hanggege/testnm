package com.mei.wood.util

import android.util.Log
import com.mei.wood.BuildConfig

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/10/26
 */

fun logDebug(tag: CharSequence, msg: CharSequence) {
    if (BuildConfig.IS_TEST) Log.e("$tag", "$msg")
}

fun logDebug(msg: CharSequence) {
    if (BuildConfig.IS_TEST) Log.e("info", "$msg")
}