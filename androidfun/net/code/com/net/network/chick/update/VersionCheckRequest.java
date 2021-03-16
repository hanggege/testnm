package com.net.network.chick.update;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.update.VersionCheckResult;

import io.reactivex.Observable;

/**
 * 检测更新
 */
public class VersionCheckRequest extends RxRequest<VersionCheckResult.Response, ApiInterface> {


    public VersionCheckRequest() {
        super(VersionCheckResult.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<VersionCheckResult.Response> loadDataFromNetwork() throws Exception {
        return getService().appVersionCheck();
    }


}
