package com.net.network.bag;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.bag.NewPeopleGiftBagInfo;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/11/17
 */
public class NewPeopleGiftBagRequest extends RxRequest<NewPeopleGiftBagInfo.Response, ApiInterface> {
    public NewPeopleGiftBagRequest() {
        super(NewPeopleGiftBagInfo.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<NewPeopleGiftBagInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getNewPeopleGiftBag();
    }
}
