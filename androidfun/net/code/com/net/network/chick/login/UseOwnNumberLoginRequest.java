package com.net.network.chick.login;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.login.LoginWithOauthResult;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/7/13.
 */

public class UseOwnNumberLoginRequest extends RxRequest<LoginWithOauthResult.Response, ApiInterface> {

    private String token;

    public UseOwnNumberLoginRequest(String token) {
        super(LoginWithOauthResult.Response.class, ApiInterface.class);
        this.token = token;
    }


    @Override
    public String toString() {
        return "UseOwnNumberLoginRequest{" +
                "token='" + token + '\'' +
                '}';
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<LoginWithOauthResult.Response> loadDataFromNetwork() throws Exception {
        return getService().useOwnNumberLogin(token);
    }
}
