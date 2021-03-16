package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.ACCOUNT_get_checksum;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class ACCOUNT_get_checksum_Request extends RxRequest<ACCOUNT_get_checksum.Response, JohnInterface> {
    private String account_name = "";

    @Override
    public String toString() {
        return "ACCOUNT_get_checksum_Request{" +
                "account_name='" + account_name + '\'' +
                '}';
    }

    public ACCOUNT_get_checksum_Request(String account_name) {
        super(ACCOUNT_get_checksum.Response.class, JohnInterface.class);
        this.account_name = account_name;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<ACCOUNT_get_checksum.Response> loadDataFromNetwork() throws Exception {
        return getService().ACCOUNT_get_checksum(account_name);
    }
}
