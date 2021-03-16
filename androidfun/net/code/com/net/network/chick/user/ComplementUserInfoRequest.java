package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 完善资料
 */
public class ComplementUserInfoRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    //用户性别(0女1男 -1未填写)
    public int gender = -1;
    //生日
    public String birthYear;
    //头像
    public String avatar;
    //昵称
    public String nickname;
    //兴趣id
    public String interestIds;

    public int login_user_id;
    public String session_id;


    public ComplementUserInfoRequest(int login_user_id, String session_id) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.login_user_id = login_user_id;
        this.session_id = session_id;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().complementUserInfo(gender == -1 ? "" : String.valueOf(gender), birthYear,
                avatar, nickname,
                interestIds,
                login_user_id > 0 ? String.valueOf(login_user_id) : "", session_id);
    }


}
