package com.net.network.chick.user

import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.orc.http.retrofit.RxRequest
import com.mei.wood.util.AppManager
import com.net.ApiInterface
import com.net.model.user.UserBasicInfo
import io.reactivex.Observable
import kotlin.jvm.Throws

/**
 * Created by steven on 15/4/25.
 */
class UserBasicInfoRequest(var user_ids: List<Int>, var publishUserId: String? = "") : RxRequest<UserBasicInfo.Response, ApiInterface>(
        UserBasicInfo.Response::class.java, ApiInterface::class.java
) {
    override fun createCacheKey(): String {
        return toString()
    }

    @Throws(Exception::class)
    override fun loadDataFromNetwork(): Observable<UserBasicInfo.Response?>? {
        val roomInfo = (AppManager.getInstance().currentActivity() as? VideoLiveRoomActivity)?.roomInfo
        if (publishUserId?.isNotEmpty() == false) {
            publishUserId = roomInfo?.createUser?.userId?.toString()
        }
        return service.userBaseInfo(user_ids.filter { it > 0 }.joinToString(","), publishUserId)
    }

}