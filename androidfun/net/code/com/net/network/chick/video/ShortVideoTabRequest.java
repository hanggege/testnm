package com.net.network.chick.video;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.video.ShortVideoTab;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/9/2
 * 短视频tab请求
 */
public class ShortVideoTabRequest extends RxRequest<ShortVideoTab.Response, ApiInterface> {
    public ShortVideoTabRequest() {
        super(ShortVideoTab.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ShortVideoTab.Response> loadDataFromNetwork() throws Exception {
        return getService().getShortVideoTabData();
    }
}
