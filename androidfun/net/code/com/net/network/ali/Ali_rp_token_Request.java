package com.net.network.ali;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.ali.AliRpToken;

import io.reactivex.Observable;

/**
 * Created by joker on 15/12/21.
 */
public class Ali_rp_token_Request extends RxRequest<AliRpToken.Response, ApiInterface> {

    private String mRealName;
    private String mIdCard;

    public Ali_rp_token_Request(String realName, String idCard) {
        super(AliRpToken.Response.class, ApiInterface.class);
        mRealName = realName;
        mIdCard = idCard;
    }

    @Override
    protected Observable<AliRpToken.Response> loadDataFromNetwork() throws Exception {
        return getService().Ali_rp_token(mRealName, mIdCard);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

}
