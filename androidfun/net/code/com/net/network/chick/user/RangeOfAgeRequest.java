package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.user.RangeOfAgeInfo;

import io.reactivex.Observable;

/**
 * RangeOfAgeRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-18
 */
public class RangeOfAgeRequest extends RxRequest<RangeOfAgeInfo.Response, ApiInterface> {

    public RangeOfAgeRequest() {
        super(RangeOfAgeInfo.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RangeOfAgeInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getRangeOfAge();
    }
}
