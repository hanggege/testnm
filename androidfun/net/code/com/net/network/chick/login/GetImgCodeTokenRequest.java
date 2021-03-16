package com.net.network.chick.login;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.login.ImgCodeTokenResult;

import io.reactivex.Observable;

/**
 * Created by zzw on 2019-12-26
 * Des:
 */
public class GetImgCodeTokenRequest extends RxRequest<ImgCodeTokenResult.Response, ApiInterface> {

    private String phoneNo = "";

    public GetImgCodeTokenRequest(String phoneNo) {
        super(ImgCodeTokenResult.Response.class, ApiInterface.class);
        this.phoneNo = phoneNo;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ImgCodeTokenResult.Response> loadDataFromNetwork() throws Exception {
        return getService().getImgCodeToken(phoneNo);
    }

}