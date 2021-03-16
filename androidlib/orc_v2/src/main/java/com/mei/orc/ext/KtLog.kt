package com.mei.orc.ext

import android.util.Log

/**
 * 佛祖保佑         永无BUG
 *
 * Created by joker on 2017/2/7.
 */
val logTag: String = "kotlin_log"

fun Any.v(message: String) {
    v(this.javaClass.simpleName, message)
}

fun Any.v(tag: String, message: String) {
    if (tag.isEmpty()) {
        Log.v(logTag, message)
    } else {
        Log.v(tag, message)
    }
}

fun Any.i(message: String) {
    i(this.javaClass.simpleName, message)
}

fun Any.i(tag: String, message: String) {
    if (tag.isEmpty()) {
        Log.i(logTag, message)
    } else {
        Log.i(tag, message)
    }
}


fun Any.w(message: String) {
    w(this.javaClass.simpleName, message)
}

fun Any.w(tag: String, message: String) {
    if (tag.isEmpty()) {
        Log.w(logTag, message)
    } else {
        Log.w(tag, message)
    }
}


fun Any.d(message: String) {
    d(this.javaClass.simpleName, message)
}

fun Any.d(tag: String, message: String) {
    if (tag.isEmpty()) {
        Log.d(logTag, message)
    } else {
        Log.d(tag, message)
    }
}


fun Any.e(message: String) {
    e(this.javaClass.simpleName, message)
}

fun Any.e(tag: String, message: String) {
    if (tag.isEmpty()) {
        Log.e(logTag, message)
    } else {
        Log.e(tag, message)
    }

}




