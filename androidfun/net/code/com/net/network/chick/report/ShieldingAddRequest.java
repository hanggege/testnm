package com.net.network.chick.report;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.user.ShieldingInfo;

import io.reactivex.Observable;

/**
 * 添加拉黑
 */
public class ShieldingAddRequest extends RxRequest<ShieldingInfo.Response, ApiInterface> {

    /**
     * roomId 可传可不传
     */
    String roomId = "";
    String userId = "";

    public ShieldingAddRequest(String userId, String roomId) {
        super(ShieldingInfo.Response.class, ApiInterface.class);
        this.userId = userId;
        this.roomId = roomId;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ShieldingInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().blackAdd(userId, roomId);
    }
}