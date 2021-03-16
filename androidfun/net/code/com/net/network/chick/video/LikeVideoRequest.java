package com.net.network.chick.video;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/8/19
 */
public class LikeVideoRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private int seqId;
    private boolean isLike;

    public LikeVideoRequest(int seqId, boolean isLike) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.seqId = seqId;
        this.isLike = isLike;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().likeVideo(seqId, isLike);
    }
}
