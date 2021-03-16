package com.joker.im

import com.joker.im.ext.TencentExt
import com.tencent.imsdk.TIMConversation
import com.tencent.imsdk.friendship.TIMFriend

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-13
 */

private val impl: TencentExt by lazy {
    TencentExt()
}

/**
 * 退出IM
 */
fun imLogout(success: () -> Unit = {}, failure: (Int, String) -> Unit = { _, _ -> }) {
    impl.imLogout(success, failure)
}

/**
 * IM是否已登录
 */
fun imIsLogin() = impl.imIsLogin()

/**
 *IM登录用户ID
 */
//fun imLoginId() = impl.imLoginId()
fun imLoginId() = impl.imLoginId()

/**
 * 获取除免打扰外的全部未读消息
 */
fun getAllMessageUnreadNum(filter: (TIMConversation) -> Boolean = { _ -> true }, back: (Int) -> Unit) {
    impl.getAllMessageUnreadNum(back, filter = filter)
}

/**
 * 是否在黑名单内
 */
fun isImUserBlacked(identifier: String, back: (Boolean) -> Unit) {
    impl.isImUserBlacked(identifier, back)
}

/**
 * 加入黑名单
 */
fun addImUserBlacked(identifier: String, success: (Boolean) -> Unit) {
    impl.addImUserBlacked(identifier, success)
}

/**
 * 移除黑名单
 */
fun removeImUserBlacked(identifier: String, success: (Boolean) -> Unit) {
    impl.removeImUserBlacked(identifier, success)
}

/**
 * 黑名单列表
 */
fun blackedList(back: (List<TIMFriend>?) -> Unit) {
    impl.blackedList(back)
}



