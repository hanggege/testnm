package com.mei.orc.util.date

import java.util.*

/**
 *
 * @author Created by Ling on 2019-08-01
 */


inline var Calendar.year: Int
    get() = get(Calendar.YEAR)
    set(value) {
        set(Calendar.YEAR, value)
    }
inline var Calendar.month: Int
    get() = get(Calendar.MONTH)
    set(value) {
        set(Calendar.MONTH, value)
    }
inline var Calendar.dayYear: Int
    get() = get(Calendar.DAY_OF_YEAR)
    set(value) {
        set(Calendar.DAY_OF_YEAR, value)
    }
inline var Calendar.dayMonth: Int
    get() = get(Calendar.DAY_OF_MONTH)
    set(value) {
        set(Calendar.DAY_OF_MONTH, value)
    }
inline var Calendar.dayWeek: Int
    get() = get(Calendar.DAY_OF_WEEK)
    set(value) {
        set(Calendar.DAY_OF_WEEK, value)
    }

/**
 * 是否是同一天
 */
fun Calendar?.isSameDay(other: Calendar?) = this != null && other != null
        && this.year == other.year && this.month == other.month && this.dayMonth == other.dayMonth

fun Calendar.dayOfWeek(): String = when (get(Calendar.DAY_OF_WEEK)) {
    Calendar.MONDAY -> "周一"
    Calendar.TUESDAY -> "周二"
    Calendar.WEDNESDAY -> "周三"
    Calendar.THURSDAY -> "周四"
    Calendar.FRIDAY -> "周五"
    Calendar.SATURDAY -> "周六"
    Calendar.SUNDAY -> "周日"
    else -> {
        ""
    }
}