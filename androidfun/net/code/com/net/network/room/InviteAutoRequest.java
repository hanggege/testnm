package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-14
 */
public class InviteAutoRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public String roomId;
    public int gender;
    public boolean use;

    public InviteAutoRequest() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().InviteAuto(roomId, gender, use ? 1 : 0);
    }
}
