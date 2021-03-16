package com.net.network.config;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.config.AppConfigLoad;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/8
 */
public class AppConfigLoadRequest extends RxRequest<AppConfigLoad.Response, ApiInterface> {

    public String keys;

    public AppConfigLoadRequest(String keys) {
        super(AppConfigLoad.Response.class, ApiInterface.class);
        this.keys = keys;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<AppConfigLoad.Response> loadDataFromNetwork() throws Exception {
        return getService().AppConfigLoad(keys);
    }
}
