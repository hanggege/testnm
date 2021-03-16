package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.user.MyBalanceInfo;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public class MyBalanceInfoRequest extends RxRequest<MyBalanceInfo.Response, ApiInterface> {

    public MyBalanceInfoRequest() {
        super(MyBalanceInfo.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<MyBalanceInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().myBalance();
    }
}
