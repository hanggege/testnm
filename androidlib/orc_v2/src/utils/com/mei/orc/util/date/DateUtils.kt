package com.mei.orc.util.date

import java.util.*

/**
 *
 * @author Created by Ling on 2019-08-01
 */

inline var Date.calendar: Calendar
    get() = Calendar.getInstance().apply { time = this@calendar }
    set(_) {}

/**
 * 获取当前时间
 */
fun dateNow(): Date = Date()

fun dateTomorrow(): Date {
    val calendar = GregorianCalendar()
    calendar.time = Date()
    calendar.add(Calendar.DATE, 1)
    return calendar.time
}

/**
 * 获取指定时间的那天 23:59:59.999 的时间
 */
fun Date.dayEnd(): Date {
    val c = Calendar.getInstance()
    c.time = this
    c.set(Calendar.HOUR_OF_DAY, 23)
    c.set(Calendar.MINUTE, 59)
    c.set(Calendar.SECOND, 59)
    c.set(Calendar.MILLISECOND, 999)
    return c.time
}

/**
 * 获取今天 23:59:59.999 的时间
 */
fun dayEnd(): Date {
    return dateNow().dayEnd()
}


/**
 * 是否是指定日期
 */
fun Date.isTheDay(day: Date): Boolean {
    return this.calendar.isSameDay(day.calendar)
}

/**
 * 是否是今天
 */
fun Date.isToday(): Boolean {
    return isTheDay(dateNow())
}

fun String.isToday(@DateFormatType format: String = DateFormatType.DateFormatYMdHms): Boolean {
    return parseToDate(format)?.isToday() ?: false
}

/**
 * 获取两个日期间的天数
 * 昨天是-1
 * 前天是-2
 * 明天是+1
 * 后天是-2
 */
fun Date?.amountDays(endDate: Date?): Int {
    /** 第一步先去掉小时分钟秒 **/
    val dayDate1 = this.formatToString(DateFormatType.DateFormatYMd).parseToDate(DateFormatType.DateFormatYMd)
    val dayDate2 = endDate.formatToString(DateFormatType.DateFormatYMd).parseToDate(DateFormatType.DateFormatYMd)
    return if (dayDate1 != null && dayDate2 != null) {
        ((dayDate1.time - dayDate2.time) / (1000 * 3600 * 24)).toInt()
    } else {
        0
    }
}


fun String.amountDays(end: String): Int {
    return parseToDate(DateFormatType.DateFormatYMd).amountDays(end.parseToDate(DateFormatType.DateFormatYMd))
}

/**
 * 获取两个日期间的年数
 */
fun Date?.amountYears(endDate: Date?): Int {
    return if (this != null && endDate != null) this.calendar.year - endDate.calendar.year else 0
}

