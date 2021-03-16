package com.net.network.config;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/8
 */
public class AppConfigUpdateRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public String key;
    public String value;

    public AppConfigUpdateRequest(String key, String value) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.key = key;
        this.value = value;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().AppConfigUpdate(key, value);
    }
}
