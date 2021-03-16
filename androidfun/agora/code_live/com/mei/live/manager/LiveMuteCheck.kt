package com.mei.live.manager

import com.mei.init.spiceHolder
import com.mei.orc.john.model.JohnUser
import com.mei.wood.extensions.executeKt
import com.mei.wood.util.logDebug
import com.net.network.chick.user.UserAuthInfoRequest
import com.tencent.imsdk.TIMGroupManager
import com.tencent.imsdk.TIMGroupMemberInfo
import com.tencent.imsdk.TIMValueCallBack

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-25
 */
private var shutUpMap = hashMapOf<Int, Int>()

/**
 * 获取是否被禁言
 */
fun checkMuteState(groupId: String, userId: Int = JohnUser.getSharedUser().userID, back: (hasPermission: Boolean) -> Unit) {
    val cacheStatus = shutUpMap.getOrElse(userId) { -1 }
    refreshServiceMute(userId)
    if (cacheStatus > 0) {
        back(false)
    } else {
        TIMGroupManager.getInstance().getGroupMembersInfo(groupId, arrayListOf(userId.toString()),
                object : TIMValueCallBack<List<TIMGroupMemberInfo>> {
                    override fun onSuccess(list: List<TIMGroupMemberInfo>?) {
                        //禁言结束时间 - 当前时间 = 还剩下多少禁言时间 s
                        val shutUp = (list.orEmpty().firstOrNull()?.silenceSeconds
                                ?: 0L) - System.currentTimeMillis() / 1000
                        back(shutUp <= 0)
                    }

                    override fun onError(p0: Int, p1: String?) {
                        logDebug("获取是否禁言：$p0  $p1")
                        back(true)
                    }

                })
    }
}

/**
 * 获取用户是否被禁言
 */
fun checkUserMuteState(userId: Int = JohnUser.getSharedUser().userID): Boolean {
    val cacheStatus = shutUpMap.getOrElse(userId) { -1 }
    if (cacheStatus == -1) refreshServiceMute(userId)
    return cacheStatus <= 0
}

/**
 * 缓存当前用户禁言状态
 */
fun refreshServiceMute(userId: Int = JohnUser.getSharedUser().userID) {
    if (userId > 0) {
        spiceHolder().apiSpiceMgr.executeKt(UserAuthInfoRequest(userId), success = {
            it.data?.apply {
                shutUpMap[userId] = this.status
            }
        })
    }
}


