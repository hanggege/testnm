package com.net.network.chick.find;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.find.FindTab;

import io.reactivex.Observable;

/**
 * FindTabRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-22
 */
public class FindTabRequest extends RxRequest<FindTab.Response, ApiInterface> {

    public FindTabRequest() {
        super(FindTab.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<FindTab.Response> loadDataFromNetwork() throws Exception {
        return getService().getFindPageTab();
    }
}
