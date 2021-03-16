package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.RoomUserQueue;
import com.net.model.room.RoomUserTypeEnum;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public class RoomUserQueueRequest extends RxRequest<RoomUserQueue.Response, ApiInterface> {

    private String roomId;
    private RoomUserTypeEnum type;
    private int pageNo = 1;

    public RoomUserQueueRequest(String roomId, RoomUserTypeEnum type) {
        super(RoomUserQueue.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.type = type;
    }

    public RoomUserQueueRequest(String roomId, RoomUserTypeEnum type, int pageNo) {
        super(RoomUserQueue.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.type = type;
        this.pageNo = pageNo;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomUserQueue.Response> loadDataFromNetwork() throws Exception {
        return getService().roomUserQueue(roomId, type, pageNo);
    }
}
