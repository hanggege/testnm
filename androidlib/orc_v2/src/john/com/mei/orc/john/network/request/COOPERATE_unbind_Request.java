package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class COOPERATE_unbind_Request extends RxRequest<Empty_data.Response, JohnInterface> {
    private String state = "";//aman_weibo_android  aman_weixin_android

    @Override
    public String toString() {
        return "COOPERATE_unbind_Request{" +
                "state='" + state + '\'' +
                '}';
    }

    public COOPERATE_unbind_Request(String state) {
        super(Empty_data.Response.class, JohnInterface.class);
        this.state = state;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().COOPERATE_unbind(state);
    }
}
