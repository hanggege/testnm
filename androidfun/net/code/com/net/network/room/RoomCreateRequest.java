package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-10
 */
public class RoomCreateRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public String roomId;
    public String roomType = "COMMON";//COMMON-普通相亲,EXCLUSIVE-专属相亲

    public RoomCreateRequest() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().RoomCreate(roomId, roomType);
    }
}