package com.net.network.chick.login;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.JohnUser;
import com.net.ApiInterface;
import com.net.model.chick.login.LoginWithOauthResult;

import io.reactivex.Observable;


public class UserAccountPasswordLoginRequest extends RxRequest<LoginWithOauthResult.Response, ApiInterface> {

    private String account = "";
    private String password = "";
    private String checksum = "";

    private String machineCode = "";

    public UserAccountPasswordLoginRequest(String account, String password, String checksum) {
        super(LoginWithOauthResult.Response.class, ApiInterface.class);
        this.account = account;
        this.password = password;
        this.checksum = checksum;
        this.machineCode = JohnUser.getSharedUser().phoneSN;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<LoginWithOauthResult.Response> loadDataFromNetwork() throws Exception {
        return getService().userAccountPasswordLogin(account, password, checksum, machineCode);
    }

}
