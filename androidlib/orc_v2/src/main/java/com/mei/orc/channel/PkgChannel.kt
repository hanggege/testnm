package com.mei.orc.channel

import com.mei.orc.Cxt
import com.meituan.android.walle.WalleChannelReader

/**
 * 佛祖保佑         永无BUG
 *
 *
 * Created by joker on 2017/3/6.
 */


private var UMENG_CHANNEL_VALUE: String = ""

// 提交更新
var channelId: String
    get() {
        if (UMENG_CHANNEL_VALUE.isNotEmpty()) {
            return UMENG_CHANNEL_VALUE
        }

        UMENG_CHANNEL_VALUE = try {
            WalleChannelReader.getChannel(Cxt.get()).orEmpty().removeSuffix("$")
        } catch (e: Exception) {
            ""
        }

        if (UMENG_CHANNEL_VALUE.isEmpty()) {
            UMENG_CHANNEL_VALUE = "other"
        }

        return UMENG_CHANNEL_VALUE
    }
    set(value) {
        UMENG_CHANNEL_VALUE = value
    }


