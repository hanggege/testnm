package com.net.network.chick.report;


import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;
import com.net.model.chick.report.ReportBean;

import io.reactivex.Observable;

/**
 * Created by 杨强彪 on 2016/5/30.
 *
 * @描述：
 */
public class ReportRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    private ReportBean reportBean = new ReportBean();

    public ReportRequest(ReportBean reportBean) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.reportBean = reportBean;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().report(reportBean);

    }
}