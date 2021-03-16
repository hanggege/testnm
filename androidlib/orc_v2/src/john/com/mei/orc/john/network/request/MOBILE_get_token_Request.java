package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.MOBILE_send_mobile_code;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

public class MOBILE_get_token_Request extends RxRequest<MOBILE_send_mobile_code.Response, JohnInterface> {


    private String phone_no;

    @Override
    public String toString() {
        return "MOBILE_send_mobile_code2_Request{" +
                "phone_no='" + phone_no +
                '}';
    }

    public MOBILE_get_token_Request(String phone_no) {
        super(MOBILE_send_mobile_code.Response.class, JohnInterface.class);
        this.phone_no = phone_no;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<MOBILE_send_mobile_code.Response> loadDataFromNetwork() throws Exception {
        return getService().MOBILE_get_token(phone_no);
    }
}
