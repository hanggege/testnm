package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.QueueTop;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-14
 */
public class QueueTopRequest extends RxRequest<QueueTop.Response, ApiInterface> {

    private String roomId;

    public QueueTopRequest(String roomId) {
        super(QueueTop.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<QueueTop.Response> loadDataFromNetwork() throws Exception {
        return getService().queueTop(roomId);
    }
}
