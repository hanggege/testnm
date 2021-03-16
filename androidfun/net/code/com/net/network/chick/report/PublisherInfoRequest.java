package com.net.network.chick.report;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.report.ChatC2CPubInfo;

import io.reactivex.Observable;

/**
 * Created by 杨强彪 on 2016/5/30.
 *
 * @描述：
 */
public class PublisherInfoRequest extends RxRequest<ChatC2CPubInfo.Response, ApiInterface> {

    private String targetUserId;

    public PublisherInfoRequest(String targetUserId) {
        super(ChatC2CPubInfo.Response.class, ApiInterface.class);
        this.targetUserId = targetUserId;

    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ChatC2CPubInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().publisherInfo(targetUserId);

    }
}