package com.mei.orc.sex

import android.util.Log
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.save.getNonValue
import com.mei.orc.util.save.putValue

/**
 * Created on 18-3-12
 * @author caowei
 * @blog https://646030315.github.io
 * @email 646030315@qq.com
 */

const val KEY_SEX_VERSION = "KEY_SEX_VERSION"
const val CATE_ID_LIST = "user_interest"
const val PRO_CATE_ID = "pro_cate_id_category"

const val BOY = 1
const val GIRL = 0

/**
 * 获取用户选择的性别版本
 */
fun getSexVersion(): SexVersion {
    val sex = KEY_SEX_VERSION.getNonValue(-1)

    return when (sex) {
        BOY -> SexVersion.BOY
        GIRL -> SexVersion.GIRL
        else -> SexVersion.NONE
    }
}

/**
 * 检查登录，返回性别
 */
fun checkLoginAndGetSexVersion(): SexVersion {
    return if (JohnUser.getSharedUser().hasLogin()) getSexVersion() else SexVersion.NONE
}

/**
 * 设置用户性别版本
 * @param interest v2.7.0,AB测时原来版本的兴趣
 * @param cate_id v2.7.0，AB测新版本B的品类，在结束AB测时保留一个
 */
fun SexVersion.saveSexVersionAndInterest(interest: String = "", cate_id: Int = 0) {
    val lowerSex = sexValue()

    when (lowerSex) {
        BOY, GIRL -> {
            KEY_SEX_VERSION.putValue(lowerSex)
        }
        else -> Log.e("SexManager", "错误的性别")
    }
    CATE_ID_LIST.putValue(interest)
    PRO_CATE_ID.putValue(cate_id)
}

/**
 * 获取用户选择的性别版本
 */
fun getInterest(): String = CATE_ID_LIST.getNonValue("")

/**
 * v2.7.0 AB测中B版选择的品类
 */
fun getProCateId(): Int = PRO_CATE_ID.getNonValue(0)


/**
 * 设置登录用户原本选择的性别版本，用于选择兴趣时高亮
 */
fun SexVersion.saveOriginInterest(interest: String) {
    getOriginInterestKey().putValue(interest)
}

/**
 * 用户性别版本切换时，获取原版的兴趣数据
 */
fun SexVersion.getOriginInterest(): String = getOriginInterestKey().getNonValue("")

private fun SexVersion.getOriginInterestKey(): String = "origin_cate_id_list${JohnUser.getSharedUser().userID}${sexValue()}"

