package com.mei.wood.ui

import android.content.Context
import com.mei.GrowingUtil

/**
 * @author Created by lenna on 2020/8/7
 */



/**
 * 点击红包领取入口
 */
fun MeiCustomActivity.clickRedPacketStat() = try {
    GrowingUtil.track("function_click",
            "function_name", "领红包入口",
            "status", "未绑定手机",
            "click_type", "领红包",
            "function_page", "")
} catch (e: Exception) {
    e.printStackTrace()
}

/**
 * 浏览红包弹框
 */
fun MeiCustomActivity.redPacketBrowseDialog() = try {
    GrowingUtil.track("function_view",
            "function_name", "领红包弹窗",
            "status", "未绑定手机",
            "function_page", "",
            "click_type", "")
} catch (e: Exception) {
    e.printStackTrace()
}

/**
 * 点击红包弹框领取入口
 */
fun Context.clickRedPacketDialogStat(clickType: String) = try {
    GrowingUtil.track("function_click",
            "function_name", "领红包弹窗",
            "status", "未绑定手机",
            "click_type", clickType,
            "function_page", "")
} catch (e: Exception) {
    e.printStackTrace()
}

