package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.user.NicknameBatchInfo;

import io.reactivex.Observable;

/**
 * 更改用户资料
 */
public class GetNicknameBatchRequest extends RxRequest<NicknameBatchInfo.Response, ApiInterface> {

    private int userId;
    private String sessionId;

    public GetNicknameBatchRequest(int userId, String sessionId) {
        super(NicknameBatchInfo.Response.class, ApiInterface.class);
        this.userId = userId;
        this.sessionId = sessionId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<NicknameBatchInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getRandomNicknameBatch(userId, sessionId);
    }


}
