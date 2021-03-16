package com.net.network.chick.pay;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;


public class PayFinishRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    private String orderSn;


    public PayFinishRequest(String orderSn) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.orderSn = orderSn;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().payFinish(orderSn);
    }


}
