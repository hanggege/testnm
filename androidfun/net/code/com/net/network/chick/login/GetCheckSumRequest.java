package com.net.network.chick.login;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.JohnUser;
import com.net.ApiInterface;
import com.net.model.chick.login.CheckSunResult;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class GetCheckSumRequest extends RxRequest<CheckSunResult.Response, ApiInterface> {

    private String account = "";
    private String machineCode = "";

    public GetCheckSumRequest(String account) {
        super(CheckSunResult.Response.class, ApiInterface.class);
        this.account = account;
        this.machineCode = JohnUser.getSharedUser().phoneSN;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<CheckSunResult.Response> loadDataFromNetwork() throws Exception {
        return getService().getChecksum(account, machineCode);
    }

}
