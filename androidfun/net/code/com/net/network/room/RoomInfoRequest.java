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
public class RoomInfoRequest extends RxRequest<RoomInfoResponse.Response, ApiInterface> {

    public String roomId;
    public String title;
    public String tag;
    public String from;

    public RoomInfoRequest(String roomId, String title, String tag, String from) {
        super(RoomInfoResponse.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.title = title;
        this.tag = tag;
        this.from = from;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomInfoResponse.Response> loadDataFromNetwork() throws Exception {
        return getService().RoomInfo(roomId, title, tag, from);
    }
}
