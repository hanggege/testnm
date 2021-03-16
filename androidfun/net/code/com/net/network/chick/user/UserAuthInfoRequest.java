package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.user.UserAuthInfo;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-25
 */
public class UserAuthInfoRequest extends RxRequest<UserAuthInfo.Response, ApiInterface> {

    private int userId;

    public UserAuthInfoRequest(int userId) {
        super(UserAuthInfo.Response.class, ApiInterface.class);
        this.userId = userId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<UserAuthInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().UserAuthinfo(userId);
    }
}
