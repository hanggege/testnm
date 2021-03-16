package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class ACCOUNT_set_user_name_Request extends RxRequest<Empty_data.Response, JohnInterface> {
    private String user_name = "";

    @Override
    public String toString() {
        return "ACCOUNT_set_user_name_Request{" +
                "user_name='" + user_name + '\'' +
                '}';
    }

    public ACCOUNT_set_user_name_Request(String user_name) {
        super(Empty_data.Response.class, JohnInterface.class);
        this.user_name = user_name;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().ACCOUNT_set_user_name(user_name);
    }
}
