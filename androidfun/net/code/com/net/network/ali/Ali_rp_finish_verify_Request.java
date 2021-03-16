package com.net.network.ali;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * Created by joker on 15/12/21.
 */
public class Ali_rp_finish_verify_Request extends RxRequest<Empty_data.Response, ApiInterface> {


    public Ali_rp_finish_verify_Request() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().Ali_rp_finish_verify();
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

}
