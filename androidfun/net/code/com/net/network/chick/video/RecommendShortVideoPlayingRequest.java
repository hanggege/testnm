package com.net.network.chick.video;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/9/2
 */
public class RecommendShortVideoPlayingRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private String seqId;

    public RecommendShortVideoPlayingRequest(String seqId) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.seqId = seqId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().recommendPlaying(seqId);
    }
}
