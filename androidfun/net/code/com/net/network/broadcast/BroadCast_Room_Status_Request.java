package com.net.network.broadcast;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.broadcast.BroadCast_Room_Status;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/1/15
 */
public class BroadCast_Room_Status_Request extends RxRequest<BroadCast_Room_Status.Response, ApiInterface> {

    private String room_id;

    public BroadCast_Room_Status_Request(String room_id) {
        super(BroadCast_Room_Status.Response.class, ApiInterface.class);
        this.room_id = room_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<BroadCast_Room_Status.Response> loadDataFromNetwork() throws Exception {
        return getService().BroadCast_Room_Status(room_id);
    }
}
