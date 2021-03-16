package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/9
 */
public class InvisibleSetRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    // 是否隐身
    private boolean roomInvisible;

    public InvisibleSetRequest(boolean roomInvisible) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.roomInvisible = roomInvisible;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().invisibleSet(roomInvisible ? 1 : 0);
    }
}
