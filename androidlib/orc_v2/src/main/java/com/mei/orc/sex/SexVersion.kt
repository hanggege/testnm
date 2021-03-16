package com.mei.orc.sex

/**
 * Created on 18-3-12
 * @author caowei
 * @blog https://646030315.github.io
 * @email 646030315@qq.com
 */

enum class SexVersion(val sex: Int) {
    /**
     * 没有获取到有效的性别
     */
    NONE(-1),

    /**
     * 男生版
     */
    BOY(1),

    /**
     * 女生版
     */
    GIRL(0);

    fun sexValue() = sex

}

fun Int.parseSex(): SexVersion {
    return when (this) {
        0 -> SexVersion.GIRL
        1 -> SexVersion.BOY
        else -> SexVersion.NONE
    }
}