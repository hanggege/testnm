package com.net.network.chick.friends;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.user.FollowFriend;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/2/21
 * desc   : 我的关注
 */
public class FollowExistRequest extends RxRequest<FollowFriend.Response, ApiInterface> {

    private int followId;

    public FollowExistRequest(int followId) {
        super(FollowFriend.Response.class, ApiInterface.class);
        this.followId = followId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<FollowFriend.Response> loadDataFromNetwork() throws Exception {
        return getService().followExist(followId);

    }
}