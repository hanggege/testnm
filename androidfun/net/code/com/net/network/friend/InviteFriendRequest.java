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
public class InviteFriendRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    private int inviteUser;
    //0一般加好友  1私聊加好友
    private int source;

    public InviteFriendRequest(int inviteUser, int source) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.inviteUser = inviteUser;
        this.source = source;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().InviteFriend(inviteUser, source);
    }
}
