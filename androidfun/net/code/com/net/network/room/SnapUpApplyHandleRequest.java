package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chat.ExclusiveHandle;

import io.reactivex.Observable;

/**
 * SnapUpApplyHandleRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-10-16
 */
public class SnapUpApplyHandleRequest extends RxRequest<ExclusiveHandle.Response, ApiInterface> {
    public int userId;
    public String roomId;

    public SnapUpApplyHandleRequest() {
        super(ExclusiveHandle.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ExclusiveHandle.Response> loadDataFromNetwork() throws Exception {
        return getService().SnapUpHandle(userId, roomId);
    }
}