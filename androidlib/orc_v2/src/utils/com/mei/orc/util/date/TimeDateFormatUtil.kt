package com.mei.orc.util.date

import com.mei.orc.BuildConfig
import com.mei.orc.unit.TimeUnit
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 * @author Created by Ling on 2019-07-31
 * 日期格式化
 */

/**
 * yyyy-MM-dd HH:mm:ss
 * 指定日期并按需求格式化
 */
fun String?.formatDateToPattern(@DateFormatType toPattern: String): String {
    return formatDateFromPatternToPattern(DateFormatType.DateFormatYMdHms, toPattern)
}

fun Long?.formatDateToPattern(@DateFormatType pattern: String): String {
    if (this == null || this == 0L) return ""
    val sDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return sDateFormat.format(Date(this))
}

/**
 * 指定日期并按需求格式化
 */
fun String?.formatDateFromPatternToPattern(@DateFormatType fromPattern: String, @DateFormatType toPattern: String): String {
    if (this.isNullOrEmpty()) {
        return ""
    }
    return try {
        val format = SimpleDateFormat(fromPattern, Locale.getDefault())
        val resultFormat = SimpleDateFormat(toPattern, Locale.getDefault())
        val date = format.parse(this)
        resultFormat.format(date!!)
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}

/**
 * 转换到字符串
 *
 * @param fmt 日期格式化字符串
 */
@JvmOverloads
fun Date?.formatToString(@DateFormatType fmt: String = DateFormatType.DateFormatYMdHms): String {
    if (this == null) return ""
    return try {
        val sdf = SimpleDateFormat(fmt, Locale.getDefault())
        sdf.format(this)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

/**
 * 从字符串解析为日期型
 */
@JvmOverloads
fun String?.parseToDate(@DateFormatType fromPattern: String = DateFormatType.DateFormatYMdHms): Date? {
    if (this.isNullOrEmpty()) return null
    return try {
        val sdf = SimpleDateFormat(fromPattern, Locale.getDefault())
        sdf.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 从字符串解析为the number of milliseconds
 */
fun String?.parseToMill(@DateFormatType fromPattern: String = DateFormatType.DateFormatYMdHms): Long {
    if (this.isNullOrEmpty()) return 0L
    return try {
        val sdf = SimpleDateFormat(fromPattern, Locale.getDefault())
        sdf.parse(this)?.time ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
        0L
    }
}

/**
 * 从毫秒值转成字符串格式
 */
fun Long?.parseString(@DateFormatType fmt: String = DateFormatType.DateFormatYMdHms): String {
    if (this == null || this == 0L) return ""
    return try {
        val sdf = SimpleDateFormat(fmt, Locale.getDefault())
        sdf.format(Date(this))
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

/**
 * 获取时间的毫秒值
 */
fun String?.formatDateMilliSecondTime(@DateFormatType pattern: String): Long {
    if (this.isNullOrEmpty()) return 0L
    return try {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        val date = format.parse(this)
        date?.time ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
        0L
    }
}


/**
 * 获取时间的毫秒值
 */
fun String?.formatDateMilliSecondTime(): Long {
    return formatDateMilliSecondTime(DateFormatType.DateFormatYMdHms)
}

/**
 * 2019-07-31 15:44:26
 * @return 刚刚、3分钟前、3小时前、3天前、7月31日等
 */
fun String?.formatDataBefore(): String {
    if (this.isNullOrEmpty()) return ""
    try {
        val format = SimpleDateFormat(DateFormatType.DateFormatYMdHms, Locale.getDefault())//yyyy-MM-dd HH:mm:ss
        val d1 = Date()
        val d2 = format.parse(this.orEmpty())
        val diff = (d1.time - d2!!.time) / 1000
        val diffData: String
        when {
            diff < 3600 -> {
                val minInter = diff / 60
                diffData = if (minInter <= 0) {
                    "刚刚"
                } else {
                    minInter.toString() + "分钟前"
                }
            }
            diff < 3600 * 24 -> {
                val hourInter = diff / 3600
                diffData = hourInter.toString() + "小时前"
            }
            diff < 3600 * 24 * 30 -> {
                val dayInter = diff / (3600 * 24)
                diffData = dayInter.toString() + "天前"
            }
            else -> {
                val format2 = SimpleDateFormat(DateFormatType.DateFormatMD, Locale.getDefault())//MM月dd日
                diffData = format2.format(d2)
            }
        }
        return diffData
    } catch (e: Exception) {
        e.printStackTrace()
        return this.orEmpty()
    }
}

/**
 * timeMillis 1560246056000
 * @return 刚刚、3分钟前、3小时前、3天前、7月31日等
 */
fun Long?.formatDataBefore(): String {
    if (this == null) return ""
    try {
        val d1 = Date()
        val d2 = Date(this)
        val diff = (d1.time - d2.time) / 1000
        val diffData: String
        when {
            diff < 3600 -> {
                val minInter = diff / 60
                diffData = if (minInter <= 0) {
                    "刚刚"
                } else {
                    minInter.toString() + "分钟前"
                }
            }
            diff < 3600 * 24 -> {
                val hourInter = diff / 3600
                diffData = hourInter.toString() + "小时前"
            }
            diff < 3600 * 24 * 30 -> {
                val dayInter = diff / (3600 * 24)
                diffData = dayInter.toString() + "天前"
            }
            else -> {
                val format2 = SimpleDateFormat(DateFormatType.DateFormatMD, Locale.getDefault())
                diffData = format2.format(d2)
            }
        }
        return diffData
    } catch (e: Exception) {
        e.printStackTrace()
        return toString()
    }
}

/**
 * 较短时间的格式化
 * @return 3小时，31分钟 一般用于倒计时
 */
fun Int?.formatTimeHours(): String {
    if (this == null) {
        return ""
    }
    return if (this < 60 * 60) {
        (this / 60 + 1).toString() + "分钟"
    } else {
        (this / 60 / 60 + 1).toString() + "小时"
    }
}

/**
 *  2019-07-31 15:44:26
 *  @param needDay 是否带 7月31日
 *  @return 7月31日 15:44 或 15:44
 */
fun String?.formatDateDayHoursMin(needDay: Boolean): String {
    return if (needDay) {
        formatDateToPattern(DateFormatType.DateFormatMDHm)//MM月dd日 HH:mm
    } else {
        formatDateToPattern(DateFormatType.DateFormatHm)//HH:mm
    }
}

/**
 * 2019-07-31 15:44:26
 * @return 今天15:44、7月31日 15:44
 */
fun String?.formatDateToday(): String {
    if (this.isNullOrEmpty()) {
        return ""
    }
    val format = SimpleDateFormat(DateFormatType.DateFormatYMdHms, Locale.getDefault())//"yyyy-MM-dd HH:mm:ss"
    var date: Date? = null
    try {
        date = format.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return when {
        date == null -> ""
        date.isToday() -> "今天 " + formatDateDayHoursMin(false)
        else -> formatDateDayHoursMin(true)
    }
}

/**
 * 2019-07-31 15:44:26
 * @return 今天15:44、明天15:44、7月31日 15:44
 */
fun String?.formatDateTomorrow(): String {
    if (this.isNullOrEmpty()) {
        return ""
    }
    val format = SimpleDateFormat(DateFormatType.DateFormatYMdHms, Locale.getDefault())//"yyyy-MM-dd HH:mm:ss"
    var date: Date? = null
    try {
        date = format.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return when {
        date == null -> ""
        date.isToday() -> "今天 " + formatDateDayHoursMin(false)
        date.isTheDay(dateTomorrow()) -> "明天 " + formatDateDayHoursMin(false)
        else -> formatDateDayHoursMin(true)
    }
}

/**
 * yyyy-MM-dd HH:mm:ss
 * @return yyyy年MM月
 */
fun String?.formatDateYearsMouth(): String {
    return formatDateToPattern(DateFormatType.DateFormatYM)
}

/**
 * MM-dd
 * @return MM月dd日
 */
fun String?.formatDateMouthDay(): String {
    return formatDateFromPatternToPattern(DateFormatType.DateFormatMd, DateFormatType.DateFormatMD) //"MM-dd" -> MM月dd日
}

/**
 * 音视频时间格式化工具
 * @return h小时m分钟/m分钟/s秒
 */
fun Long?.formatTimeAudio(): String {
    if (this == null) return ""
    var timeResult: Long = this
    val format: SimpleDateFormat
    if (BuildConfig.DEBUG && timeResult > TimeUnit.DAY)
        throw IllegalArgumentException("time$this")
    if (timeResult == 0L) {
        timeResult = 60000
    }
    format = when {
        timeResult < TimeUnit.MINUTE -> SimpleDateFormat("s秒", Locale.CHINA)
        timeResult < TimeUnit.HOUR -> SimpleDateFormat("m分钟", Locale.CHINA)
        else -> SimpleDateFormat("h小时m分钟", Locale.CHINA)
    }
    return format.format(Date(timeResult))
}

/**
 * 倒计时时间格式 01：01
 *
 */
fun Long?.formatTimeDown(): String {
    if (this == null) return ""
    var timeResult: Long = this
    if (BuildConfig.DEBUG && timeResult > TimeUnit.HOUR)
        throw IllegalArgumentException("time$this")
    if (timeResult == 0L) {
        return ""
    }
    val format: SimpleDateFormat = SimpleDateFormat("mm:ss", Locale.CHINA)
    return format.format(Date(timeResult))
}

/**
 * 较短时间的格式化
 * @return 3'0" 一般用于发送语音
 */
@Deprecated("过时")
fun Long?.formatTimeMessage() = this?.toInt()?.formatTimeVoice() ?: ""

/**
 * 较短时间的格式化
 * @return 3'31" 一般用于音频时长
 */
fun Int?.formatTimeVoice(): String {
    if (this == null) {
        return ""
    }
    return when {
        this <= 60 -> toString() + "\""
        this % 60 == 0 -> (this / 60).toString() + "'"
        else -> (this / 60).toString() + "'" + this % 60 + "\""
    }
}

/**
 * timeMillis
 * @return 12:07:13
 */
fun Long?.formatTimeVideo(): String = this?.toInt().formatTimeToHMS()

fun Int?.formatTimeToHMS(): String {
    if (this == null) return ""
    val h = this / 3600
    val m = this / 60 % 60
    val s = this % 60
    val format = DecimalFormat("00")
    return when {
        h > 0 -> "${format.format(h)}:${format.format(m)}:${format.format(s)}"
        else -> "${format.format(m)}:${format.format(s)}"
    }
}

fun Int?.formatTimeToHMS2(): String {
    if (this == null) return ""
    val h = this / 3600
    val m = this / 60 % 60
    val s = this % 60
    val format = DecimalFormat("00")
    return when {
        h > 0 -> "${format.format(h)}时${format.format(m)}分${format.format(s)}秒"
        else -> "${format.format(m)}分${format.format(s)}秒"
    }
}
