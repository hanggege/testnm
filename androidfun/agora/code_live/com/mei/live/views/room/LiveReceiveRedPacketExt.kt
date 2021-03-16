package com.mei.live.views.room

import com.mei.GrowingUtil
import com.net.model.room.RoomRedPacket

/**
 * Created by hang on 2020/11/16.
 */
/**
 * 直播间红包浏览
 */
fun LiveReceiveRedPacketFragment.redPacketBrowse(isBindPhone: Boolean) = try {
    GrowingUtil.track("function_view",
            "function_name", "领红包入口",
            "status", getRedPacketStatus(isBindPhone, packetInfo),
            "function_page", "",
            "click_type", "")
} catch (e: Exception) {
    e.printStackTrace()
}

/**
 * 点击红包领取入口
 */
fun LiveReceiveRedPacketFragment.clickRedPacketStat(isBindPhone: Boolean) = try {
    GrowingUtil.track("function_click",
            "function_name", "领红包入口",
            "status", getRedPacketStatus(isBindPhone, packetInfo),
            "click_type", "领红包",
            "function_page", "")
} catch (e: Exception) {
    e.printStackTrace()
}

/**
 * 浏览红包弹框
 */
fun LiveReceiveRedPacketFragment.redPacketBrowseDialog(isBindPhone: Boolean) = try {
    GrowingUtil.track("function_view",
            "function_name", "领红包弹窗",
            "status", getRedPacketStatus(isBindPhone, packetInfo),
            "function_page", "",
            "click_type", "")
} catch (e: Exception) {
    e.printStackTrace()
}

/**
 * 点击红包弹框领取入口
 */
fun clickRedPacketDialogStat(isBindPhone: Boolean, clickType: String, redPacket: RoomRedPacket?) = try {
    GrowingUtil.track("function_click",
            "function_name", "领红包弹窗",
            "status", getRedPacketStatus(isBindPhone, redPacket),
            "click_type", clickType,
            "function_page", "")
} catch (e: Exception) {
    e.printStackTrace()
}

fun getRedPacketStatus(isBindPhone: Boolean, redPacket: RoomRedPacket?): String {
    return if (isBindPhone) {
        if (redPacket?.thisTime == 1) "直播间首次领取" else "直播间二次领取"
    } else {
        "未绑定手机"
    }
}