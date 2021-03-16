package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.RoomInfoResponse;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-12
 */
public class RoomStartRequest extends RxRequest<RoomInfoResponse.Response, ApiInterface> {

    private String title;
    private int tagId;

    public RoomStartRequest(String title, int tagId) {
        super(RoomInfoResponse.Response.class, ApiInterface.class);
        this.title = title;
        this.tagId = tagId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomInfoResponse.Response> loadDataFromNetwork() throws Exception {
        return getService().roomStart(title, tagId);
    }
}
