package com.joker.im.ext

import com.tencent.imsdk.*
import com.tencent.imsdk.friendship.TIMFriend
import com.tencent.imsdk.friendship.TIMFriendResult

/**
 *  Created by zzw on 2019-07-11
 *  Des:
 */

internal class TencentExt {
    /**
     * 退出IM
     */
    fun imLogout(success: () -> Unit = {}, failure: (Int, String) -> Unit = { _, _ -> }) {
        TIMManager.getInstance().logout(object : TIMCallBack {
            override fun onSuccess() {
                success()
            }

            override fun onError(code: Int, msg: String?) {
                failure(code, msg ?: "退出失败")
            }

        })
    }


    /**
     * IM是否已登录
     */
    fun imIsLogin() = imLoginId().isNotEmpty()


    /**
     *IM登录用户ID
     * 退出登录时 [TIMManager.getInstance]的loginUser不会置空，所有不能作为是否登录的状态
     * fix it 4.4.627 版本之后解决了这个bug
     */
    //fun imLoginId() = imLoginInfo.imLoginId
    fun imLoginId() = TIMManager.getInstance().loginUser.orEmpty()


    /**
     * 获取除免打扰外的全部未读消息
     */
    fun getAllMessageUnreadNum(back: (Int) -> Unit, filter: (TIMConversation) -> Boolean = { _ -> true }) {
        if (com.joker.im.imIsLogin()) {
            val conversationList = TIMManager.getInstance().conversationList
            val unreadNum = conversationList
                    .filter { it.type != TIMConversationType.System && filter(it) }
                    .sumBy { it.unreadMessageNum.toInt() }
            back(unreadNum)
        } else back(0)
    }


    /**
     * 是否在黑名单内
     */
    fun isImUserBlacked(identifier: String, back: (Boolean) -> Unit) {
        TIMFriendshipManager.getInstance().getBlackList(object : TIMValueCallBack<List<TIMFriend>> {
            override fun onSuccess(list: List<TIMFriend>?) {
                back(list.orEmpty().any { it.identifier == identifier })
            }

            override fun onError(code: Int, msg: String?) {
                back(false)
            }

        })
    }

    /**
     * 黑名单列表
     */
    fun blackedList(back: (List<TIMFriend>?) -> Unit) {
        TIMFriendshipManager.getInstance().getBlackList(object : TIMValueCallBack<List<TIMFriend>> {
            override fun onSuccess(list: List<TIMFriend>?) {
                back(list.orEmpty())
            }

            override fun onError(code: Int, msg: String?) {
                back(null)
            }
        })
    }


    /**
     * 加入黑名单
     */
    fun addImUserBlacked(identifier: String, success: (Boolean) -> Unit) {
        TIMFriendshipManager.getInstance()
                .addBlackList(arrayListOf(identifier), object : TIMValueCallBack<List<TIMFriendResult>> {
                    override fun onSuccess(list: List<TIMFriendResult>?) {
                        success(list.orEmpty().any { it.identifier == identifier })
                    }

                    override fun onError(code: Int, msg: String?) {
                        success(false)
                    }

                })
    }


    /**
     * 移除黑名单
     */
    fun removeImUserBlacked(identifier: String, success: (Boolean) -> Unit) {
        TIMFriendshipManager.getInstance()
                .deleteBlackList(arrayListOf(identifier), object : TIMValueCallBack<List<TIMFriendResult>> {
                    override fun onSuccess(list: List<TIMFriendResult>?) {
                        success(list.orEmpty().any { it.identifier == identifier })
                    }

                    override fun onError(code: Int, msg: String?) {
                        success(false)
                    }

                })
    }


}