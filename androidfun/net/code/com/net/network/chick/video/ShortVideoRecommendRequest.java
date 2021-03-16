package com.net.network.chick.video;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.video.ShortVideoInfo;

import io.reactivex.Observable;

/**
 * 短视频推荐
 *
 * @author Created by lenna on 2020/9/2
 */
public class ShortVideoRecommendRequest extends RxRequest<ShortVideoInfo.Response, ApiInterface> {
    private String nextStartKey;

    public ShortVideoRecommendRequest(String nextStartKey) {
        super(ShortVideoInfo.Response.class, ApiInterface.class);
        this.nextStartKey = nextStartKey;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ShortVideoInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getRecommendShortVideoList(nextStartKey);
    }
}
