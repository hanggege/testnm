package com.net.network.config;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.config.UPLOAD_uptoken;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/25.
 */
public class UPLOAD_uptoken_Request extends RxRequest<UPLOAD_uptoken.Response, ApiInterface> {


    public UPLOAD_uptoken_Request() {
        super(UPLOAD_uptoken.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<UPLOAD_uptoken.Response> loadDataFromNetwork() throws Exception {
        return getService().uploadToken();
    }
}
