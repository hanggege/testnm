package com.net.network.chick.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/7/28
 */
public class RoomOpenRedPacketRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private String roomId;

    public RoomOpenRedPacketRequest(String roomId) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().openRedPacket(roomId);
    }
}
