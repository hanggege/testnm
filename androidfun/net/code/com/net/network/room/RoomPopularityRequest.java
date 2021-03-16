package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.RoomPopularity;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public class RoomPopularityRequest extends RxRequest<RoomPopularity.Response, ApiInterface> {
    private String roomId;

    public RoomPopularityRequest(String roomId) {
        super(RoomPopularity.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomPopularity.Response> loadDataFromNetwork() throws Exception {
        return getService().roomPopularity(roomId);
    }
}
