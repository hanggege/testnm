package com.net.network.gift;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.gift.GiftListInfo;

import io.reactivex.Observable;

/**
 * @author Created by lh
 */
public class GiftListInfoRequest extends RxRequest<GiftListInfo.Response, ApiInterface> {

    public GiftListInfoRequest() {
        super(GiftListInfo.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<GiftListInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().giftListInfo();
    }
}
