package com.net.network.chick.friends;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.MyFollowListResult;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/2/21
 * desc   : 我的关注
 */
public class MyFollowkListRequest extends RxRequest<MyFollowListResult.Response, ApiInterface> {
    private int pageSize = 20;
    private int mPageNo;

    public MyFollowkListRequest(int pageNo) {
        super(MyFollowListResult.Response.class, ApiInterface.class);
        mPageNo = pageNo;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<MyFollowListResult.Response> loadDataFromNetwork() throws Exception {
        return getService().getMyFollowList(mPageNo, pageSize);

    }
}