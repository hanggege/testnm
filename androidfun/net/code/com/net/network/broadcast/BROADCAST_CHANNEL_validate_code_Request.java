package com.net.network.broadcast;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.broadcast.BROADCAST_CHANNEL_validate_code;

import io.reactivex.Observable;

/**
 * Created by joker on 15/12/21.
 */
public class BROADCAST_CHANNEL_validate_code_Request extends RxRequest<BROADCAST_CHANNEL_validate_code.Response, ApiInterface> {

    private String mCode;
    private String mHistory;
    private int mBroadcastId;

    public BROADCAST_CHANNEL_validate_code_Request(int broadcastId, String code) {
        super(BROADCAST_CHANNEL_validate_code.Response.class, ApiInterface.class);
        mBroadcastId = broadcastId;
        mCode = code;
    }

    public BROADCAST_CHANNEL_validate_code_Request(int broadcastId, String code, String history) {
        super(BROADCAST_CHANNEL_validate_code.Response.class, ApiInterface.class);
        mBroadcastId = broadcastId;
        mHistory = history;
        mCode = code;
    }

    @Override
    protected Observable<BROADCAST_CHANNEL_validate_code.Response> loadDataFromNetwork() throws Exception {
        return getService().BROADCAST_CHANNEL_validate_code(mBroadcastId, mHistory, mCode);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

}
