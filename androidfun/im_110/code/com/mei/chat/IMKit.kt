package com.mei.chat

import android.content.Context
import com.mei.base.network.netchange.checkNetAndLogin
import com.mei.chat.ext.IMExt
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.cache.getCacheUserInfo
import com.net.MeiUser

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
private val impl: IMExt by lazy {
    IMExt()
}

@JvmOverloads
fun Context.toImChat(chatId: String, isGroup: Boolean = false, ob: Any? = null) {
    checkNetAndLogin(back = {
        if (it) {
            val info = getCacheUserInfo(chatId.toInt())
            if (info?.isPublisher == MeiUser.getSharedUser().info?.isPublisher) {
                UIToast.toast(this, if (info?.isPublisher == true) "暂不支持知心达人之间的私聊功能" else "暂不支持用户之间的私聊功能")
            } else impl.toImChat(this, chatId, isGroup, ob)
        }
    })
}
