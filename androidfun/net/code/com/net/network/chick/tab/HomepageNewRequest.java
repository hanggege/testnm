package com.net.network.chick.tab;


import com.mei.orc.Cxt;
import com.mei.orc.ext.DimensionsKt;
import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.room.RoomList;

import cn.dreamtobe.kpswitch.util.StatusBarHeightUtil;
import io.reactivex.Observable;

/**
 * Created by 杨强彪 on 2016/5/30.
 */
public class HomepageNewRequest extends RxRequest<RoomList.Response, ApiInterface> {
    private int pageNo;
    private String version;

    public HomepageNewRequest(int pageNo) {
        super(RoomList.Response.class, ApiInterface.class);
        this.pageNo = pageNo;
    }

    public HomepageNewRequest(int pageNo, String version) {
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
        int barHeight = StatusBarHeightUtil.getStatusBarHeight(Cxt.get());
        float height = DimensionsKt.px2dip(Cxt.get(), barHeight);
        return getService().homepageNew(pageNo, 10, String.valueOf(height), version);

    }
}