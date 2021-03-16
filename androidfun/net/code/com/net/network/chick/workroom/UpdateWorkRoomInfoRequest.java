package com.net.network.chick.workroom;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/7/23
 * 更新工作室信息请求
 */
public class UpdateWorkRoomInfoRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private String introduction;
    private String tags;
    private String nickName;
    private String avatar;

    public UpdateWorkRoomInfoRequest(String avatar,String nickName, String tags, String introduction) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.introduction = introduction;
        this.tags = tags;
        this.nickName = nickName;
        this.avatar = avatar;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().editWorkRoomInfo(avatar,nickName, tags, introduction);
    }
}
