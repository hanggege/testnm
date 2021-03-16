package com.net.network.chick.report;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.user.ShieldingInfo;

import io.reactivex.Observable;

/**
 * 删除拉黑
 */
public class ShieldingDeleteRequest extends RxRequest<ShieldingInfo.Response, ApiInterface> {

    String userId = "";
    String roomId = "";

    public ShieldingDeleteRequest(String userId, String roomId) {
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
        return getService().blackDelete(userId, roomId);
    }
}