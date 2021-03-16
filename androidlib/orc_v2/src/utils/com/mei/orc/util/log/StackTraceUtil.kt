package com.mei.orc.util.log

import android.util.Log
import java.util.*

/**
 *  Created by zzw on 2019-07-18
 *  Des:
 */


fun printStackTrace(): String {
    val builder = StringBuilder()
    for (ele in Thread.currentThread().stackTrace) {
        builder.append("\tat ")
        builder.append(ele.toString())
        builder.append("\n")
    }
    Log.e(createTag(), builder.toString())
    return builder.toString()
}


private fun createTag(): String {
    val elements = Thread.currentThread().stackTrace
    return if (elements.size < 3) {
        "StackTraceUtil.kt"
    } else {
        val fullClassName = elements[4].className
        val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
        val methodName = elements[4].methodName
        val lineNumber = elements[4].lineNumber
        String.format(Locale.getDefault(), "%second.%second():%d", className, methodName, lineNumber)
    }
}