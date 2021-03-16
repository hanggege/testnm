package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Oauth_Connect;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/7/13.
 */

public class Oauth_Connect_Request extends RxRequest<Oauth_Connect.Response, JohnInterface> {

    private String state;
    private String authorization_code;
    private String openid;
    private String access_token;
    private boolean bind;
    private String captcha;
    private String token;


    public Oauth_Connect_Request(String state, String authorization_code, String openid, String access_token, boolean bind) {
        super(Oauth_Connect.Response.class, JohnInterface.class);
        this.state = state;
        this.authorization_code = authorization_code;
        this.openid = openid;
        this.access_token = access_token;
        this.bind = bind;
    }

    public Oauth_Connect_Request(String state, String authorization_code, String openid, String access_token, boolean bind, String captcha, String token) {
        super(Oauth_Connect.Response.class, JohnInterface.class);
        this.state = state;
        this.authorization_code = authorization_code;
        this.openid = openid;
        this.access_token = access_token;
        this.bind = bind;
        this.captcha = captcha;
        this.token = token;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Oauth_Connect.Response> loadDataFromNetwork() throws Exception {
        return getService().Oauth_Connect(state, authorization_code, openid, access_token, bind ? 1 : 0, captcha, token);
    }
}
