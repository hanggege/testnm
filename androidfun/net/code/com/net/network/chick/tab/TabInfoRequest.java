package com.net.network.chick.tab;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.tab.TabBar;

import io.reactivex.Observable;

/**
 * Created by zzw on 2019-12-18
 * Des:
 */
public class TabInfoRequest extends RxRequest<TabBar.Response, ApiInterface> {
    private String version;
    private String specifications;

    public TabInfoRequest(String version, String specifications) {
        super(TabBar.Response.class, ApiInterface.class);
        this.version = version;
        this.specifications = specifications;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<TabBar.Response> loadDataFromNetwork() throws Exception {
        return getService().tabInfo(version, specifications);

    }
}