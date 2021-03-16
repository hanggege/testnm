package com.net.network.chick.workroom;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/7/30
 * 编辑工作室成员信息请求
 */
public class EditMemberIntroductionRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private int targetUserId;
    private String introduction;

    public EditMemberIntroductionRequest(int targetUserId, String introduction) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.targetUserId = targetUserId;
        this.introduction = introduction;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().editMemberInfo(targetUserId, introduction);
    }
}
