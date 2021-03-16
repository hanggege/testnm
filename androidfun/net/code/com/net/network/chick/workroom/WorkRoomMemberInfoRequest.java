package com.net.network.chick.workroom;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.workroom.WorkRoomMemberListResponse;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/7/29
 * 工作室成员信息请求
 */
public class WorkRoomMemberInfoRequest extends RxRequest<WorkRoomMemberListResponse.Response, ApiInterface> {
    public WorkRoomMemberInfoRequest() {
        super(WorkRoomMemberListResponse.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<WorkRoomMemberListResponse.Response> loadDataFromNetwork() throws Exception {
        return getService().getWorkRoomMemberInfo();
    }
}
