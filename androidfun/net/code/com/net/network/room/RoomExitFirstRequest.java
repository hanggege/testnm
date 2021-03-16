package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.RoomExit;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public class RoomExitFirstRequest extends RxRequest<RoomExit.Response, ApiInterface> {
    private String roomId;

    public RoomExitFirstRequest(String roomId) {
        super(RoomExit.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomExit.Response> loadDataFromNetwork() throws Exception {
        return getService().againExitRoomFirst(roomId);
    }
}
