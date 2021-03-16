package com.net.network.report;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/10/28
 * 勋章请求
 */
public class HonorDialogReportRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private String msgId;

    public HonorDialogReportRequest(String msgId) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.msgId = msgId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().reportHonorDialogMsg(msgId);
    }
}
