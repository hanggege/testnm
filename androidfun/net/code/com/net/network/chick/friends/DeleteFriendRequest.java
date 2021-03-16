package com.net.network.chick.friends;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.DeleteFriendResult;

import io.reactivex.Observable;

/**
 * Song Jian
 */
public class DeleteFriendRequest extends RxRequest<DeleteFriendResult.Response, ApiInterface> {

    private String mTargetUserId;
    //0 普通 1私聊删除
    private int source;

    public DeleteFriendRequest(String targetUserId, int source) {
        super(DeleteFriendResult.Response.class, ApiInterface.class);
        mTargetUserId = targetUserId;
        this.source = source;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<DeleteFriendResult.Response> loadDataFromNetwork() throws Exception {
        return getService().deleteFriend(mTargetUserId, source);
    }

}
