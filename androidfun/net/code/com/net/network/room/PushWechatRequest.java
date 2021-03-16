package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-15
 */
public class PushWechatRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    private String roomId;
    private int userId;

    public PushWechatRequest(String roomId, int userId) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.userId = userId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().PushWechat(roomId, userId);
    }
}
