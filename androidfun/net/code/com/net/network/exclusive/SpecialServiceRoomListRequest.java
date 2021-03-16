package com.net.network.exclusive;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.SpecialServicePageList;

import io.reactivex.Observable;

/**
 * 获取已购买专属服务数据
 *
 * @author Created by lenna on 2020/6/3
 */
public class SpecialServiceRoomListRequest extends RxRequest<SpecialServicePageList.Response, ApiInterface> {
    private int mUserId;
    private int mPublisherId;
    private String mRoomId;

    public SpecialServiceRoomListRequest(int userId, int publisherId, String roomId) {
        super(SpecialServicePageList.Response.class, ApiInterface.class);
        mUserId = userId;
        mPublisherId = publisherId;
        mRoomId = roomId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<SpecialServicePageList.Response> loadDataFromNetwork() throws Exception {
        return getService().specialServiceRoomList(mUserId, mPublisherId, mRoomId);
    }
}
