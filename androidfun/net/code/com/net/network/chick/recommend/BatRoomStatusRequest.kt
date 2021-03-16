package com.net.network.chick.recommend

import com.mei.orc.http.retrofit.RxRequest
import com.net.ApiInterface
import com.net.model.chick.recommend.BatRoomStatusResult
import io.reactivex.Observable

/**
 * author : Song Jian
 * date   : 2020/1/11
 * desc   :
 */
class BatRoomStatusRequest(private var user_ids: List<Int>) : RxRequest<BatRoomStatusResult.Response, ApiInterface>(
        BatRoomStatusResult.Response::class.java, ApiInterface::class.java
) {
    override fun createCacheKey(): String {
        return toString()
    }

    override fun loadDataFromNetwork(): Observable<BatRoomStatusResult.Response?>? {
        return service.batRoomStatus(user_ids.filter { it > 0 }.joinToString(","))
    }

}