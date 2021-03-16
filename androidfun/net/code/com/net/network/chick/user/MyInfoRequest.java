package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.JohnUser;
import com.net.ApiInterface;
import com.net.model.chick.user.ChickUserInfo;

import io.reactivex.Observable;

/**
 * Created by zzw on 2019-12-09
 * Des:
 */
public class MyInfoRequest extends RxRequest<ChickUserInfo.Response, ApiInterface> {

    public int login_user_id;
    public String session_id;

    public MyInfoRequest() {
        super(ChickUserInfo.Response.class, ApiInterface.class);
        login_user_id = JohnUser.getSharedUser().getUserID();
        session_id = JohnUser.getSharedUser().getSessionID();
    }

    public MyInfoRequest(int login_user_id, String session_id) {
        super(ChickUserInfo.Response.class, ApiInterface.class);
        this.login_user_id = login_user_id;
        this.session_id = session_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ChickUserInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().myInfo(login_user_id, session_id);
    }
}