package com.net.model.chick.tab

import com.mei.orc.util.save.getNonObjectValue
import com.mei.orc.util.save.putValue
import com.net.MeiUser

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/6/11
 */

const val TABBAR_SAVE_KEY = "TABBAR_SAVE_KEY"
var tabbarConfig: TabBar = TabBar()
    get() {
        return TABBAR_SAVE_KEY.getNonObjectValue(TabBar())
    }
    set(value) {
        field = value
        TABBAR_SAVE_KEY.putValue(value)
    }

fun clearTabItem() {
    val tabInfo = tabbarConfig
    tabInfo.tabs = arrayListOf()
    tabbarConfig = tabInfo
}

fun isShowShieldingBtn(publishUserIsShowShield: Boolean = false): Boolean {
    return if (MeiUser.getSharedUser().info?.isPublisher != true) {
        tabbarConfig.blockEnable
    } else {
        publishUserIsShowShield
    }
}