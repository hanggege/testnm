package com.net.network.chat;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/6/29
 */
public class C2cStatisticsRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    String publishId;

    public C2cStatisticsRequest(String publishId) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.publishId = publishId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().uploadC2cStatistics(publishId);
    }
}
