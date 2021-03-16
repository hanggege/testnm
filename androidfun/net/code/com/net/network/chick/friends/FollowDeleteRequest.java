package com.net.network.chick.friends;


import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/2/21
 * desc   : 取消关注
 */
public class FollowDeleteRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    private int followId;

    //    private int source;//来源 0-个人主页、1-直播间拉起的知心达人资料框关注
//    private String fromId;//目前是roomId（在直播间内关注）、user_id（知心达人id，知心达人个人主页关注
    public FollowDeleteRequest(int followId) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.followId = followId;
//        this.source = source;
//        this.fromId = fromId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().followDelete(followId);

    }
}