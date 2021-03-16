package com.net.network.chick.friends;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.user.HomeStatus;

import io.reactivex.Observable;

/**
 * Song Jian
 */
public class HomeStatusRequest extends RxRequest<HomeStatus.Response, ApiInterface> {

    private int mTargetUserId;

    public HomeStatusRequest(int targetUserId) {
        super(HomeStatus.Response.class, ApiInterface.class);
        mTargetUserId = targetUserId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<HomeStatus.Response> loadDataFromNetwork() throws Exception {
        return getService().roomStatus(mTargetUserId);
    }

}
