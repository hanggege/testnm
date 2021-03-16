package com.net.network.chat;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chat.ExclusiveHandle;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/21
 */
public class ExclusiveHandleRequest extends RxRequest<ExclusiveHandle.Response, ApiInterface> {
    public int result;
    public int userId;

    public ExclusiveHandleRequest() {
        super(ExclusiveHandle.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ExclusiveHandle.Response> loadDataFromNetwork() throws Exception {
        return getService().exclusiveHandle(result, userId);
    }
}
