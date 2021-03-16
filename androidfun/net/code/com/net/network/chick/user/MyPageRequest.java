package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.user.MyPageInfo;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/2/25
 * desc   : 我的页面
 */
public class MyPageRequest extends RxRequest<MyPageInfo.Response, ApiInterface> {


    public MyPageRequest() {
        super(MyPageInfo.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<MyPageInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().myPage();
    }
}