package com.net.network.chick.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.RoomRedPacket;
import com.net.model.room.RoomRedPacketResult;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/7/27
 */
public class RoomRedPacketStatusRequest extends RxRequest<RoomRedPacketResult.Response, ApiInterface> {
    private String roomId;

    public RoomRedPacketStatusRequest(String roomId) {
        super(RoomRedPacketResult.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomRedPacketResult.Response> loadDataFromNetwork() throws Exception {
        return getService().getRedPacketStatus(roomId);
    }
}
