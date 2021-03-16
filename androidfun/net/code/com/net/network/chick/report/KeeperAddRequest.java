package com.net.network.chick.report;


import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 添加房管
 */
public class KeeperAddRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    String roomId = "";
    String userId = "";

    public KeeperAddRequest(String roomId, String userId) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.userId = userId;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().keeperAdd(roomId, userId);
    }
}