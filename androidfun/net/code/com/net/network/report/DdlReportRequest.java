package com.net.network.report;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/9/28
 * ddl 上传请求
 */
public class DdlReportRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private String content;
    private String webViewUa;

    public DdlReportRequest(String content, String webViewUa) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.content = content;
        this.webViewUa = webViewUa;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().ddlReportServer(content, webViewUa);
    }
}
