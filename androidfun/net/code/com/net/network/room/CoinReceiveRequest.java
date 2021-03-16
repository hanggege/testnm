package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.CoinReceive;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public class CoinReceiveRequest extends RxRequest<CoinReceive.Response, ApiInterface> {
    private int publisherId;

    public CoinReceiveRequest(int publisherId) {
        super(CoinReceive.Response.class, ApiInterface.class);
        this.publisherId = publisherId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<CoinReceive.Response> loadDataFromNetwork() throws Exception {
        return getService().coinReceive(publisherId);
    }
}
