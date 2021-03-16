package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.PeopleCount;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-14
 */
public class PeopleCountRequest extends RxRequest<PeopleCount.Response, ApiInterface> {

    private String roomId;

    public PeopleCountRequest(String roomId) {
        super(PeopleCount.Response.class, ApiInterface.class);
        this.roomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<PeopleCount.Response> loadDataFromNetwork() throws Exception {
        return getService().PeopleCount(roomId);
    }
}
