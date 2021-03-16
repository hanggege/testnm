package com.net.network.chick.tab;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.room.RoomList;

import io.reactivex.Observable;

/**
 * Created by 杨强彪 on 2016/5/30.
 */
public class HomepageIndexRequest extends RxRequest<RoomList.Response, ApiInterface> {
    private int pageNo;
    private String version;

    public HomepageIndexRequest(int pageNo) {
        super(RoomList.Response.class, ApiInterface.class);
        this.pageNo = pageNo;
    }

    public HomepageIndexRequest(int pageNo, String version) {
        super(RoomList.Response.class, ApiInterface.class);
        this.pageNo = pageNo;
        this.version = version;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomList.Response> loadDataFromNetwork() throws Exception {
        return getService().homepageIndex(pageNo, 10, version);
    }
}