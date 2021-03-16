package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.AccountLoginResult;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/2/20
 * desc   : 更新绑定手机
 */

public class UpdatePhoneRequest extends RxRequest<AccountLoginResult.Response, ApiInterface> {

    private String phoneNo = "";
    private String mobileCode = "";
    private int fromPage;

    public UpdatePhoneRequest(String phoneNo, String mobileCode, int fromPage) {
        super(AccountLoginResult.Response.class, ApiInterface.class);
        this.phoneNo = phoneNo;
        this.mobileCode = mobileCode;
        this.fromPage = fromPage;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<AccountLoginResult.Response> loadDataFromNetwork() throws Exception {
        return getService().updatePhone(phoneNo, mobileCode,fromPage);
    }

}
