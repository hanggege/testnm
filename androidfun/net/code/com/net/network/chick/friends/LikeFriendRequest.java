package com.net.network.chick.friends;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.LikeFriendResult;

import io.reactivex.Observable;


public class LikeFriendRequest extends RxRequest<LikeFriendResult.Response, ApiInterface> {
    private int type;

    public LikeFriendRequest(int type) {
        super(LikeFriendResult.Response.class, ApiInterface.class);
        this.type = type;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<LikeFriendResult.Response> loadDataFromNetwork() throws Exception {
        return getService().getLikeFriendList(type);

    }
}