package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.RoomWeekRank;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/9/9
 * 知心周榜
 */
public class RoomWeekRankRequest extends RxRequest<RoomWeekRank.Response, ApiInterface> {
    private String roomId;
    private int nextPageNo = 1;

    public RoomWeekRankRequest(String roomId, int nextPageNo) {
        super(RoomWeekRank.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.nextPageNo = nextPageNo;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomWeekRank.Response> loadDataFromNetwork() throws Exception {
        return getService().getLiveWeekRank(roomId, nextPageNo);
    }
}
