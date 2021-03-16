package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.RoomGetToken;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-10
 */
public class RoomGetTokenRequest extends RxRequest<RoomGetToken.Response, ApiInterface> {

    public String tokenType;//token类型,CREATE_ROOM-创建普通房间,JOIN_ROOM-加入房间,BEGIN_UPSTREAM-连线"
    public String roomId;//房间id,创建时不需要传,

    public RoomGetTokenRequest() {
        super(RoomGetToken.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomGetToken.Response> loadDataFromNetwork() throws Exception {
        return getService().RoomGetToken(tokenType, roomId);
    }
}
