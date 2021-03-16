package com.net.network.live;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * Created by joker on 16/7/14.
 */
public class Reservation_Broadcast_Request extends RxRequest<Empty_data.Response, ApiInterface> {

    private int broadcast_id;
    private int is_notify;  //预约:1, 或 取消预约:0 ,2:表示查询该是否有预约权限

    @Override
    public String toString() {
        return "Reservation_Broadcast_Request{" +
                "broadcast_id=" + broadcast_id +
                "is_notify=" + is_notify +
                '}';
    }

    public Reservation_Broadcast_Request(int broadcast_id) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.broadcast_id = broadcast_id;
        is_notify = 1;
    }

    public Reservation_Broadcast_Request(int broadcast_id, int is_notify) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.broadcast_id = broadcast_id;
        this.is_notify = is_notify;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().Reservation_Broadcast(broadcast_id, is_notify);
    }
}
