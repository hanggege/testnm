package com.net.network.chick.video;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.video.ShortVideoCompletionInfo;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/8/26
 */
public class ShortVideoCompletionRequest extends RxRequest<ShortVideoCompletionInfo.Response, ApiInterface> {
    private String seqId;
    private long duration;

    public ShortVideoCompletionRequest(String seqId, long duration) {
        super(ShortVideoCompletionInfo.Response.class, ApiInterface.class);
        this.seqId = seqId;
        this.duration = duration;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ShortVideoCompletionInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().ShortVideoCompletion(seqId, duration);
    }
}
