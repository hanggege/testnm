package com.net.network.chick.friends;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.MyFansListResult;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/2/21
 * desc   : 我的粉丝
 */
public class MyFanskListRequest extends RxRequest<MyFansListResult.Response, ApiInterface> {
    private int pageSize = 20;
    private int mPageNo;

    public MyFanskListRequest(int pageNo) {
        super(MyFansListResult.Response.class, ApiInterface.class);
        mPageNo = pageNo;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<MyFansListResult.Response> loadDataFromNetwork() throws Exception {
        return getService().getMyFansList(mPageNo, pageSize);

    }
}