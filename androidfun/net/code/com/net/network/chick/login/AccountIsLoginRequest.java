package com.net.network.chick.login;


import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * Created by 杨强彪 on 2016/5/30.
 *
 * @描述：
 */
public class AccountIsLoginRequest extends RxRequest<Empty_data.Response, ApiInterface> {


    public AccountIsLoginRequest() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().isLogin();

    }
}