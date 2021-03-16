package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.CheckAccountResult;
import com.mei.orc.john.model.JohnUser;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;


/**
 * Created by steven on 15/4/27.
 */
public class ACCOUNT_Check_Account_Name_Request extends RxRequest<CheckAccountResult.Response, JohnInterface> {

    private String account_name = "";

    @Override
    public String toString() {
        return "ACCOUNT_get_checksum_Request{" +
                "account_name='" + account_name + '\'' +
                '}';
    }

    public ACCOUNT_Check_Account_Name_Request(String account_name) {
        super(CheckAccountResult.Response.class, JohnInterface.class);
        this.account_name = account_name;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<CheckAccountResult.Response> loadDataFromNetwork() throws Exception {
        return getService().ACCOUNT_check_account_name(account_name, JohnUser.getSharedUser().getPhoneSN());
    }
}
