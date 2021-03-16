package com.net.network.chick.message;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.message.MessageBanner;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/2/25
 */
public class MessageBannerRequest extends RxRequest<MessageBanner.Response, ApiInterface> {

    public MessageBannerRequest() {
        super(MessageBanner.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<MessageBanner.Response> loadDataFromNetwork() throws Exception {
        return getService().MessageBanner();
    }
}
