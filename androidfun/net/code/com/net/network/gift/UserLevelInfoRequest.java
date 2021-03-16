package com.net.network.gift;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.gift.UserLevelInfo;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/17
 */
public class UserLevelInfoRequest extends RxRequest<UserLevelInfo.Response, ApiInterface> {
    public UserLevelInfoRequest() {
        super(UserLevelInfo.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<UserLevelInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getUserLevel();
    }
}
