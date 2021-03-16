package com.net.network.chick.user

import com.mei.orc.http.retrofit.RxRequest
import com.net.ApiInterface
import com.net.model.user.UserBasicInfo
import io.reactivex.Observable

/**
 *
 * @author Created by lenna on 2020/6/16
 */
class UserMoreInfoRequest(var user_ids: List<Int>) :
        RxRequest<UserBasicInfo.Response, ApiInterface>(UserBasicInfo.Response::class.java, ApiInterface::class.java) {

    override fun createCacheKey(): String {
        return toString()
    }

    override fun loadDataFromNetwork(): Observable<UserBasicInfo.Response?>? {
        return service.userMessageListInfo(user_ids.filter { it > 0 }.joinToString(","))
    }

}