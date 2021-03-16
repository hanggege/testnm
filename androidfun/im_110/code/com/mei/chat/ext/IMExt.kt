@file:Suppress("unused")

package com.mei.chat.ext

import android.content.Context
import android.content.Intent
import com.joker.im.imIsLogin
import com.mei.im.quickLoginIM
import com.mei.im.ui.IMC2CMessageActivity
import com.mei.im.ui.IMC2CMessageActivityLauncher
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.json.toJson
import com.mei.wood.util.AppManager
import com.mei.wood.util.logDebug


/**
 *  Created by zzw on 2019/07/10
 */

internal class IMExt {
    @Suppress("UNUSED_PARAMETER")
    fun toImChat(context: Context, chatId: String, isGroup: Boolean = false, ob: Any? = null) {
        if (!imIsLogin()) {
            UIToast.toast(context, "聊天未登录")
            quickLoginIM()
        } else if (chatId.isNotEmpty()) {
            toC2C(context, chatId, ob)
        } else {
            logDebug("IMChatHelper", "toImChat: chatId can not not null")
        }
    }

    private fun toC2C(context: Context, chatId: String, ob: Any? = null) {
        AppManager.getInstance().run {
            if (hasActivitys(IMC2CMessageActivity::class.java, VideoLiveRoomActivity::class.java).size == 2) {
                finishActivity(IMC2CMessageActivity::class.java)
            }
        }
        IMC2CMessageActivityLauncher.startActivity(context, chatId, ob?.toJson().orEmpty())
    }

    private fun Intent.putExtraIntent(ob: Any?): Intent {
        ob?.let {
//            when (it) {
//                else -> {
//                }
//            }
        }
        return this
    }
}



