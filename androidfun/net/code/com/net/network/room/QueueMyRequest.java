package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.QueueMy;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-14
 */
public class QueueMyRequest extends RxRequest<QueueMy.Response, ApiInterface> {
    private String roomId;

    public QueueMyRequest(String roomId) {
        super(QueueMy.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<QueueMy.Response> loadDataFromNetwork() throws Exception {
        return getService().queueMy(roomId);
    }
}
