package com.mei.im.ui.view.menu

import androidx.annotation.DrawableRes
import java.io.Serializable

/**
 *
 * @author Created by lenna on 2020/7/14
 * 聊天页面底部显示的功能菜单
 */
class ChatMenu : Serializable {
    var menuIcon: String = ""

    @DrawableRes
    var menuIconId = 0

    var menuName: String = ""
    var menuType: Int = 0
    var actionUrl = ""

    object MenuType {
        const val TYPE_PIC = 1  //相册
        const val TYPE_CAMERA = 2 //相机
        const val TYPE_ATTACHMENT = 3 //私密连线
        const val TYPE_SEND_GIFT = 4 // 送礼
        const val TYPE_COURSE_SERVICE = 5 //课程服务
        const val TYPE_SEND_TEST = 6 //发送测评
        const val TYPE_SHIELD_MANAGER = 7//黑名单
        const val TYPE_KEEPER_MANAGER = 8 //助教设置
        const val TYPE_SHARE_LIVE = 10
        const val TYPE_RECHARGE = 9
        const val TYPE_SUGGESTION = 11
    }
}