package com.net.network.chick.friends;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.UserHomePagerResult;

import io.reactivex.Observable;

/**
 * Song Jian
 */
public class UserHomePagerRequest extends RxRequest<UserHomePagerResult.Response, ApiInterface> {

    private int mTargetUserId;

    public UserHomePagerRequest(int targetUserId) {
        super(UserHomePagerResult.Response.class, ApiInterface.class);
        mTargetUserId = targetUserId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<UserHomePagerResult.Response> loadDataFromNetwork() throws Exception {
        return getService().homePage(mTargetUserId);
    }

}
