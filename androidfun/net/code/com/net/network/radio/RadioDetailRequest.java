package com.net.network.radio;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.radio.RadioDetailInfo;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/9/23
 * 电台详情请求
 */
public class RadioDetailRequest extends RxRequest<RadioDetailInfo.Response, ApiInterface> {
    private boolean firstOpen;

    public RadioDetailRequest(boolean firstOpen) {
        super(RadioDetailInfo.Response.class, ApiInterface.class);
        this.firstOpen = firstOpen;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RadioDetailInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getRadioDetailData(firstOpen);
    }
}
