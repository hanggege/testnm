package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * Created by joker on 16/1/6.
 */
public class MOBILE_verify_mobile_code_Request extends RxRequest<Empty_data.Response, JohnInterface> {
    private String phone_no;
    private String mobile_code;

    public MOBILE_verify_mobile_code_Request(String phone_no, String mobile_code) {
        super(Empty_data.Response.class, JohnInterface.class);
        this.phone_no = phone_no;
        this.mobile_code = mobile_code;
    }

    @Override
    public String toString() {
        return "MOBILE_verify_mobile_code_Request{" +
                "phone_no='" + phone_no + '\'' +
                ", mobile_code='" + mobile_code + '\'' +
                '}';
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().MOBILE_verify_mobile_code(this.phone_no, this.mobile_code);
    }
}
