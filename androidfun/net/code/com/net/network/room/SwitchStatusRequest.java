package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-26
 */
public class SwitchStatusRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    private String roomId;
    private boolean isOpen;

    public SwitchStatusRequest(String roomId, boolean isOpen) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.isOpen = isOpen;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().switchStatus(roomId, isOpen);
    }
}
