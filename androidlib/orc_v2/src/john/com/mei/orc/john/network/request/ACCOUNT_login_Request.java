package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.AccountLoginResult;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class ACCOUNT_login_Request extends RxRequest<AccountLoginResult.Response, JohnInterface> {
    private String account_name = "";
    private String user_password = "";
    private String checksum = "";

    @Override
    public String toString() {
        return "ACCOUNT_login_Request{" +
                "account_name='" + account_name + '\'' +
                ", user_password='" + user_password + '\'' +
                ", checksum='" + checksum + '\'' +
                '}';
    }

    public ACCOUNT_login_Request(String account_name, String user_password, String checksum) {
        super(AccountLoginResult.Response.class, JohnInterface.class);
        this.account_name = account_name;
        this.user_password = user_password;
        this.checksum = checksum;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<AccountLoginResult.Response> loadDataFromNetwork() throws Exception {
        return getService().ACCOUNT_login(account_name, user_password, checksum);
    }
}
