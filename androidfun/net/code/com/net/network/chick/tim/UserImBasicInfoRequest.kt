package com.net.network.chick.tim

import com.mei.orc.http.retrofit.RxRequest
import com.net.ApiInterface
import com.net.model.user.UserBasicInfo
import io.reactivex.Observable

/**
 * Created by steven on 15/4/25.
 */
class UserImBasicInfoRequest(var targetId: String) : RxRequest<UserBasicInfo.Response, ApiInterface>(
        UserBasicInfo.Response::class.java, ApiInterface::class.java
) {
    override fun createCacheKey(): String {
        return toString()
    }

    @Throws(Exception::class)
    override fun loadDataFromNetwork(): Observable<UserBasicInfo.Response?>? {
        return service.userImInfo(targetId)
    }

}