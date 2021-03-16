package com.net.network.chick.workroom;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * SendAnalystsRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-10
 */
public class SendAnalystsRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    private int userId;
    private int targetUserId;

    /**
     * @param userId 绑定销售ID
     * @param targetUserId 点的头像的人的id
     */
    public SendAnalystsRequest(int userId, int targetUserId) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.userId = userId;
        this.targetUserId = targetUserId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().sendAnalystsInfo(userId, targetUserId);
    }
}
