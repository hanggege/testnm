package com.mei.live.ui.ext

import androidx.annotation.DrawableRes
import com.mei.wood.R
import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * 用户等级配置（
 * 用户等级显示分为两个size normal big
 * 用户分五个等级有5个level
 * ）
 *  Created by zhanyinghui on 2020/4/13
 *  Des:
 */
@DrawableRes
fun getBackGroundLevelResource(level: Int, size: LevelSize): Int {
    return when (size) {
        LevelSize.Normal -> {
            getNormalBackgroundResource(level)
        }
        LevelSize.Big -> {
            getBigBackgroundResource(level)
        }

    }
}


fun getNormalBackgroundResource(level: Int): Int {
    return when (level) {
        0 -> R.drawable.normal_level_zero
        in 1..5 -> R.drawable.normal_level_one
        in 6..10 -> R.drawable.normal_level_two
        in 11..15 -> R.drawable.normal_level_three
        in 16..20 -> R.drawable.normal_level_four
        else -> -1
    }
}

fun getBigBackgroundResource(level: Int): Int {
    return when (level) {
        0 -> R.drawable.big_level_zero
        in 1..5 -> R.drawable.big_level_one
        in 6..10 -> R.drawable.big_level_two
        in 11..15 -> R.drawable.big_level_three
        in 16..20 -> R.drawable.big_level_four
        else -> -1
    }
}

enum class LevelSize {
    Normal,
    Big
}

fun formatContribute(num: Long, unit: String): String {
    return if (num < 10000) {
        num.toString()
    } else {
        val format: NumberFormat = DecimalFormat("#.##")
        format.format((num).toFloat() / 10000) + unit
    }
}

fun formatOneContribute(num: Long, unit: String): String {
    return if (num < 10000) {
        num.toString()
    } else {
        val format: NumberFormat = DecimalFormat("#.#")
        format.format((num).toFloat() / 10000) + unit
    }
}