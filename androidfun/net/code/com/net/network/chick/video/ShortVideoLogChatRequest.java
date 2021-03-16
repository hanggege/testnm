package com.net.network.chick.video;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/8/26
 */
public class ShortVideoLogChatRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private int publisherWorksId;

    public ShortVideoLogChatRequest(int publisherWorksId) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.publisherWorksId = publisherWorksId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().logChat(publisherWorksId);
    }
}
