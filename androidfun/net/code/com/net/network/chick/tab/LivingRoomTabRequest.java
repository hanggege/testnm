package com.net.network.chick.tab;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.tab.TabBar;

import io.reactivex.Observable;

/**
 * LivingRoomTabRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-23
 */
public class LivingRoomTabRequest extends RxRequest<TabBar.Response, ApiInterface> {

    public LivingRoomTabRequest() {
        super(TabBar.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<TabBar.Response> loadDataFromNetwork() throws Exception {
        return getService().getLivingRoomTab();
    }
}
