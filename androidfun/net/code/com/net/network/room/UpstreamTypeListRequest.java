package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.UpstreamTypeList;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-14
 */
public class UpstreamTypeListRequest extends RxRequest<UpstreamTypeList.Response, ApiInterface> {
    private String roomId;

    public UpstreamTypeListRequest(String roomId) {
        super(UpstreamTypeList.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<UpstreamTypeList.Response> loadDataFromNetwork() throws Exception {
        return getService().upstreamTypeList(roomId);
    }
}
