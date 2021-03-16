package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.RoomUserRecent;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-26
 */
public class RoomUserRecentRequest extends RxRequest<RoomUserRecent.Response, ApiInterface> {

    private String roomId;
    private int topN;//前多少位排麦用户
    private String type;//"join": 最近加入的用户"chat": 最近发言的用户

    public RoomUserRecentRequest(String roomId, int topN, String type) {
        super(RoomUserRecent.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.topN = topN;
        this.type = type;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomUserRecent.Response> loadDataFromNetwork() throws Exception {
        return getService().roomUserRecent(roomId, topN, type);
    }
}
