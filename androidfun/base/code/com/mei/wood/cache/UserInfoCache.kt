package com.mei.wood.cache

import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.wood.extensions.executeKt
import com.net.model.user.UserInfo
import com.net.network.chick.user.UserBasicInfoRequest
import com.net.network.chick.user.UserMoreInfoRequest
import java.util.concurrent.ConcurrentHashMap

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/2/18
 */

/**
 * 仅从本地获取用户信息
 */
fun getCacheUserInfo(userId: Int): UserInfo? = getCacheUserInfos(arrayOf(userId)).firstOrNull()

fun getCacheUserInfos(userIds: Array<Int>): MutableList<UserInfo> {
    return UserInfoCache.instance.getCacheUserInfos(userIds)
}

fun clearUserCache() = UserInfoCache.instance.userMap.clear()


/**
 * 拉取个人信息
 * @param needRefresh 是否需要强制刷新
 */
fun RetrofitClient.requestUserInfo(userIds: Array<Int>, needRefresh: Boolean = false, back: (List<UserInfo>) -> Unit = {}) {
    UserInfoCache.instance.requestUserInfo(this, userIds.toSet().toList(), back, needRefresh=needRefresh)
}

/**
 * 拉取个人信息 (专属消息列表)
 * @param needRefresh 是否需要强制刷新
 */
fun RetrofitClient.requestImUserInfo(userIds: Array<Int>, needRefresh: Boolean = false, back: (List<UserInfo>) -> Unit = {}) {
    UserInfoCache.instance.requestUserInfo(this, userIds.toSet().toList(), back, needMore = true, needRefresh = needRefresh)
}

class UserInfoCache {

    val userMap = ConcurrentHashMap<Int, UserInfo>()

    companion object {
        val instance: UserInfoCache by lazy { UserInfoCache() }
    }

    /**
     * 仅从本地获取用户信息
     */
    fun getCacheUserInfos(userIds: Array<Int>): MutableList<UserInfo> {
        val resultList = arrayListOf<UserInfo>()
        userMap.forEach {
            if (userIds.contains(it.key)) {
                resultList.add(it.value)
            }
        }
        return resultList
    }


    /**
     * 拉取个人信息
     * @param needRefresh 是否需要强制刷新
     */
    fun requestUserInfo(
            sindarClient: RetrofitClient,
            user_ids: List<Int>,
            back: (List<UserInfo>) -> Unit,
            needMore: Boolean = false,
            needRefresh: Boolean = false
    ) {

        val resultList = arrayListOf<UserInfo>()
        val userIds = user_ids.filter { it > 0 }.toMutableList()
        if (!needRefresh) {
            userMap.forEach {
                /** 检查是否已缓存，有的话不再请求 **/
                if (userIds.contains(it.key)) {
                    resultList.add(it.value)
                    userIds.removeAll { id -> id == it.key }
                }
            }
        }
        val request = if (needMore) UserMoreInfoRequest(userIds) else UserBasicInfoRequest(userIds)
        if (userIds.isEmpty()) {
            back(resultList)
        } else {
            sindarClient.executeKt(request, success = {
                /** 缓存最新拉取的个人信息 **/
                it.data?.infoList.orEmpty().forEach { info ->
                    userMap[info.userId] = info
                }
                resultList.addAll(it.data?.infoList.orEmpty())
                back(resultList)
            }, failure = {
                back(resultList)
            })
        }
    }

}