package com.net.model.config

import com.mei.init.spiceHolder
import com.mei.wood.extensions.executeKt
import com.net.network.config.AppConfigLoadRequest
import com.net.network.config.AppConfigUpdateRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/8
 */

var appConfigMap = hashMapOf<String, String>()

fun requestConfigLoad(key: String = "", needRefresh: Boolean = true, back: (String) -> Unit = {}) {
    if (!needRefresh && appConfigMap[key].orEmpty().isNotEmpty()) {
        back(appConfigMap[key].orEmpty())
    } else {
        spiceHolder().apiSpiceMgr.executeKt(AppConfigLoadRequest(key), success = {
            it.data?.custom.orEmpty().forEach { entry ->
                appConfigMap[entry.key] = entry.value
            }
            /** 如果key为空的则是请求全部，不需要返回当前value **/
            back(if (key.isEmpty()) "" else appConfigMap[key].orEmpty())
        }, failure = {
            back("")
        })
    }
}

fun uploadConfigLoad(key: String, value: String) {
    spiceHolder().apiSpiceMgr.executeKt(AppConfigUpdateRequest(key, value))
}