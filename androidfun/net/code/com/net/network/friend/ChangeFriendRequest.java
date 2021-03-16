package com.net.network.friend;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-15
 */
public class ChangeFriendRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    //1 同意  2拒绝
    private int status;
    //0一般加好友  1私聊加好友
    private int source;
    private String friendId;

    public ChangeFriendRequest(int status, int source, String friendId) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.status = status;
        this.source = source;
        this.friendId = friendId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().changeFriend(status, source, friendId);
    }
}
