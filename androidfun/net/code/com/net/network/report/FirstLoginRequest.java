package com.net.network.report;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/9
 */
public class FirstLoginRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public FirstLoginRequest() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().FirstLogin();
    }
}
