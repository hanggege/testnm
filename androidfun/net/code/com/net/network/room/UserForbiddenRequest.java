package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-26
 */
public class UserForbiddenRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public String roomId;
    public int targetUserId;
    public int forbidden;//1.闭麦 0.解禁
    public String type;// 1:mic 闭麦 2：mute 禁言 3video 开视频/关视频
    public int fromUserId;
    public String reason = "";

    public UserForbiddenRequest() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().userForbidden(roomId, targetUserId, forbidden, type, fromUserId, reason);
    }
}
