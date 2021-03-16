package com.net.network.chick.login;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.login.LoginWithOauthResult;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class UseMobileCodeLoginRequest extends RxRequest<LoginWithOauthResult.Response, ApiInterface> {

    private String phoneNo = "";
    private String mobileCode = "";

    public UseMobileCodeLoginRequest(String phoneNo, String mobileCode) {
        super(LoginWithOauthResult.Response.class, ApiInterface.class);
        this.phoneNo = phoneNo;
        this.mobileCode = mobileCode;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<LoginWithOauthResult.Response> loadDataFromNetwork() throws Exception {
        return getService().useMobileCodeLogin(phoneNo, mobileCode);
    }

}
