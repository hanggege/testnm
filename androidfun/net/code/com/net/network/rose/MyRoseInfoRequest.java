package com.net.network.rose;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.rose.MyRoseInfo;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public class MyRoseInfoRequest extends RxRequest<MyRoseInfo.Response, ApiInterface> {

    public MyRoseInfoRequest() {
        super(MyRoseInfo.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<MyRoseInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().MyRoseInfo();
    }
}
