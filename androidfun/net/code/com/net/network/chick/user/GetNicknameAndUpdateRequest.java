package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.user.NicknameResponse;

import io.reactivex.Observable;

/**
 * 更改用户资料
 */
public class GetNicknameAndUpdateRequest extends RxRequest<NicknameResponse.Response, ApiInterface> {

    public GetNicknameAndUpdateRequest() {
        super(NicknameResponse.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<NicknameResponse.Response> loadDataFromNetwork() throws Exception {
        return getService().getNickNameAndUpdate();
    }


}
