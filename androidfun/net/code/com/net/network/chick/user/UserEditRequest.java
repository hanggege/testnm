package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 更改用户资料
 */
public class UserEditRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    //用户性别(0女1男 -1未填写)
    public int gender = -1;
    //生日
    public String birthYear;
    //头像
    public String avatar;
    //昵称
    public String nickname;
    //感兴趣的内容
    public String interestId;


    public UserEditRequest() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    public String toString() {
        return "UserEditRequest{" +
                "gender=" + gender +
                ", birthYear='" + birthYear + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", interestId='" + interestId + '\'' +
                '}';
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().userEdit(gender == -1 ? "" : String.valueOf(gender), birthYear,
                avatar, nickname,
                interestId);
    }


}
