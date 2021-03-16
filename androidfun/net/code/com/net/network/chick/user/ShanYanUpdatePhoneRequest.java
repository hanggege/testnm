package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/7/24
 */
public class ShanYanUpdatePhoneRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private String token;
    private int fromPage;

    public ShanYanUpdatePhoneRequest(String token, int fromPage) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.token = token;
        this.fromPage = fromPage;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().userNumberBind(token, fromPage);
    }
}
