package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * Created by hang on 2020/6/12.
 */
public class RoomInfoModifyRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public String roomId;//房间id
    public String title;
    public int proCateId;

    public RoomInfoModifyRequest() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().roomInfoModify(roomId, title, proCateId);
    }
}
