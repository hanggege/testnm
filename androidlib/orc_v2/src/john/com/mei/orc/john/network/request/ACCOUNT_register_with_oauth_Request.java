package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.AccountLoginResult;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class ACCOUNT_register_with_oauth_Request extends RxRequest<AccountLoginResult.Response, JohnInterface> {
    private String user_name = "";
    private String access_token = "";
    private long seq_id;

    @Override
    public String toString() {
        return "ACCOUNT_register_with_oauth_Request{" +
                "user_name='" + user_name + '\'' +
                ", access_token='" + access_token + '\'' +
                ", seq_id='" + seq_id + '\'' +
                '}';
    }

    public ACCOUNT_register_with_oauth_Request(String user_name, String access_token, long seq_id) {
        super(AccountLoginResult.Response.class, JohnInterface.class);
        this.user_name = user_name;
        this.access_token = access_token;
        this.seq_id = seq_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<AccountLoginResult.Response> loadDataFromNetwork() throws Exception {
        return getService().ACCOUNT_register_with_oauth(user_name, access_token, seq_id);
    }
}
