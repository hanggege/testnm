package com.net.network.radio;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.radio.RadioFavoriteListInfo;

import io.reactivex.Observable;

/**
 * RadioFavoriteListRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-24
 */
public class RadioFavoriteListRequest extends RxRequest<RadioFavoriteListInfo.Response, ApiInterface> {

    public RadioFavoriteListRequest() {
        super(RadioFavoriteListInfo.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RadioFavoriteListInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getRadioFavoriteList();
    }
}
