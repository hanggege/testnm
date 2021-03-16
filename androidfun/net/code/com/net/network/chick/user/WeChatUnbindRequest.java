package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/2/25
 * desc   : 微信解绑
 */
public class WeChatUnbindRequest extends RxRequest<Empty_data.Response, ApiInterface> {


    private String mState;

    public WeChatUnbindRequest(String state) {
        super(Empty_data.Response.class, ApiInterface.class);
        mState = state;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().unbindWechat(mState);
    }
}