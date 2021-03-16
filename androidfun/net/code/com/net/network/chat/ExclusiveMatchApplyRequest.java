package com.net.network.chat;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chat.ExclusiveApply;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/21
 */
public class ExclusiveMatchApplyRequest extends RxRequest<ExclusiveApply.Response, ApiInterface> {
    public long couponNum;
    public int categoryId;
    public int videoMode;
    public int publisherId;

    public ExclusiveMatchApplyRequest() {
        super(ExclusiveApply.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ExclusiveApply.Response> loadDataFromNetwork() throws Exception {
        return getService().exclusiveMatchApply(couponNum, categoryId, videoMode, publisherId);
    }
}
