package com.mei.im

import android.app.Application
import android.content.Context
import com.joker.im.Message
import com.joker.im.listener.IMAllEventManager
import com.mei.im.ext.IMInitExt

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-20
 */


private val impl: IMInitExt by lazy {
    IMInitExt()
}

/**
 * im刷新消息检测
 */
fun Context.checkIMRefresh(back: () -> Unit): IMAllEventManager = impl.checkIMRefresh(this, back)

/**
 * im新消息接收
 */
fun Context.checkIMNewMessage(back: (List<Message>) -> Unit): IMAllEventManager = impl.checkIMNewMessage(this, back)

/**
 * 查检IM被踢回调
 */
fun Context.checkIMForceOffline(back: () -> Unit): IMAllEventManager = impl.checkIMForceOffline(this, back)

/**
 * 注册im事件,登录实时检测，离线推送初始化
 */
fun Application.registerIMEvent() = impl.registerIMEvent(this)


/**
 * 快速登录IM
 */
fun quickLoginIM(success: () -> Unit = {}, failure: ((Int, String) -> Unit)? = null) {
    impl.loginIM(success, failure)
}

