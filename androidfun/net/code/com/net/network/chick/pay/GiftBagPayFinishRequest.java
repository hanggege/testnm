package com.net.network.chick.pay;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/11/17
 */
public class GiftBagPayFinishRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private String orderSn;


    public GiftBagPayFinishRequest(String orderSn) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.orderSn = orderSn;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().giftBagPayFinish(orderSn);
    }
}
