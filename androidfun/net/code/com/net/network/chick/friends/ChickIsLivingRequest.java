package com.net.network.chick.friends;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.ChickIsLivingResult;

import io.reactivex.Observable;

public class ChickIsLivingRequest extends RxRequest<ChickIsLivingResult.Response, ApiInterface> {

    private int userId;

    public ChickIsLivingRequest(int userId) {
        super(ChickIsLivingResult.Response.class, ApiInterface.class);
        this.userId = userId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<ChickIsLivingResult.Response> loadDataFromNetwork() throws Exception {
        return getService().chickIsLiving(userId);
    }

}
