package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.AccountLoginResult;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class ACCOUNT_register_with_phone_no_Request extends RxRequest<AccountLoginResult.Response, JohnInterface> {
    private String user_name = "";
    private String user_salt = "";
    private String user_password = "";
    private String phone_no = "";
    private String mobile_code = "";

    @Override
    public String toString() {
        return "ACCOUNT_register_with_phone_no_Request{" +
                "user_name='" + user_name + '\'' +
                ", user_salt='" + user_salt + '\'' +
                ", user_password='" + user_password + '\'' +
                ", phone_no='" + phone_no + '\'' +
                ", mobile_code='" + mobile_code + '\'' +
                '}';
    }

    public ACCOUNT_register_with_phone_no_Request(String user_name, String user_salt, String user_password, String phone_no, String mobile_code) {
        super(AccountLoginResult.Response.class, JohnInterface.class);
        this.user_name = user_name;
        this.user_salt = user_salt;
        this.user_password = user_password;
        this.phone_no = phone_no;
        this.mobile_code = mobile_code;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<AccountLoginResult.Response> loadDataFromNetwork() throws Exception {
        return getService().ACCOUNT_register_with_phone_no(user_name, user_salt, user_password, phone_no, mobile_code);
    }
}
