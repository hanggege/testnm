package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.RefreshRoomWeekRank;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/9/9
 * 周榜请求
 */
public class RoomWeekRankRefreshRequest extends RxRequest<RefreshRoomWeekRank.Response, ApiInterface> {
    private String roomId;

    public RoomWeekRankRefreshRequest(String roomId) {
        super(RefreshRoomWeekRank.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RefreshRoomWeekRank.Response> loadDataFromNetwork() throws Exception {
        return getService().refreshWeekRank(roomId);
    }
}
