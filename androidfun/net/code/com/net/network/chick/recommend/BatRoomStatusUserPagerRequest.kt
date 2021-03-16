package com.net.network.chick.recommend

import com.mei.orc.http.retrofit.RxRequest
import com.net.ApiInterface
import com.net.model.chick.recommend.BatRoomStatusNewResult
import io.reactivex.Observable

/**
 * author : Song Jian
 * date   : 2020/1/11
 * desc   :
 */
class BatRoomStatusUserPagerRequest(var user_ids: List<Int>) : RxRequest<BatRoomStatusNewResult.Response, ApiInterface>(
        BatRoomStatusNewResult.Response::class.java, ApiInterface::class.java
) {
    override fun createCacheKey(): String {
        return toString()
    }

    @Throws(Exception::class)
    override fun loadDataFromNetwork(): Observable<BatRoomStatusNewResult.Response?>? {
        return service.batchUserStatus(user_ids.filter { it > 0 }.joinToString(","))
    }

}