package com.mei.orc.util.string

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 *
 * @author Created by Ling on 2019-07-31
 */

/**
 * string转int
 *
 * @param defaultValue 默认值
 */
fun String?.getInt(defaultValue: Int = 0): Int {
    if (this.isNullOrEmpty()) return defaultValue
    return try {
        this.toInt()
    } catch (e: Exception) {
        e.printStackTrace()
        defaultValue
    }
}

/**
 * string转double
 *
 * @param defaultValue 默认值
 */
fun String?.getDouble(defaultValue: Double = 0.0): Double {
    if (this.isNullOrEmpty()) return defaultValue
    return try {
        java.lang.Double.parseDouble(this)
    } catch (e: Exception) {
        e.printStackTrace()
        defaultValue
    }
}

/**
 * string转long
 *
 * @param defaultValue 默认值
 */
fun String?.getLong(defaultValue: Long = 0L): Long {
    if (this.isNullOrEmpty()) return defaultValue
    return try {
        java.lang.Long.parseLong(this)
    } catch (e: Exception) {
        e.printStackTrace()
        defaultValue
    }
}

/**
 * string转Float
 *
 * @param defaultValue 默认值
 */
fun String?.getFloat(defaultValue: Float = 0f): Float {
    if (this.isNullOrEmpty()) return defaultValue
    return try {
        java.lang.Float.parseFloat(this)
    } catch (e: Exception) {
        e.printStackTrace()
        defaultValue
    }

}


/**
 * 格式化两位小数
 */
fun Double?.formatPrice(): String {
    if (this == null) return "0.00"
    val df = DecimalFormat("#0.00")
    return try {
        df.format(this)
    } catch (e: Exception) {
        "0.00"
    }
}

/**
 * 格式化为99+
 */
fun Int?.maxNinetyNine(): String {
    var string = ""
    if (this ?: 0 >= 100) {
        string = "99+"
    } else if (this ?: 0 > 0) {
        string = this.toString()
    }
    return string
}

/**
 * 格式化为99+
 */
fun Long?.maxNinetyNine(): String {
    var string = ""
    if (this ?: 0 >= 100) {
        string = "99+"
    } else if (this ?: 0 > 0) {
        string = this.toString()
    }
    return string
}


/**
 * 格式化数字count为 1~9999,1万
 */
fun Int?.formatChineseCount(zero: String = ""): String {
    val sb = StringBuilder()
    val count = this ?: 0
    when {
        count <= 0 -> return zero
        count <= 9999 -> sb.append(count)
        else -> {
            sb.append(count / 10000)
            if (count % 10000 / 1000 > 0) {
                sb.append(".")
                sb.append(count % 10000 / 1000)
            }
            sb.append("万")
        }
    }
    return sb.toString()
}

/**
 * 格式化数字count为 1~999,1k~9.9k,1w~9.9w,9.9w+
 */
fun Int?.formatCount(zero: String = ""): String {
    val count = this ?: 0
    val sb = StringBuilder()
    when {
        count <= 0 -> return zero
        count <= 999 -> sb.append(count)
        count <= 9999 -> {
            sb.append(count / 1000)
            if (count % 1000 / 100 > 0) {
                sb.append(".")
                sb.append(count % 1000 / 100)
            }
            sb.append("k")
        }
        count <= 99999 -> {
            sb.append(count / 10000)
            if (count % 10000 / 1000 > 0) {
                sb.append(".")
                sb.append(count % 10000 / 1000)
            }
            sb.append("w")
        }
        else -> sb.append("9.9w+")
    }
    return sb.toString()
}


fun Int?.formatMaxNumK(zero: String = ""): String {
    val count = this ?: 0
    val sb = StringBuilder()
    when {
        count <= 0 -> return zero
        count <= 999 -> sb.append(count)
        count <= 9999 -> {
            sb.append(count / 1000)
            if (count % 1000 / 100 > 0) {
                sb.append(".")
                sb.append(count % 1000 / 100)
            }
            sb.append("k")
        }
        else -> sb.append("9.9K+")
    }
    return sb.toString()
}


/**
 * 当浮点数小数位为0时省略掉
 *
 * @param
 * @return
 */
fun Float?.floatTrans(): String {
    val num = this ?: 0f
    return if (Math.round(num) - num == 0f) {
        num.toLong().toString()
    } else num.toString()
}

/**
 * 分隔千分位
 *
 * 推荐格式化类型
 * [Long] [Int] [Short] [Byte] [Number]
 * @return
 */

fun Any?.formatThousands(): String {
    return try {
        val format = DecimalFormat("0,000")
        format.format(this) ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

/**
 * 格式化小数，取N位
 * @param maxDecimal 最多小数位
 */
fun Double.smartFormat(maxDecimal: Int): String {
    //无小数和小数位为0
    if (this == toLong().toDouble() || maxDecimal == 0) {
        return toLong().toString()
    }
    //最多为负数时返回原值
    if (maxDecimal < 0) {
        return toString()
    }
    val result = BigDecimal(this).setScale(maxDecimal, BigDecimal.ROUND_HALF_UP).toDouble()
    //格式完的小数也要去0处理
    return if (result == result.toLong().toDouble()) {
        result.toLong().toString()
    } else {
        result.toString()
    }
}