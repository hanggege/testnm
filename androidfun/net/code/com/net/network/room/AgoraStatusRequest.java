package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.AgoraStatus;
import com.net.model.room.RoomType;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-26
 */
public class AgoraStatusRequest extends RxRequest<AgoraStatus.Response, ApiInterface> {

    private String roomId;
    private int status;//111:用户将角色切换为知心达人。112:用户将角色切换为观众。113:上麦中掉线。114:上麦连接时掉线
    private RoomType roomType;

    public AgoraStatusRequest(String roomId, int status, RoomType roomType) {
        super(AgoraStatus.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.status = status;
        this.roomType = roomType;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<AgoraStatus.Response> loadDataFromNetwork() throws Exception {
        return getService().AgoraStatus(roomId, status, roomType.name(),System.currentTimeMillis() + "");
    }
}
