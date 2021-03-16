package com.net.network.chick.login;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.login.LoginWithOauthResult;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class LoginWithOauthRequest extends RxRequest<LoginWithOauthResult.Response, ApiInterface> {
    private String nickname = "";
    private String avatar = "";
    private long seqId;


    @Override
    public String toString() {
        return "LoginWithOauthRequest{" +
                "nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", seqId=" + seqId +
                '}';
    }

    public LoginWithOauthRequest(String user_name, String avatar, long seq_id) {
        super(LoginWithOauthResult.Response.class, ApiInterface.class);
        this.nickname = user_name;
        this.avatar = avatar;
        this.seqId = seq_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<LoginWithOauthResult.Response> loadDataFromNetwork() throws Exception {
        return getService().loginWithOauth(nickname, avatar, seqId);
    }
}
