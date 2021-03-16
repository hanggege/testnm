package com.net.network.share;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.share.LivingShareResult;

import io.reactivex.Observable;

/**
 * 直播间发送消息
 */
public class LivingShareRequest extends RxRequest<LivingShareResult.Response, ApiInterface> {

    private String roomId;

    public LivingShareRequest(String roomId) {
        super(LivingShareResult.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<LivingShareResult.Response> loadDataFromNetwork() throws Exception {
        return getService().livingShare(roomId);
    }
}