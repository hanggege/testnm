package com.net.network.chick.friends;


import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/1/8
 * desc   :
 */
public class BlackChangeRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private int passiveUser;
    private int status;


    public BlackChangeRequest(int passiveUser, int status) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.passiveUser = passiveUser;
        this.status = status;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().changeBlack(passiveUser, status);

    }
}