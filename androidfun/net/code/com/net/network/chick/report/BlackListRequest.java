package com.net.network.chick.report;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.report.ShieldListInfo;

import io.reactivex.Observable;

/**
 * 拉黑名单
 */
public class BlackListRequest extends RxRequest<ShieldListInfo.Response, ApiInterface> {
    private String roomId = "";

    public BlackListRequest(String roomId) {
        super(ShieldListInfo.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ShieldListInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().userBlacklist(roomId);
    }
}