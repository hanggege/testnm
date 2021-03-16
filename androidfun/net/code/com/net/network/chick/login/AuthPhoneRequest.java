package com.net.network.chick.login;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.AccountLoginResult;
import com.net.ApiInterface;
import com.net.MeiUser;
import com.net.model.user.UserInfo;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class AuthPhoneRequest extends RxRequest<AccountLoginResult.Response, ApiInterface> {

    private String phoneNo;
    private String mobileCode;

    public AuthPhoneRequest(String phoneNo, String mobileCode) {
        super(AccountLoginResult.Response.class, ApiInterface.class);
        this.phoneNo = phoneNo;
        this.mobileCode = mobileCode;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<AccountLoginResult.Response> loadDataFromNetwork() throws Exception {
        UserInfo userInfo = MeiUser.getSharedUser().info;
        String nextCode = "";
        if (userInfo != null) {
            if (userInfo.phoneAuthStatus == 0) {
                nextCode = "logined_set_phone";
            } else {
                nextCode = "logined_reset_phone";
            }
        }
        return getService().authPhone(phoneNo, mobileCode, nextCode);
    }

}
