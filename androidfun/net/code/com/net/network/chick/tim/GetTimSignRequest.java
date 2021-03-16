package com.net.network.chick.tim;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.tim.UserSig;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class GetTimSignRequest extends RxRequest<UserSig.Response, ApiInterface> {
    private int userId;

    @Override
    public String toString() {
        return "GetTimSignRequest{" +
                "userId=" + userId +
                '}';
    }

    public GetTimSignRequest(int userId) {
        super(UserSig.Response.class, ApiInterface.class);
        this.userId = userId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<UserSig.Response> loadDataFromNetwork() throws Exception {
        return getService().getTimSin(userId);
    }
}
