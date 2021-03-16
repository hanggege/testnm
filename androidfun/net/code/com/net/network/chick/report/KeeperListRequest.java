package com.net.network.chick.report;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.report.ShieldListInfo;

import io.reactivex.Observable;

/**
 * 房管名单
 */
public class KeeperListRequest extends RxRequest<ShieldListInfo.Response, ApiInterface> {

    String roomId = "";

    public KeeperListRequest(String roomId) {
        super(ShieldListInfo.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ShieldListInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().keeperList(roomId);
    }
}