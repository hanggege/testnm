package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.RoomStatus;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-15
 */
public class RoomStatusRequest extends RxRequest<RoomStatus.Response, ApiInterface> {

    private String roomId;

    public RoomStatusRequest(String roomId) {
        super(RoomStatus.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomStatus.Response> loadDataFromNetwork() throws Exception {
        return getService().RoomStatus(roomId);
    }
}
