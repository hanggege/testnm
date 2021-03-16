package com.net.network.chick.friends;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.BlackListResult;

import io.reactivex.Observable;


public class BlackListRequest extends RxRequest<BlackListResult.Response, ApiInterface> {

    public BlackListRequest() {
        super(BlackListResult.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<BlackListResult.Response> loadDataFromNetwork() throws Exception {
        return getService().getBlackList();

    }
}