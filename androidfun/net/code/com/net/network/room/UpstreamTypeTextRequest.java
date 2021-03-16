package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.UpstreamTypeText;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-14
 */
public class UpstreamTypeTextRequest extends RxRequest<UpstreamTypeText.Response, ApiInterface> {
    private String roomId;

    public UpstreamTypeTextRequest(String roomId) {
        super(UpstreamTypeText.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<UpstreamTypeText.Response> loadDataFromNetwork() throws Exception {
        return getService().upstreamTypeText(roomId);
    }
}
