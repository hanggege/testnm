package com.net.network.chick.friends;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.MyFollowListBean;

import io.reactivex.Observable;

public class BindUsersRequest extends RxRequest<MyFollowListBean.Response, ApiInterface> {

    public BindUsersRequest() {
        super(MyFollowListBean.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<MyFollowListBean.Response> loadDataFromNetwork() throws Exception {
        return getService().blindUsers();
    }

}
