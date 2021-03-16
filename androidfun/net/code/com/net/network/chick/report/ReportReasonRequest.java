package com.net.network.chick.report;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.report.ReasonBean;

import io.reactivex.Observable;

/**
 * Created by 杨强彪 on 2016/5/30.
 *
 * @描述：
 */
public class ReportReasonRequest extends RxRequest<ReasonBean.Response, ApiInterface> {

    public ReportReasonRequest() {
        super(ReasonBean.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ReasonBean.Response> loadDataFromNetwork() throws Exception {
        return getService().reason();
    }
}