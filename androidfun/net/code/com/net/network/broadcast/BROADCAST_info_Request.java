package com.net.network.broadcast;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.broadcast.BROADCAST_info;

import io.reactivex.Observable;

/**
 * Created by Joker on 2015/8/20.
 */
public class BROADCAST_info_Request extends RxRequest<BROADCAST_info.Response, ApiInterface> {
    private int broadcast_id;
    private String room_id;

    @Override
    public String toString() {
        return "BROADCAST_info_Request{" +
                "broadcast_id=" + broadcast_id +
                '}';
    }

    public BROADCAST_info_Request(int broadcast_id, String room_id) {
        super(BROADCAST_info.Response.class, ApiInterface.class);
        this.broadcast_id = broadcast_id;
        this.room_id = room_id;
    }

    public BROADCAST_info_Request(int broadcast_id) {
        super(BROADCAST_info.Response.class, ApiInterface.class);
        this.broadcast_id = broadcast_id;
        this.room_id = "";
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<BROADCAST_info.Response> loadDataFromNetwork() throws Exception {
        return getService().BROADCAST_info(broadcast_id, room_id);
    }
}
