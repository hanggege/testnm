package com.mei.wood.dialog.share

import androidx.annotation.DrawableRes
import androidx.annotation.IntDef

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/29
 */
const val wechat = 101
const val wechat_circle = 102
const val weibo = 103
const val long_image = 104
const val copy = 106

@IntDef(flag = true, value = [wechat, wechat_circle, weibo, long_image, copy])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class ShowFlag


data class ShowItem(@ShowFlag val type: Int, @DrawableRes val res: Int, val title: String)