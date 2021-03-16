package com.net.network.chick.friends;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.GuardianRankingListResult;

import io.reactivex.Observable;

/**
 * 守护榜
 */
public class GuardianRankingRequest extends RxRequest<GuardianRankingListResult.Response, ApiInterface> {

    public GuardianRankingRequest() {
        super(GuardianRankingListResult.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<GuardianRankingListResult.Response> loadDataFromNetwork() throws Exception {
        // TODO: 2020/2/7 接口待完善
        return getService().getGuardianRankingList();

    }
}