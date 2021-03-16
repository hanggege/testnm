package com.net.network.chick.login;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.login.OauthConnectResult;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/7/13.
 */

public class OauthConnectRequest extends RxRequest<OauthConnectResult.Response, ApiInterface> {

    private String authorization_code;
    private String state;
    private String bind;

    public OauthConnectRequest(String state, String authorization_code) {
        super(OauthConnectResult.Response.class, ApiInterface.class);
        this.authorization_code = authorization_code;
        this.state = state;
    }

    public OauthConnectRequest(String state, String authorization_code, String bind) {
        super(OauthConnectResult.Response.class, ApiInterface.class);
        this.authorization_code = authorization_code;
        this.state = state;
        this.bind = bind;
    }


    @Override
    public String toString() {
        return "OauthConnectRequest{" +
                "authorization_code='" + authorization_code + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<OauthConnectResult.Response> loadDataFromNetwork() throws Exception {
        return getService().oauthConnect(authorization_code, state, bind);
    }
}
