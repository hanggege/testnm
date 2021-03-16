package com.net.network.chick.report;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.report.ChatC2CUseInfo;

import io.reactivex.Observable;

/**
 * Created by 杨强彪 on 2016/5/30.
 *
 * @描述：
 */
public class UserInfoRequest extends RxRequest<ChatC2CUseInfo.Response, ApiInterface> {

    private String targetUserId;

    public UserInfoRequest(String targetUserId) {
        super(ChatC2CUseInfo.Response.class, ApiInterface.class);
        this.targetUserId = targetUserId;

    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ChatC2CUseInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().userInfo(targetUserId);

    }
}