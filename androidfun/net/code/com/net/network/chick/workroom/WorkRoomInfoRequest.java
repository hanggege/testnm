package com.net.network.chick.workroom;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.workroom.WorkRoomInfo;

import io.reactivex.Observable;

/**
 *
 * @author Created by lenna on 2020/7/22
 * 工作室信息请求
 */
public class WorkRoomInfoRequest extends RxRequest<WorkRoomInfo.Response, ApiInterface> {
    public WorkRoomInfoRequest() {
        super(WorkRoomInfo.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<WorkRoomInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getWorkRoomInfo();
    }
}
