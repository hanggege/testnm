package com.net.network.chick.handpick;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.room.HandPickResult;

import io.reactivex.Observable;

/**
 * Created by hang on 2018/10/29.
 * 安卓首页推荐A版网络请求
 * 首页数据统一改为V5接口
 */
public class HandPickRequest extends RxRequest<HandPickResult.Response, ApiInterface> {

    public HandPickRequest() {
        super(HandPickResult.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<HandPickResult.Response> loadDataFromNetwork() throws Exception {
        return getService().handPick();
    }

}
