package com.mei

import android.content.Context
import com.mei.base.ui.nav.Nav
import com.mei.init.spiceHolder
import com.mei.orc.john.model.JohnUser
import com.mei.wood.extensions.executeKt
import com.net.network.chick.report.GIOUploadRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/3/18
 */

var deepLinkMap: MutableMap<String, String> = hashMapOf()

var actionLink = ""
fun Context.handleDeepLink() {
    if (actionLink.isNotEmpty()) {
        Nav.toAmanLink(this, actionLink)
        actionLink = ""
    }
    reportDeepLink {
        if (JohnUser.getSharedUser().hasLogin()) {
            deepLinkMap.clear()
        }
    }
}

fun reportDeepLink(success: () -> Unit = {}) {
    if (deepLinkMap.isNotEmpty()) {
        //  上报服务器
        spiceHolder().apiSpiceMgr.executeKt(GIOUploadRequest(deepLinkMap), success = {
            if (it.isSuccess) success()
        })
    }
}